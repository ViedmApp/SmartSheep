package cl.uach.inf.smartsheep.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cl.uach.inf.smartsheep.MainActivity;
import cl.uach.inf.smartsheep.R;
import cl.uach.inf.smartsheep.data.model.Login;
import cl.uach.inf.smartsheep.data.model.User;
import cl.uach.inf.smartsheep.data.service.UserClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://sheep-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();

    UserClient userClient = retrofit.create(UserClient.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }

                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    //updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);
                //Complete and destroy login activity once successful
                //goMainScreen(loginResult.getSuccess());
                //finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);
                /*loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());*/
                login(usernameEditText.getText().toString(), passwordEditText.getText().toString() );

            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void goMainScreen(String email){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Username",email);
        startActivity(intent);
    }

    //Aqui pa abajo
    public static String token;
    private void login(final String email, String password){
        Login login = new Login(email,
                password);
        Call<User> call = userClient.login(login);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    token = response.body().getToken();

                    //Enviar token a MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("Token", token);
                    intent.putExtra("Username", email);

                    SharedPreferences.Editor editor = getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                            .edit();
                    editor.putString("TOKEN", token).apply();
                    editor.putString("USERNAME", email).apply();

                    startActivity(intent);
                    finish();
                    //getPredio();
                    //Cambiar de pantalla a Main Activity
                    //goMainScreen(email);
                    //Se debe borrar
                    //Toast.makeText(LoginActivity.this, "TOKEN: "+token, Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "error D:", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPredio(){

        Call<ResponseBody> call = userClient.getPredio("Bearer "+token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {

                            JSONArray jsonArray = new JSONArray(response.body().string());
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                            int idPredio = jsonObject.getInt("id");

                            Toast.makeText(LoginActivity.this, "id: "+idPredio, Toast.LENGTH_SHORT).show();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "No hay predios", Toast.LENGTH_SHORT).show();

                }
            }



            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "no hay conexión", Toast.LENGTH_SHORT).show();

            }
        });

    }



}

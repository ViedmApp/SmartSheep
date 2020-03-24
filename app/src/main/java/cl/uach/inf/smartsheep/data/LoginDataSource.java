package cl.uach.inf.smartsheep.data;

import android.util.Log;
import android.widget.Toast;

import cl.uach.inf.smartsheep.data.model.LoggedInUser;
import cl.uach.inf.smartsheep.data.model.Login;
import cl.uach.inf.smartsheep.data.service.UserClient;
import cl.uach.inf.smartsheep.ui.login.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://sheep-api.herokuapp.com:5000/")
            .addConverterFactory(GsonConverterFactory.create());

    private Retrofit retrofit = builder.build();

    private UserClient userClient = retrofit.create(UserClient.class);
    String token;

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication

            Login login = new Login(username,
                        password);

            Call<LoggedInUser> call = userClient.login(login);
            call.enqueue(new Callback<LoggedInUser>() {
                @Override
                public void onResponse(Call<LoggedInUser> call, Response<LoggedInUser> response) {
                    if(response.isSuccessful()){
                        token = response.body().getDisplayName();
                    }

                }

                @Override
                public void onFailure(Call<LoggedInUser> call, Throwable t) {

                }
            });

            if(token != null || token.length() > 10) {
                Log.println(Log.ASSERT, "token", "token received");
                LoggedInUser loggedInUser = new LoggedInUser(token);

                return new Result.Success<>(loggedInUser);
            } else {
                throw new IOException();
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}

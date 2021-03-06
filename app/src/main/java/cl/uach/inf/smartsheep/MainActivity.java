package cl.uach.inf.smartsheep;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cl.uach.inf.smartsheep.data.model.Predio;
import cl.uach.inf.smartsheep.data.service.UserClient;
import cl.uach.inf.smartsheep.ui.home.HomeViewModel;
import cl.uach.inf.smartsheep.ui.property.PropertyViewModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ArrayList<Predio> predioArrayList = new ArrayList<>();

    private HomeViewModel homeViewModel;
    private PropertyViewModel propertyViewModel;

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://sheep-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();

    UserClient userClient = retrofit.create(UserClient.class);

    private int currentPredio;

    private String token;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        token = (String) this.getIntent().getExtras().get("Token");
         if (token == null){
             token = prefs.getString("TOKEN", null);
         }
        prefs.edit().putString("TOKEN", token).apply();

        getPredio();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateNavHeader();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_property, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);




        propertyViewModel = new ViewModelProvider(this).get(PropertyViewModel.class);
        propertyViewModel.loadPredios(predioArrayList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }


    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View root = navigationView.getHeaderView(0);
        final TextView textView = root.findViewById(R.id.username);

        textView.append(prefs.getString("USERNAME", "owner"));
    }


    private void getPredio(){
        Call<ResponseBody> call = userClient.getPredio("Bearer "+ token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {

                        JSONArray jsonArray = new JSONArray(response.body().string());

                        for (int i = 0; i < jsonArray.length(); ++i){
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            predioArrayList.add(
                                    new Predio(jsonObject.getInt("id"),
                                            jsonObject.getString("name"),
                                            Integer.parseInt(jsonObject.getString("latitude")),
                                            Integer.parseInt(jsonObject.getString("longitude"))
                                    )
                            );

                        }

                        if (predioArrayList.size() > 0){

                            currentPredio = predioArrayList.get(0).getId();

                            propertyViewModel.setIdPredio(currentPredio);
                            prefs.edit().putInt("IDPREDIO", currentPredio).apply();
                            homeViewModel.setPredio(currentPredio);

                            homeViewModel.loadSheeps();

                        }else{
                            Toast.makeText(getApplicationContext(),
                                    "No tiene predios",
                                    Toast.LENGTH_LONG).show();
                        }


                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "No hay predios", Toast.LENGTH_SHORT).show();

                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "No hay conexión", Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (token != null || !token.isEmpty()){
            outState.putString("TOKEN", token);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        assert savedInstanceState != null;
        token = savedInstanceState.getString("TOKEN");
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    public void onButtonClick(final View view){
        Intent intent = new Intent(this, FormActivity.class);

        startActivity(intent);
    }


}
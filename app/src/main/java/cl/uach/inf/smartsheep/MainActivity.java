package cl.uach.inf.smartsheep;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import cl.uach.inf.smartsheep.data.model.Predio;
import cl.uach.inf.smartsheep.data.model.Sheep;
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

    private ArrayList<Sheep> sheepArrayList = new ArrayList<>();
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


        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.loadSheeps(sheepArrayList);

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

    @Override
    protected void onStart() {
        super.onStart();
        updateRecyclerView();

    }

    public void updateRecyclerView(){
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        View root = relativeLayout.getRootView();
        final RecyclerView recycler = root.findViewById(R.id.sheepRecyclerView);

        homeViewModel.getArraySheep().observe(this, new Observer<ArrayList<Sheep>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChanged(ArrayList<Sheep> sheep) {
                Objects.requireNonNull(recycler.getAdapter()).notifyDataSetChanged();
            }
        });
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
                            getSheeps(predioArrayList.get(0).getId());
                            Toast.makeText(getApplicationContext(),
                                    "Total Predios: "+ predioArrayList.size(),
                                    Toast.LENGTH_SHORT).show();
                            currentPredio = predioArrayList.get(0).getId();

                            propertyViewModel.setIdPredio(currentPredio);
                            prefs.edit().putInt("IDPREDIO", currentPredio).apply();

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
                Toast.makeText(MainActivity.this, "no hay conexión", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void getSheeps(int predio){
        Call<ResponseBody> call = userClient.getSheeps(predio);
        call.enqueue(new Callback<ResponseBody>() {
                         @Override
                         public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                             if (response.isSuccessful()){
                                 try{
                                     JSONArray jsonArray = new JSONArray(response.body().string());

                                     sheepArrayList.clear();

                                     populateSheep(jsonArray);

                                     updateRecyclerView();
                                     Toast.makeText(getApplicationContext(),
                                             "Total ovejas: " + sheepArrayList.size(),
                                             Toast.LENGTH_LONG).show();

                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<ResponseBody> call, Throwable t) {
                             Toast.makeText(getApplicationContext(), "no hay conexión", Toast.LENGTH_LONG).show();
                         }
                     }

        );

    }

    public void populateSheep(JSONArray jsonArray) throws Exception{
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = Date.valueOf(
                    jsonObject.getString("date_birth").equalsIgnoreCase("null")?
                    "0001-01-01":
                    jsonObject.getString("date_birth"));

            sheepArrayList.add(
                    new Sheep(
                            jsonObject.getString("earring"),
                            jsonObject.getString("earring_color"),
                            jsonObject.getString("gender"),
                            jsonObject.getString("breed"),
                            jsonObject.getDouble("birth_weight"),
                            jsonObject.getString("purpose"),
                            jsonObject.getString("category"),
                            jsonObject.getInt("merit"),
                            jsonObject.getString("is_dead")
                    )
            );
        }

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
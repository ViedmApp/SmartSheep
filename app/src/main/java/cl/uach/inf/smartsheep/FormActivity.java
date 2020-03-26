package cl.uach.inf.smartsheep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.InvalidObjectException;
import java.sql.Date;

import cl.uach.inf.smartsheep.data.model.Sheep;
import cl.uach.inf.smartsheep.data.service.UserClient;
import cl.uach.inf.smartsheep.ui.property.PropertyViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FormActivity extends AppCompatActivity {
    private Sheep sheep;
    private Spinner genderSpinner;
    private Spinner deadSpinner;

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://sheep-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();

    UserClient userClient = retrofit.create(UserClient.class);

    SharedPreferences prefs;
    String token;
    int currentPredio;

    PropertyViewModel propertyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sheep_posting);

        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        token = prefs.getString("TOKEN", null);
        currentPredio = prefs.getInt("IDPREDIO", 0);

        Toast.makeText(this,
                "predio: " + currentPredio,
                Toast.LENGTH_SHORT).show();

        genderSpinner = (Spinner) findViewById(R.id.form_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_selection,
                android.R.layout.simple_spinner_dropdown_item
        );
        genderSpinner.setAdapter(adapter);

        deadSpinner = (Spinner) findViewById(R.id.form_isDead);
        ArrayAdapter<CharSequence> deadAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.status,
                android.R.layout.simple_spinner_dropdown_item
        );
        deadSpinner.setAdapter(deadAdapter);


    }

    public void onClick(View view) {
        //Send Data
        Sheep sheep;

        try {
            EditText editText = (EditText) findViewById(R.id.form_earring);
            String earring = editText.getText().toString();
            editText = findViewById(R.id.form_earringColor);
            String earringColor = editText.getText().toString();
            editText = findViewById(R.id.form_merit);
            int merit = Integer.parseInt(editText.getText().toString());
            editText = findViewById(R.id.form_breed);
            String breed = editText.getText().toString();
            editText = findViewById(R.id.form_category);
            String category = editText.getText().toString();
            editText = findViewById(R.id.form_birthWeight);
            double birthWeight = Double.parseDouble(editText.getText().toString());
            editText = findViewById(R.id.form_purpose);
            String purpose = editText.getText().toString();

            String gender = String.valueOf(genderSpinner.getSelectedItem());

            String isDead = String.valueOf(deadSpinner.getSelectedItem());

            sheep = new Sheep(
                    earring,
                    earringColor,
                    gender,
                    breed,
                    birthWeight,
                    purpose,
                    category,
                    merit,
                    isDead
            );

            if (currentPredio == 0 || token == null)
                throw new InvalidObjectException("Predio no válido");

            sheep.setFarms_id(currentPredio);

            Call<Sheep> call = userClient.postSheep("Bearer " + token, sheep);
            call.enqueue(new Callback<Sheep>() {
                @Override
                public void onResponse(Call<Sheep> call, Response<Sheep> response) {
                    if (response.isSuccessful()) {
                        System.out.println(response.body().toString());
                        Toast.makeText(getApplicationContext(),
                                "Oveja agregada Exitosamente",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Sheep> call, Throwable t) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Error agregando oveja",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

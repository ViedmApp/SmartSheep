package cl.uach.inf.smartsheep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InvalidObjectException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.Inflater;

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
    ArrayAdapter<CharSequence> deadAdapter;
    ArrayAdapter<CharSequence> adapter;

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://sheep-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();

    UserClient userClient = retrofit.create(UserClient.class);

    SharedPreferences prefs;
    String token;
    int currentPredio;

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
        adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_selection,
                android.R.layout.simple_spinner_dropdown_item
        );


        deadSpinner = (Spinner) findViewById(R.id.form_isDead);
        deadAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.status,
                android.R.layout.simple_spinner_dropdown_item
        );


        Intent intent = getIntent();
        sheep = (Sheep) intent.getSerializableExtra("SHEEP");
        if (sheep != null){
            preloadData();
            deadSpinner.setSelection(deadAdapter.getPosition(sheep.getIs_dead()));
            genderSpinner.setSelection(adapter.getPosition(sheep.getGender()));
        }

        genderSpinner.setAdapter(adapter);
        deadSpinner.setAdapter(deadAdapter);
    }

    private void preloadData() {

        EditText editText = findViewById(R.id.form_earring);
        editText.setText(sheep.getEarring());
        editText = findViewById(R.id.form_earringColor);
        editText.setText(sheep.getEarringColor());
        editText = findViewById(R.id.form_merit);
        editText.setText(String.valueOf(sheep.getMerit()));
        editText = findViewById(R.id.form_breed);
        editText.setText(sheep.getBreed());
        editText = findViewById(R.id.form_category);
        editText.setText(sheep.getCategory());
        editText = findViewById(R.id.form_purpose);
        editText.setText(sheep.getPurpose());
        editText = findViewById(R.id.form_birthWeight);
        editText.setText(
                String.format(Locale.ENGLISH, "%.2f", sheep.getBirth_weight())
        );

    }

    public void onClick(View view) {
        //Send Data

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

            String isDead = String.valueOf(deadAdapter.getPosition(deadSpinner.getSelectedItem().toString()));

            if(this.sheep == null) {
                //Send new sheep
                Sheep sheep = new Sheep(
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
                sendNewSheep(sheep);

            } else {
                //Edit sheep data
                sheep.setEarring(earring);
                sheep.setEarringColor(earringColor);
                sheep.setMerit(merit);
                sheep.setBirth_weight(birthWeight);
                sheep.setBreed(breed);
                sheep.setGender(gender);
                sheep.setIs_dead(isDead);
                sheep.setPurpose(purpose);
                sheep.setCategory(category);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enviar oveja");
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendEditSheep(sheep);
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                //sendEditSheep(sheep);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendEditSheep(Sheep sheep) {
        Call<Sheep> call = userClient.updateSheep(
                "Bearer " + token,
                sheep.get_id(),
                sheep);

        call.enqueue(new Callback<Sheep>() {
            @Override
            public void onResponse(Call<Sheep> call, Response<Sheep> response) {
                if(response.isSuccessful()){
                    Toast.makeText(
                            getApplicationContext(),
                            "Oveja editada exitosamente",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<Sheep> call, Throwable t) {
                Toast.makeText(
                        getApplicationContext(),
                        "Error editando oveja",
                        Toast.LENGTH_SHORT
                ).show();

            }
        });


    }



    private void sendNewSheep(Sheep sheep) throws InvalidObjectException {
            //Send new sheep to api
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
    }




}

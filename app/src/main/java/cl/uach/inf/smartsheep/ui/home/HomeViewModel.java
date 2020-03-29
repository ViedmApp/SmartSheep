package cl.uach.inf.smartsheep.ui.home;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cl.uach.inf.smartsheep.data.model.Sheep;
import cl.uach.inf.smartsheep.data.service.UserClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Sheep>> mySheeps;
    private MutableLiveData<Integer> predio;

    private Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://sheep-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create());

    private Retrofit retrofit = builder.build();

    private UserClient userClient = retrofit.create(UserClient.class);

    public HomeViewModel() {
        mySheeps = new MutableLiveData<>();
        predio = new MutableLiveData<>();
        mySheeps.setValue(new ArrayList<Sheep>());
    }

    LiveData<ArrayList<Sheep>> getArraySheep() {
        if (mySheeps.getValue() == null || mySheeps.getValue().size() == 0){
            loadSheeps();
        }

        return mySheeps;
    }

    public void loadSheeps() {
        final ArrayList<Sheep> sheepArrayList = mySheeps.getValue();
        if(predio.getValue() ==null) return;

        Call<ResponseBody> call = userClient.getSheeps(predio.getValue());
        call.enqueue(new Callback<ResponseBody>() {
                         @Override
                         public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                             if (response.isSuccessful()){
                                 try{
                                     JSONArray jsonArray = new JSONArray(response.body().string());

                                     sheepArrayList.clear();
                                     populateSheep(jsonArray, sheepArrayList);

                                     mySheeps.setValue(sheepArrayList);

                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<ResponseBody> call, Throwable t) {

                         }
                     }

        );
    }

    public void loadSheeps(ArrayList<Sheep> sheep){
        mySheeps.setValue(sheep);
        if(sheep.size()>0) predio.setValue(sheep.get(0).get_id());
    }

    public void setPredio(int predio){
        this.predio.setValue(predio);
    }

    private void populateSheep(JSONArray jsonArray, ArrayList<Sheep> sheepArrayList) throws Exception{
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
                            jsonObject.getInt("_id"),
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




}
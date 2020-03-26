package cl.uach.inf.smartsheep.data.service;

import cl.uach.inf.smartsheep.data.model.LoggedInUser;
import cl.uach.inf.smartsheep.data.model.Login;
import cl.uach.inf.smartsheep.data.model.Sheep;
import cl.uach.inf.smartsheep.data.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserClient {
    //comprobar Login
    @POST("login")
    Call<User> login(@Body Login login);

    //get predio
    @GET("predio")
    Call<ResponseBody> getPredio(@Header("Authorization") String token);

    @GET("sheep/{idPredio}")
    Call<ResponseBody> getSheeps(@Path("idPredio") int predio);

    @POST("sheep")
    Call<Sheep> postSheep(@Header("Authorization")String token, @Body Sheep sheep);

    @PUT("updatesheep/{id_oveja}")
    Call<Sheep> updateSheep(
            @Header("Authorization") String token,
            @Path("id_oveja") int id_oveja,
            @Body Sheep sheep);

}

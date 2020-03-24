package cl.uach.inf.smartsheep.data.service;

import cl.uach.inf.smartsheep.data.model.LoggedInUser;
import cl.uach.inf.smartsheep.data.model.Login;
import cl.uach.inf.smartsheep.data.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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

}

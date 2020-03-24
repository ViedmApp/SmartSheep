package cl.uach.inf.smartsheep.data.service;

import cl.uach.inf.smartsheep.data.model.LoggedInUser;
import cl.uach.inf.smartsheep.data.model.Login;
import cl.uach.inf.smartsheep.data.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {
    @POST("login") Call<User> login(@Body Login login);



}

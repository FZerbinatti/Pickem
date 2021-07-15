package com.dreamsphere.pickem.Services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderAPI_Summoner {

    @GET("/lol/summoner/v4/summoners/by-name/{summonerName}/")
    Call<Post_Summoner> getPost(@Path("summonerName") String summonerName, @Query("api_key") String api_key);


}

package com.dreamsphere.pickem.Services;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderAPI_Elo {

    @GET("/lol/league/v4/entries/by-summoner/{summonerID}/")
    Call<List<Post_Elo>> getPost(@Path("summonerID") String summonerID, @Query("api_key") String api_key);



}

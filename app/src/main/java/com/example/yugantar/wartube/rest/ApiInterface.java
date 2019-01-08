package com.example.yugantar.wartube.rest;

import com.example.yugantar.wartube.model.Model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface {
    @GET("channels")
    Call<Model> getSubscribers(@Query("part") String statistics,
                               @Query("forUsername") String name,
                               @Query("key") String key);

//    @GET("movie/{id}")
//    Call<Model> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);
}
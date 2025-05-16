package com.indian.ramiz.indianoilfetrcher.apiendpoints;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface CallMaker {

    @GET("ping")
    Call<PingResponse> ping();

    @POST
    Call<PostResponse> sendOtp(@Url String endPoint,
                               @Body SendBody sendBody);
}

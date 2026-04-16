package com.effinghamministorage.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EmsApiService {

    @POST("api/v1/get-pin")
    Call<AuthResponse> getPin(@Body GetPinRequest request);

    @POST("api/v1/authenticate")
    Call<AuthResponse> authenticate(@Body AuthenticateRequest request);
}

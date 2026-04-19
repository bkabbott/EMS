package com.effinghamministorage.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface EmsApiService {

    @POST("api/v1/get-pin")
    Call<AuthResponse> getPin(@Body GetPinRequest request);

    @POST("api/v1/authenticate")
    Call<AuthResponse> authenticate(@Body AuthenticateRequest request);

    @GET("api/v1/rentals/summary")
    Call<RentalSummaryResponse> getRentalSummary(@Header("Authorization") String bearerToken);

    @POST("api/v1/pins/issue")
    Call<LockboxPinIssuanceResponse> issueLockboxPin(
            @Header("X-API-Key") String apiKey,
            @Body LockboxPinIssuanceRequest request);
}

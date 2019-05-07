package com.uol.yt120.lecampus.model.restAPI;

import com.uol.yt120.lecampus.model.domain.Footprint;
import com.uol.yt120.lecampus.model.domain.User;
import com.uol.yt120.lecampus.model.domain.UserEvent;
import com.uol.yt120.lecampus.model.restDomain.BuildingGeoFence;
import com.uol.yt120.lecampus.model.restDomain.CrimeGeoFence;
import com.uol.yt120.lecampus.model.restDomain.EmergencyRequest;
import com.uol.yt120.lecampus.model.restDomain.PublicEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApiClient {

    /**
     * Get crime data
     */
    @GET("api/crime")
    Call<List<CrimeGeoFence>> getCrimeGeoFences();

    @GET("api/crime/{date}")
    Call<List<CrimeGeoFence>> getCrimeGeoFences(@Path("date") String yearAndMonth);

    /**
     * Building GeoFence
     */
    @GET("api/building")
    Call<BuildingGeoFence> getBuildingGeoFence(@Query("building") String buildingName); // /api/building?name=buildingName

    /**
     * Send emergency request
     */
    @POST("api/emergency")
    Call<EmergencyRequest> sendEmergencyRequest(@Body EmergencyRequest emergencyRequest);

    /**
     * Footprint sync
     */
    @GET("api/footprint/{author}")
    Call<List<Footprint>> getFootprintsByCreator(@Path("author") String creator);

    /**
     * User info for register
     */
    @FormUrlEncoded
    @POST("api/user")
    Call<User> sendUser(
            @Field("studentNumber") String studentNum,
            @Field("realname") String realName,
            @Field("uolEmail") String uolEmail
    );

    /**
     * Public Event for nearBy
     */
    @GET("api/public-event")
    Call<List<PublicEvent>> getPublicEvents();


    /**
     * User events for Timetable
     */
    @POST("api/user-event")
    Call<List<UserEvent>> sendUserEvents(@Body List<UserEvent> userEventList);

    @PUT("api/user-event/{serverId}") // replace completely
    Call<UserEvent> replaceUserEvent(@Path("serverId") int id, @Body UserEvent userEvent);

    @PATCH("api/user-event/{serverId}") // only change specific fields in body and keep others same
    Call<UserEvent> patchUserEvent(@Path("serverId") int id, @Body UserEvent userEvent);

}

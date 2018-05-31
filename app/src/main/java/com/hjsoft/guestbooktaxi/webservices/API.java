package com.hjsoft.guestbooktaxi.webservices;

import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.model.AllRidesPojo;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.CabArrivalTimePojo;
import com.hjsoft.guestbooktaxi.model.CabLocationPojo;
import com.hjsoft.guestbooktaxi.model.CabPojo;
import com.hjsoft.guestbooktaxi.model.CancelData;
import com.hjsoft.guestbooktaxi.model.CancelPojo;
import com.hjsoft.guestbooktaxi.model.CatPojo;
import com.hjsoft.guestbooktaxi.model.CityCenterPojo;
import com.hjsoft.guestbooktaxi.model.DurationPojo;
import com.hjsoft.guestbooktaxi.model.HashPojo;
import com.hjsoft.guestbooktaxi.model.LocalPackagesPojo;
import com.hjsoft.guestbooktaxi.model.MSeaterPojo;
import com.hjsoft.guestbooktaxi.model.OutStationPojo;
import com.hjsoft.guestbooktaxi.model.PaymentPojo;
import com.hjsoft.guestbooktaxi.model.RideStopPojo;
import com.hjsoft.guestbooktaxi.model.ServiceLocationPojo;
import com.hjsoft.guestbooktaxi.model.TariffRatePojo;
import com.hjsoft.guestbooktaxi.model.WalletDataPojo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by hjsoft on 23/11/16.
 */
public interface API {

    @GET
    Call<DurationPojo> getDistanceDetails(@Url String urlString);

    @GET("VehicleCat/GetVehicleCat")
    Call<List<CatPojo>> getCabCat();

    //To Get Vehicle data for the category
    @GET("NearLocations/GetNearestLocations")
    Call<List<CabPojo>> getAllCabs(@Query("location") String city,
                                   @Query("latitude") String latitude,
                                   @Query("longitude") String longitude,
                                   @Query("cat") String category,
                                   @Query("companyid") String companyid);


    @GET("VehDetails/GetVehDetails")
    Call<List<CabPojo>> getCabs(@Query("location") String location,
                                @Query("category") String category);

    //To send booking request to driver
    @POST("UserProfileCab/AddUserDetails")
    Call<BookCabPojo> sendCabRequest(@Body JsonObject v);

    //To get the booking status updated by driver - accept,decline, no response
    @POST("Status/CabStatus")
    Call<BookCabPojo> getCabRequestStatus(@Body JsonObject v);

    //To check if cab arrived at pickup point and to get OTP
    @POST("GuestNotify/Notify")
    Call<BookCabPojo> getNotification(@Body JsonObject v);

    // @POST("GuestCancel/CancelRide")
    //To get cancel fee
    @POST("Cancellation/AddCancellationFee")
    Call<List<CancelPojo>> sendCancelStatus(@Body JsonObject v);

    //To send booking cancel status
    @POST("GuestCancel/CancelRide")
    Call<BookCabPojo> doCancel(@Body JsonObject v);

    //To track the ongoing booking cab
    @GET("CabLocation/GetCabLoc")
    Call<List<CabLocationPojo>> getCabLocation(@Query("ReqID") String requestId,
                                               @Query("companyid") String companyId);

    //To get the minimum time for a cab to arrive
    @GET("DisplayTime/GetDisplayTime")
    Call<List<CabArrivalTimePojo>> getCabArrivalTimes(@Query("location") String location,
                                                      @Query("latitude") String latitude,
                                                      @Query("longitude") String longitude,
                                                      @Query("companyid") String companyId);


    @GET("Tariffrates/GetTariffrates")
    Call<List<TariffRatePojo>> getTariffRates(@Query("location") String location,
                                              @Query("category") String category,
                                              @Query("companyid") String companyId);

    //To get the bill details
    @GET("GuestDetailsByRequest/GetGuestDetails")
    Call<List<RideStopPojo>> getRideStopData(@Query("reqid") String requestId,
                                             @Query("companyid") String companyId,
                                             @Query("user") String user);

    @GET("GuestDetails/GetGuestDetails")
    Call<ArrayList<AllRidesPojo>> getAllUserRides(@Query("guestid") String guestId);

    @POST("UserLogin/CheckUserlogin")
    Call<BookCabPojo> loginTo(@Body JsonObject v);

    @POST("Register/AddUserDetails")
    Call<BookCabPojo> registerInto(@Body JsonObject v);

    @GET("GuestDetails/GetGuestDetails")
    Call<ArrayList<AllRidesPojo>> getUserRides(@Query("profileid") String profileId,
                                               @Query("user") String user,
                                               @Query("companyid") String companyId);

    //To register to get OTP
    @POST("Register/AddUserDetails")
    Call<BookCabPojo> registerForOTP(@Body JsonObject v);

    //To register with OTP
    @POST("Registerwithotp/AddUserDetailswithOTP")
    Call<BookCabPojo> registerWithOTP(@Body JsonObject v);

    //To login to get OTP
    @POST("UserLogin/CheckUserlogin")
    Call<BookCabPojo> loginForOTP(@Body JsonObject v);

    //To Login with OTP
    @POST("UserLoginWithOTP/CheckUserloginWithOTP")
    Call<BookCabPojo> loginWithOTP(@Body JsonObject v);

    //To check if OTP is validated by driver or not
    @POST("AuthenticateOTP/CheckOTPStatus")
    Call<BookCabPojo> checkOTP(@Body JsonObject v);

    //To get RateCard details
    @POST("RideEstimate/FareEstimate")
    Call<List<OutStationPojo>> getFareEstimate(@Body JsonObject v);

    //To send Outstation booking request
    @POST("OutStationCab/AddUserDetails")
    Call<BookCabPojo> sendOutstationDetails(@Body JsonObject v);

    //To send paymnet through wallet
    @POST("UserPayment/Payment")
    Call<List<PaymentPojo>> sendPaymentDetails(@Body JsonObject v);

    //To get Hash value from gateway
    @POST("PayuHash/HashValue")
    Call<List<HashPojo>> getHashValue(@Body JsonObject v);

    //To get package slab rates
    @GET("SlabDetails/GetSlabDetails")
    Call<List<LocalPackagesPojo>> getLocalPackages(@Query("location") String city,
                                                   @Query("companyid") String companyId);

    //To send feedback for a booking
    @POST("Feedback/AddFeedback")
    Call<BookCabPojo> sendFeedback(@Body JsonObject v);

    @GET("ServiceLocations/GetServicelocations")
    Call<List<ServiceLocationPojo>> getServiceLocations(@Query("companyid") String companyId);

    @GET("VehicleNames/GetVehicleNames")
    Call<List<MSeaterPojo>> getVehicles(@Query("companyid") String companyId);

    @POST("VehicleComments/AddVehicleComments")
    Call<BookCabPojo> addVehComments(@Body JsonObject v);

    @GET("ServiceCoordinates/GetServiceCoordinates")
    Call<List<CityCenterPojo>> getCoordinates(@Query("location") String location,
                                              @Query("companyid") String companyId);

    //To update No Response status from guest
    @POST("UpdateStatus/UpdateUserStatus")
    Call<BookCabPojo> sendCabAcceptanceStatus(@Body JsonObject v);

    //To get Ride History
    @GET("BookingHistory/GetDetails")
    Call<ArrayList<AllRidesPojo>> getRideHistory(@Query("profileid") String profileId,
                                                 @Query("user") String user,
                                                 @Query("companyid") String companyId,
                                                 @Query("fromdate") String fromdate,
                                                 @Query("todate") String todate);

    //To get wallet transactions
    @GET("UserWalletHistory/GetDetails")
    Call<ArrayList<WalletDataPojo>> getWalletHistory(@Query("profileid") String profileId,
                                                     @Query("companyid") String companyId);
    //To add driver rating
    @POST("DriverRating/AddDriverRating")
    Call<BookCabPojo> sendRating(@Body JsonObject v);

    //To get cancellation reasons
    @GET("CancellationReasons/GetReasons")
    Call<ArrayList<CancelData>> getCancelList(@Query("companyid") String companyId,
                                              @Query("user") String user);

}

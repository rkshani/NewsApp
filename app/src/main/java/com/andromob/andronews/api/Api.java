package com.andromob.andronews.api;

import com.andromob.andronews.models.Category;
import com.andromob.andronews.models.EarnHistory;
import com.andromob.andronews.models.GetComment;
import com.andromob.andronews.models.GetResponse;
import com.andromob.andronews.models.GetResponseDetailed;
import com.andromob.andronews.models.ModelPost;
import com.andromob.andronews.models.News;
import com.andromob.andronews.models.Settings;
import com.andromob.andronews.models.UserPoints;
import com.andromob.andronews.models.VP;
import com.andromob.andronews.models.Videos;
import com.andromob.andronews.models.WithdrawalHistory;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Api {
    @FormUrlEncoded
    @POST("api")
    Call<GetResponseDetailed> doLogin(@Query("apicall") String call, @Query("key") String key,
                                      @Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("api")
    Call<GetResponse> resetPassword(@Query("apicall") String call, @Query("key") String key,
                                    @Field("email") String email, @Field("phone") String phone);

    @FormUrlEncoded
    @POST("api")
    Call<GetResponse> doRegister(@Query("apicall") String call, @Query("key") String key,
                                 @Field("user_type") String user_type,
                                 @Field("name") String name, @Field("email") String email, @Field("password") String password, @Field("phone") String phone, @Field("auth_id") String auth_id);

    @FormUrlEncoded
    @POST("api")
    Call<GetResponseDetailed> doSocialLogin(@Query("apicall") String call, @Query("key") String key,
                                            @Field("user_type") String user_type,
                                            @Field("name") String name, @Field("email") String email, @Field("img_url") String img_url, @Field("auth_id") String auth_id);

    @Multipart
    @POST("api")
    Call<GetResponseDetailed> updateProfileWithImage(@Query("apicall") String call, @Query("key") String key,
                                                     @Part("name") RequestBody name, @Part("email") RequestBody email, @Part("phone") RequestBody phone, @Part MultipartBody.Part image, @Part("user_id") RequestBody id);

    @FormUrlEncoded
    @POST("api")
    Call<GetResponseDetailed> updateProfile(@Query("apicall") String call, @Query("key") String key,
                                            @Field("name") String name, @Field("email") String email, @Field("phone") String phone, @Field("user_id") String id);

    @FormUrlEncoded
    @POST("api")
    Call<GetResponse> updatePassword(@Query("apicall") String call, @Query("key") String key,
                                     @Field("user_id") int id, @Field("email") String email, @Field("password") String password);

    @GET("api")
    Call<List<Category>> getCategories(@Query("apicall") String call, @Query("key") String key);

    @GET("api")
    Call<List<Settings>> getSettings(@Query("apicall") String call, @Query("key") String key);

    @GET("api")
    Call<List<News>> getMethodNews(@Query("apicall") String call, @Query("type") String type, @Query("key") String key);

    @GET("api")
    Call<List<News>> getMethodNewsFavorite(@Query("apicall") String call, @Query("type") String type, @Query("uid") int user_id, @Query("key") String key);

    @GET("api")
    Call<List<News>> getNewsBySearch(@Query("apicall") String call, @Query("search") String search, @Query("key") String key);

    @GET("api")
    Call<List<News>> getMethodNewsByUser(@Query("apicall") String call, @Query("type") String type, @Query("key") String key, @Query("user_id") int user_id);

    @GET("api")
    Call<List<Videos>> getMethodVideos(@Query("apicall") String call, @Query("key") String key, @Query("page") int page);

    @GET("api")
    Call<List<Videos>> getRelatedVideos(@Query("apicall") String call, @Query("key") String key, @Query("type") String type, @Query("id") int id, @Query("cid") int cid);

    @GET("api")
    Call<List<News>> getNewsByCategory(@Query("apicall") String call, @Query("getByCat") int cat_id, @Query("page") int page, @Query("key") String key);

    @GET("api")
    Call<List<News>> getRelatedNews(@Query("apicall") String call, @Query("key") String key, @Query("type") String type, @Query("id") int id, @Query("cid") int cid);

    @FormUrlEncoded
    @POST("api")
    Call<List<ModelPost>> postView(@Query("apicall") String call, @Field("id") int id, @Query("key") String key);

    @FormUrlEncoded
    @POST("api")
    Call<List<ModelPost>> addRemoveFav(@Query("apicall") String call, @Query("key") String key, @Field("post_id") int post_id, @Field("user_id") int user_id);

    @FormUrlEncoded
    @POST("api")
    Call<List<ModelPost>> doReport(@Query("apicall") String call, @Query("key") String key,
                                   @Field("type") String type, @Field("uid") int uid, @Field("nid") int nid, @Field("vid") int vid, @Field("report") String report);

    @FormUrlEncoded
    @POST("api")
    Call<List<ModelPost>> doComment(@Query("apicall") String call, @Query("key") String key,
                                    @Field("type") String type, @Field("uid") int uid, @Field("nid") int nid, @Field("vid") int vid, @Field("comment") String comment);

    @GET("api")
    Call<List<GetComment>> getComments(@Query("apicall") String call, @Query("key") String key, @Query("type") String type, @Query("post_id") int post_id);

    @GET("api")
    Call<List<EarnHistory>> getEarnHistory(@Query("apicall") String call, @Query("key") String key, @Query("user_id") int user_id);

    @GET("api")
    Call<List<WithdrawalHistory>> getWithdrawalHistory(@Query("apicall") String call, @Query("key") String key, @Query("user_id") int user_id);

    @GET("api")
    Call<List<UserPoints>> getUserPoints(@Query("apicall") String call, @Query("key") String key, @Query("uid") int uid);

    @Multipart
    @POST("api")
    Call<List<ModelPost>> addNews(@Query("apicall") String call, @Query("key") String key,
                                  @Part("news_type") RequestBody news_type, @Part("cid") RequestBody cid, @Part("uid") RequestBody uid, @Part("news_title") RequestBody news_title, @Part("news") RequestBody news, @Part MultipartBody.Part thumbnail, @Part("video_url") RequestBody video_url, @Part("news_status") RequestBody news_status);

    @FormUrlEncoded
    @POST("api")
    Call<List<ModelPost>> updateNews(@Query("apicall") String call, @Query("key") String key,
                                     @Field("id") int id, @Field("news_type") String news_type, @Field("cid") int cid, @Field("uid") int uid, @Field("news_title") String news_title, @Field("news") String news, @Field("video_url") String video_url, @Field("news_status") int news_status);

    @Multipart
    @POST("api")
    Call<List<ModelPost>> updateNewsWithImage(@Query("apicall") String call, @Query("key") String key,
                                              @Part("id") RequestBody id, @Part("news_type") RequestBody news_type, @Part("cid") RequestBody cid, @Part("uid") RequestBody uid, @Part("news_title") RequestBody news_title, @Part("news") RequestBody news, @Part MultipartBody.Part thumbnail, @Part("video_url") RequestBody video_url, @Part("news_status") RequestBody news_status);

    @FormUrlEncoded
    @POST("api")
    Call<List<ModelPost>> addPoints(@Query("apicall") String call, @Query("key") String key,
                                     @Field("user_id") int user_id, @Field("post_id") int post_id, @Field("activity") String activity);

    @FormUrlEncoded
    @POST("api")
    Call<List<ModelPost>> redeemPoints(@Query("apicall") String call, @Query("key") String key,
                                    @Field("uid") int uid, @Field("points") int points, @Field("amount") double amount, @Field("payment_method") String payment_method, @Field("payment_detail") String payment_detail);

    @FormUrlEncoded
    @POST("api")
    Call<List<ModelPost>> submitContactForm(@Query("apicall") String call, @Query("key") String key,
                                            @Field("name") String name, @Field("email") String email, @Field("subject") String subject, @Field("message") String message);

    @GET("api")
    Call<VP> VP(@Query("do") String call, @Query("pcode") String pcode);
}

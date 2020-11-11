package com.lqcode.adjump.frame.net;

import com.lqcode.adjump.entity.PayEntity;
import com.lqcode.adjump.entity.Result;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {


    @GET("autoSkip/md5")
    Call<Result<String>> getMd5();

    @GET("autoSkip/get")
    Call<Result<Map<String, String>>> getAppConfig();

    @GET("pay/native/{phone}/{type}")
    Call<Result<PayEntity>> payInfo(@Path("phone") String phone, @Path("type") String type);


    @GET("autoSkip/judgeVIP/{phone}")
    Call<Result<Object>> judgeVIP(@Path("phone") String phone);

    @POST("pay/activationKey/{phone}/{codeKey}")
    Call<Result<Integer>> activationKey(@Path("phone") String phone, @Path("codeKey") String codeKey);


}

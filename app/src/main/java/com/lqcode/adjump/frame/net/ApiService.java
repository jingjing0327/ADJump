package com.lqcode.adjump.frame.net;

import com.lqcode.adjump.entity.PayEntity;
import com.lqcode.adjump.entity.Result;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {


    @GET("autoSkip/md5")
    Call<Result<String>> getMd5();

    @GET("autoSkip/get")
    Call<Result<Map<String, String>>> getAppConfig();

    @GET("pay/native/{phone}/alipay")
    Call<Result<PayEntity>> alipay(@Path("phone") String phone);

    @GET("autoSkip/judgeVIP/{phone}")
    Call<Result<Object>> judgeVIP(@Path("phone") String phone);


}

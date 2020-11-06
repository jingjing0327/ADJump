package com.lqcode.adjump.frame.net;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiController {
    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.lqcode.cn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    static ApiService service;

    public static ApiService getService() {
        if (service == null)
            service = retrofit.create(ApiService.class);
        return service;
    }

}

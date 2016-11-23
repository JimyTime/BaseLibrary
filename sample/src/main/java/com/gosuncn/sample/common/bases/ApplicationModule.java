package com.gosuncn.sample.common.bases;
/**
 * @author Tong
 * @time 2016/11/22
 * @apiNote
 */

import android.app.Application;

import com.gosuncn.core.common.logger.okHttpLog.HttpLoggingInterceptorM;
import com.gosuncn.core.common.logger.okHttpLog.LogInterceptor;
import com.gosuncn.core.common.util.UrlInterceptor;
import com.gosuncn.sample.common.config.Config;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    public Application application() {
        return application;
    }

    @Singleton
    @Provides
    public UrlInterceptor provideUrlInterceptor(){
        return new UrlInterceptor();
    }

    @Singleton
    @Provides
    public Retrofit provideRetrofit(UrlInterceptor interceptor){

        //添加HTTPLogging，用于打印http请求和响应
        HttpLoggingInterceptorM loggingInterceptor = new HttpLoggingInterceptorM(new LogInterceptor());
        loggingInterceptor.setLevel(HttpLoggingInterceptorM.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(interceptor)
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();

        String baseUrl = "http://"+ Config.IpConfig.IP_ADDRESS+":"+Config.IpConfig.PORT+"/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit;
    }

}

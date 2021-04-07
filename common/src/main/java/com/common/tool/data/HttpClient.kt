package com.common.tool.data

import android.util.Log
import com.common.tool.data.service.CommonService
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author 李雄厚
 *
 * @features 客户端配置
 */
class HttpClient private constructor(){
    private object Holder {
        val INSTANCE = HttpClient()
    }

    companion object{
        val instance by lazy { Holder.INSTANCE }
        val serviceMap by lazy {
            return@lazy HashMap<String, Any>()
        }
        fun getCommonService() = getService<CommonService>()

        inline fun <reified T> getService(timeout:Long = 20,unit: TimeUnit = TimeUnit.SECONDS): T {
            val clazz = serviceMap.get(T::class.java.name)
            if (clazz == null) {
                val create = initClient(timeout, unit).create(T::class.java)
                serviceMap.put(T::class.java.name, (create as T)!!)
            }
            return serviceMap[T::class.java.name] as T
        }

        fun initClient(timeout: Long=20,unit: TimeUnit = TimeUnit.SECONDS): Retrofit {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(timeout, unit)
                .writeTimeout(timeout, unit)
                .connectTimeout(timeout, unit)
                .addInterceptor(
                    LoggingInterceptor.Builder()
                        .setLevel(Level.BASIC)
                        .log(Log.ERROR)
                        .build()
                )
                .build()
            return Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com/"/*这里输入自己的服务器*/)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
    }
}
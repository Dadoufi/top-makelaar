package dadoufi.topmakelaar.inject

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dadoufi.topmakelaar.App
import dadoufi.topmakelaar.BuildConfig
import dadoufi.topmakelaar.util.AppRxSchedulers
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideContext(application: App): Context =
        application.applicationContext

    @Singleton
    @Provides
    fun provideRxSchedulers(): AppRxSchedulers {
        return AppRxSchedulers(
            Schedulers.io(),
            Schedulers.single(),
            Schedulers.io(),
            AndroidSchedulers.mainThread()
        )
    }


    @Provides
    @Singleton
    @Named("cache")
    fun provideCacheDir(application: App): File = application.cacheDir

    @Provides
    @Named("api_key")
    fun provideFundaApiKey(): String = BuildConfig.FUNDA_API_KEY

    @Provides
    @Named("base_url")
    fun provideBaseUrl(): String = BuildConfig.FUNDA_BASE_URL

    @Provides
    @Named("request_type")
    fun provideRequestType(): String = BuildConfig.REQUEST_TYPE


    @Provides
    @Singleton
    fun provideAppPreferences(application: App): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

}
package dadoufi.topmakelaar.inject

import dadoufi.topmakelaar.App
import dadoufi.topmakelaar.data.DatabaseModule
import dadoufi.topmakelaar.remote.NetworkModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        DatabaseModule::class,
        ViewModelBuilder::class,
        NetworkModule::class,
        MainActivityModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}
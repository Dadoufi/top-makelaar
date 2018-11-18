package dadoufi.topmakelaar.inject

import androidx.lifecycle.ViewModel
import dadoufi.topmakelaar.ui.makelaar.MakelaarFragment
import dadoufi.topmakelaar.ui.makelaar.MakelaarViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun topMalekaarFragment(): MakelaarFragment


    @Binds
    @IntoMap
    @ViewModelKey(MakelaarViewModel::class)
    abstract fun bindTopAlbumViewModel(viewModel: MakelaarViewModel): ViewModel

}
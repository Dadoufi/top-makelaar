package dadoufi.topmakelaar.base

import androidx.lifecycle.ViewModel
import dadoufi.topmakelaar.data.entities.UiState
import dadoufi.topmakelaar.util.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel<Result : Any> : ViewModel() {

    val uiStateLiveData by lazy(LazyThreadSafetyMode.NONE) {
        SingleLiveEvent<UiState<Result>>()
    }

    val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }


    open fun callRefresh(forceUpdate: Boolean = true) = Unit
}
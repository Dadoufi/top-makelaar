package dadoufi.topmakelaar.ui.makelaar

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import dadoufi.topmakelaar.base.BaseViewModel
import dadoufi.topmakelaar.data.entities.TopMakelaar
import dadoufi.topmakelaar.data.repositories.Repository
import dadoufi.topmakelaar.data.repositories.makelaar.MakelaarRepository
import io.reactivex.rxkotlin.plusAssign
import java.util.*
import javax.inject.Inject

@SuppressLint("RxSubscribeOnError")
class MakelaarViewModel @Inject constructor(
    private var repository: MakelaarRepository
) : BaseViewModel<List<TopMakelaar>>() {

    private val queryMutableLiveData = MutableLiveData<String>()


    override fun callRefresh(forceUpdate: Boolean) {
        disposables.clear()
        queryMutableLiveData.value?.let {
            disposables += repository.execute(
                Repository.Params(
                    queryMutableLiveData.value!!,
                    if (forceUpdate) Repository.UpdateSource.NETWORK else Repository.UpdateSource.CACHE
                )
            )
                .subscribe { uiState -> uiStateLiveData.postValue(uiState) }
        }

    }


    fun setQuery(query: String) {
        val searchQuery = BASE_QUERY + query.trim().toLowerCase(Locale.getDefault())

        if (searchQuery == queryMutableLiveData.value) {
            callRefresh(false)
        } else {
            queryMutableLiveData.value = searchQuery
            callRefresh(true)
        }

    }

    companion object {
        const val BASE_QUERY = "/amsterdam/"
    }

}
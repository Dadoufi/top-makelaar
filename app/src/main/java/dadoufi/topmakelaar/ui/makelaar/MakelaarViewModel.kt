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


    override fun callRefresh(forceUpdate: Boolean) { // fetch data from db or from Network
        queryMutableLiveData.value?.let {
            disposables += repository.execute(
                Repository.Params(
                    queryMutableLiveData.value!!,
                    if (forceUpdate) Repository.UpdateSource.NETWORK else Repository.UpdateSource.CACHE
                )
            )
                .subscribe { uiState -> uiStateLiveData.postValue(uiState) } // post the State updates on the UI
        }

    }


    fun setQuery(query: String) {
        val searchQuery = BASE_QUERY + query.trim().toLowerCase(Locale.getDefault())

        if (searchQuery == queryMutableLiveData.value) {
            // in case of an orientation change while a request is active don't fetch local data so that it liveData wont observe the constant insertions in the DB
            // and update the List with each insertion
            if (disposables.isDisposed) {
                callRefresh(false)//if its the same query fetch from DB
            }
        } else {
            disposables.clear()
            queryMutableLiveData.value = searchQuery
            callRefresh(true) // else fetch from network
        }

    }

    companion object {
        const val BASE_QUERY = "/amsterdam/"
    }

}
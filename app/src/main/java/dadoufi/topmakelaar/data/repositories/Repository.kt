package dadoufi.topmakelaar.data.repositories

import dadoufi.topmakelaar.data.entities.UiState
import io.reactivex.Flowable

interface BaseRepository<T : Any>

interface Repository<T : Any> : BaseRepository<Any> {
    fun refresh(query: String, forceRefresh: Boolean = true): Flowable<UiState<T>>


    fun execute(params: Params): Flowable<UiState<T>> {
        return when (params.updateSource) {
            UpdateSource.CACHE -> refresh(params.query, false)
            UpdateSource.NETWORK -> refresh(params.query)
        }
    }

    data class Params(val query: String, val updateSource: UpdateSource)

    enum class UpdateSource {
        NETWORK,
        CACHE
    }


}






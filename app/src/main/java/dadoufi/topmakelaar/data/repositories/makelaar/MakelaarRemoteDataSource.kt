package dadoufi.topmakelaar.data.repositories.makelaar

import dadoufi.topmakelaar.data.entities.TopMakelaar
import io.reactivex.Flowable

interface MakelaarRemoteDataSource {

    fun getRemoteProperties(
        type: String = "koop",
        query: String = "amsterdam",
        page: Int = 1,
        pageSize: Int = 25
    ): Flowable<List<TopMakelaar>>

}
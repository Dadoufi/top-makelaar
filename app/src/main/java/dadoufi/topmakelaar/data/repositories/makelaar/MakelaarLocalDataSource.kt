package dadoufi.topmakelaar.data.repositories.makelaar

import dadoufi.topmakelaar.data.entities.TopMakelaar
import io.reactivex.Flowable

interface MakelaarLocalDataSource {


    fun getLocalMakelaar(query: String): Flowable<List<TopMakelaar>>
}
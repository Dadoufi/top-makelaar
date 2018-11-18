package dadoufi.topmakelaar.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dadoufi.topmakelaar.data.entities.MakelaarEntity
import dadoufi.topmakelaar.data.entities.TopMakelaar
import io.reactivex.Flowable

@Dao
abstract class MakelaarDao : EntityDao<MakelaarEntity> {

    @Transaction
    @Query("SELECT name,COUNT(name) AS `listings`  FROM makelaar WHERE `query` = :query GROUP BY name ORDER BY COUNT(name) DESC LIMIT 10 ")
    abstract fun getTopMakelaar(query: String): Flowable<List<TopMakelaar>>


    @Query("DELETE FROM makelaar")
    abstract fun deleteAll()

    @Query("DELETE FROM makelaar WHERE `query` = :query ")
    abstract fun deleteAllFiltered(query: String)


    @Query("SELECT COUNT(*) FROM makelaar WHERE `query` = :query ")
    abstract fun getCount(query: String): Int


}


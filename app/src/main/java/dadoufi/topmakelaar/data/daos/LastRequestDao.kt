package dadoufi.topmakelaar.data.daos

import androidx.room.Dao
import androidx.room.Query
import dadoufi.topmakelaar.data.entities.LastRequestEntity
import io.reactivex.Maybe

@Dao
abstract class LastRequestDao : EntityDao<LastRequestEntity> {


    @Query("SELECT * FROM last_request WHERE `query` = :query")
    abstract fun getLastRequest(query: String): Maybe<LastRequestEntity>


    @Query("DELETE  FROM last_request WHERE `query` = :query")
    abstract fun deleteLastRequest(query: String)


    @Query("SELECT COUNT(*) FROM last_request WHERE `query` = :query ")
    abstract fun getCount(query: String): Int

}
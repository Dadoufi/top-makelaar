package dadoufi.topmakelaar.data.entities

import android.os.SystemClock
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dadoufi.topmakelaar.data.repositories.makelaar.MakelaarRepository.Companion.CACHE_TIME_WINDOW
import java.util.concurrent.TimeUnit


@Entity(
    tableName = "last_request",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["query"], unique = true)
    ]
)
data class LastRequestEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "query") val query: String,
    @ColumnInfo(name = "request_page") val requestPage: Int,
    @ColumnInfo(name = "total_pages") val totalPages: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long = SystemClock.uptimeMillis()
) {

    fun shouldFetch(): Boolean {
        val lastFetched = timestamp
        if (lastFetched == -1L) return true

        val now = SystemClock.uptimeMillis()
        return now - lastFetched > TimeUnit.SECONDS.toMillis(CACHE_TIME_WINDOW)
    }


}
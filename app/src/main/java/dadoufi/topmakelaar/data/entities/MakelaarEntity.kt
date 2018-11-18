package dadoufi.topmakelaar.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "makelaar",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["name"], unique = false),
        Index(value = ["mid"], unique = false),
        Index(value = ["page"], unique = false),
        Index(value = ["query"], unique = false)
    ]
)
data class MakelaarEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String? = null,
    @ColumnInfo(name = "mid") val mid: Long? = null,
    @ColumnInfo(name = "query") val query: String? = null,
    @ColumnInfo(name = "page") val page: Int? = null
)


data class TopMakelaar(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "listings") var listings: Int
)
package dadoufi.topmakelaar.remote.model

import com.google.gson.annotations.SerializedName

data class MakelaarResponse(
    @SerializedName("Objects")
    val properties: List<Makelaar>?,
    @SerializedName("Paging")
    val paging: Paging,
    @SerializedName("TotaalAantalObjecten")
    val totalProperties: Int
)
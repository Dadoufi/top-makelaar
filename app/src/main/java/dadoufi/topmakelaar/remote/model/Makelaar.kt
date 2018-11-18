package dadoufi.topmakelaar.remote.model

import com.google.gson.annotations.SerializedName

data class Makelaar(
    @SerializedName("MakelaarId")
    val makelaarId: Long?,
    @SerializedName("MakelaarNaam")
    val makelaarName: String?
)
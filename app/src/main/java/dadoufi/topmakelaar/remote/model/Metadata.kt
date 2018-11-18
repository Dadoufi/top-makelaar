package dadoufi.topmakelaar.remote.model

import com.google.gson.annotations.SerializedName

data class Metadata(
    @SerializedName("ObjectType")
    val objectType: String?,
    @SerializedName("Omschrijving")
    val omschrijving: String?,
    @SerializedName("Titel")
    val titel: String?
)
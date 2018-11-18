package dadoufi.topmakelaar.remote.model

import com.google.gson.annotations.SerializedName

data class Paging(
    @SerializedName("AantalPaginas")
    val totalPages: Int,
    @SerializedName("HuidigePagina")
    val currentPage: Int,
    @SerializedName("VolgendeUrl")
    val nextPageUrl: String?,
    @SerializedName("VorigeUrl")
    val previousPageUrl: String?
)
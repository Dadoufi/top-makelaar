package dadoufi.topmakelaar.remote.model

interface ApiError {
    // @SerializedName("error")
    val error: Int?
    //@SerializedName("message")
    val message: String?

}
package dadoufi.topmakelaar.remote

import dadoufi.topmakelaar.BuildConfig
import dadoufi.topmakelaar.remote.model.MakelaarResponse
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * REST API access points
 */
interface FundaService {

    @GET(BuildConfig.REQUEST_TYPE + BuildConfig.FUNDA_API_KEY)
    fun getProperties(
        @Query("type") type: String,
        @Query("zo") searchQuery: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Flowable<MakelaarResponse>


}

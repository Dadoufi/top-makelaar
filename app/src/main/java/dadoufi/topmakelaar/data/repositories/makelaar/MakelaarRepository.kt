package dadoufi.topmakelaar.data.repositories.makelaar

import dadoufi.topmakelaar.data.DatabaseTransactionRunner
import dadoufi.topmakelaar.data.daos.LastRequestDao
import dadoufi.topmakelaar.data.daos.MakelaarDao
import dadoufi.topmakelaar.data.entities.LastRequestEntity
import dadoufi.topmakelaar.data.entities.MakelaarEntity
import dadoufi.topmakelaar.data.entities.TopMakelaar
import dadoufi.topmakelaar.data.entities.UiState
import dadoufi.topmakelaar.data.mapper.MakelaarToMakelaarEntity
import dadoufi.topmakelaar.data.repositories.Repository
import dadoufi.topmakelaar.remote.FundaService
import dadoufi.topmakelaar.remote.model.Makelaar
import dadoufi.topmakelaar.remote.model.MakelaarResponse
import dadoufi.topmakelaar.util.AppRxSchedulers
import dadoufi.topmakelaar.util.retryOnError
import io.reactivex.Flowable
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MakelaarRepository @Inject constructor(
    private val makelaarDao: MakelaarDao,
    private val lastRequestDao: LastRequestDao,
    private val fundaService: FundaService,
    private val schedulers: AppRxSchedulers,
    private val mapper: MakelaarToMakelaarEntity,
    private val transactionRunner: DatabaseTransactionRunner
) : Repository<List<TopMakelaar>>,
    MakelaarRemoteDataSource,
    MakelaarLocalDataSource {

    companion object {
        const val CACHE_TIME_WINDOW = 60L
        const val DELAY_BEFORE_RETRY = 10000L
        const val MAX_RETRY = 20
    }


    override fun refresh(
        query: String,
        forceRefresh: Boolean
    ): Flowable<UiState<List<TopMakelaar>>> {
        val result: Flowable<List<TopMakelaar>>
        result = when {
            forceRefresh -> getRemoteProperties(query = query) //get the freshData
            else -> lastRequestDao.getLastRequest(query)//get theLastPage request for this query
                .subscribeOn(schedulers.database)
                .defaultIfEmpty(LastRequestEntity(query = query, requestPage = 1, totalPages = 25, timestamp = -1))
                //if its empty return a default last request, which always fetches the latest data with

                .map { it.shouldFetch() } //checks if the last request is within the CACHE_TIME_WINDOW
                .toFlowable()
                .flatMap { shouldFetch ->
                    // if it is, we get the data from the DB else we fetch fresh data
                    if (shouldFetch) getRemoteProperties(query = query) else getLocalMakelaar(
                        query = query
                    )
                }
        }

        return result
            .map<UiState<List<TopMakelaar>>> { UiState.Success(it) } //  post the data
            .startWith(UiState.Loading) // start the stream with a loading event
            .onErrorReturn {
                UiState.Error.RefreshError(it.message) // post the error
            }
            .observeOn(schedulers.main)
    }

    override fun getRemoteProperties(
        type: String,
        query: String,
        page: Int,
        pageSize: Int
    ): Flowable<List<TopMakelaar>> {

        var totalPages = 0
        var currentPage = 1


        return fundaService.getProperties(type, query, page, pageSize) //fetch the first page
            .subscribeOn(schedulers.network)
            .map { makelaarResponse ->
                totalPages = makelaarResponse.paging.totalPages //  save to total number of pages
                makelaarResponse
            }
            .flatMap {
                Flowable.range( //loop over the page number starting from the latest page requested in case of a 401 error so that we don't start from page 1 again
                    currentPage,
                    totalPages
                ).takeUntil { it == totalPages }
                    .flatMap { page ->
                        if (page == 1) {
                            makelaarDao.deleteAllFiltered(query) // delete previous entries if its a fresh request
                        }
                        fundaService.getProperties(
                            type,
                            query,
                            page,
                            pageSize
                        ) // fetch data for each page
                            .map { response ->
                                transactionRunner.run {
                                    insertEntities(response, query) //insert data in db
                                    insertLastRequest(
                                        query,
                                        response.paging.currentPage,
                                        response.paging.totalPages
                                    ) // save the last request
                                }
                                totalPages = response.paging.totalPages // update the total pages
                                currentPage =
                                        response.paging.currentPage //keep the current page number
                            }
                    }

            }
            .retryOnError( // in case of request  limit  reached or other error, retry with a delay
                predicate = { shouldRetry(it) },
                maxRetry = MAX_RETRY,
                delayBeforeRetry = DELAY_BEFORE_RETRY
            )
            .skipWhile { currentPage < totalPages } //  skip the return of the local data until all pages have been requested
            .concatMap { getLocalMakelaar(query = query) } // return the new data from DB


    }


    private fun shouldRetry(throwable: Throwable): Boolean {
        val retry = when (throwable) {
            is HttpException -> throwable.code() == 401
            is IOException -> true
            else -> false
        }

        return retry
    }

    private fun insertLastRequest(
        query: String,
        requestPage: Int,
        totalPages: Int
    ) {
        return lastRequestDao.insertReplace(
            LastRequestEntity(
                query = query,
                requestPage = requestPage,
                totalPages = totalPages
            )
        )
    }


    private fun insertEntities(
        makelaarResponse: MakelaarResponse,
        query: String
    ) {
        makelaarResponse.properties?.let {
            makelaarDao.insertAll(
                convertRemoteToLocal(
                    it,
                    query,// sometimes the artist name within the object is different from the actual artist query.We use the query
                    makelaarResponse.paging.currentPage
                )
            )
        }
    }


    private fun convertRemoteToLocal(
        remoteAlbums: List<Makelaar>,
        query: String,
        page: Int
    ): List<MakelaarEntity> {
        return remoteAlbums.asSequence()
            .mapTo(mutableListOf()) { makelaar ->
                mapper.map(
                    makelaar,
                    query,
                    page
                )
            }
    }


    override fun getLocalMakelaar(query: String): Flowable<List<TopMakelaar>> {
        return makelaarDao.getTopMakelaar(query)
            .subscribeOn(schedulers.database)
    }


}




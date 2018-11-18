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
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
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
        const val TIMEOUT = 60L
        const val DELAY_BEFORE_RETRY = 10000L
        const val MAX_RETRY = 20
    }


    override fun refresh(
        query: String,
        forceRefresh: Boolean
    ): Flowable<UiState<List<TopMakelaar>>> {
        val result: Flowable<List<TopMakelaar>>
        result = when {
            forceRefresh -> getRemoteProperties(query = query)
            else -> lastRequestDao.getLastRequest(query)
                .subscribeOn(schedulers.database)
                .defaultIfEmpty(LastRequestEntity(query = query, requestPage = 1, totalPages = 25, timestamp = -1))
                .map { it.shouldFetch() }
                .toFlowable()
                .flatMap { shouldFetch ->
                    if (shouldFetch) getRemoteProperties(query = query) else getLocalMakelaar(
                        query = query
                    )
                }
        }

        return result
            .map<UiState<List<TopMakelaar>>> { UiState.Success(it) }
            .startWith(UiState.Loading)
            .onErrorReturn {
                UiState.Error.RefreshError(it.message)
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

        Timber.d(TimeUnit.SECONDS.toMillis(TIMEOUT).toString())

        return fundaService.getProperties(type, query, page, pageSize)
            .subscribeOn(schedulers.network)
            .map { makelaarResponse ->
                totalPages = makelaarResponse.paging.totalPages
                makelaarResponse
            }
            .flatMap {
                Flowable.range(
                    currentPage,
                    totalPages
                ).takeUntil { it == totalPages }
                    .flatMap { page ->
                        if (page == 1) {
                            makelaarDao.deleteAllFiltered(query)
                        }
                        fundaService.getProperties(type, query, page, pageSize)
                            .map { response ->
                                transactionRunner.run {
                                    insertEntities(response, query)
                                    insertLastRequest(query, response.paging.currentPage, response.paging.totalPages)
                                }
                                totalPages = response.paging.totalPages
                                currentPage = response.paging.currentPage
                            }
                    }

            }
            .retryOnError(
                predicate = { shouldRetry(it) },
                maxRetry = MAX_RETRY,
                delayBeforeRetry = DELAY_BEFORE_RETRY
            )
            .skipWhile { currentPage < totalPages }
            .concatMap { getLocalMakelaar(query = query) }


    }


    /*  Flowable.range(
      makelaarResponse.paging.currentPage + 1,
      makelaarResponse.paging.totalPages
      )
      .flatMap {
          fundaService.getProperties(type, query, it, pageSize)
              .map { response ->
                  insertEntities(response, query)
                  insertLastRequest(query, it, response)
              }
              .doOnError {
                  onErrorSaveLastRequest(query, makelaarResponse)
              }
      }
      .doOnError {
          onErrorSaveLastRequest(query, makelaarResponse)
      }*/

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




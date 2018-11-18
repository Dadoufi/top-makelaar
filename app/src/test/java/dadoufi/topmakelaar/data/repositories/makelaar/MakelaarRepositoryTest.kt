package dadoufi.topmakelaar.data.repositories.makelaar

import dadoufi.topmakelaar.data.RoomTransactionRunner
import dadoufi.topmakelaar.data.daos.LastRequestDao
import dadoufi.topmakelaar.data.daos.MakelaarDao
import dadoufi.topmakelaar.data.mapper.MakelaarToMakelaarEntity
import dadoufi.topmakelaar.remote.FundaService
import dadoufi.topmakelaar.util.BaseDatabaseTest
import dadoufi.topmakelaar.util.testAppRxSchedulers
import org.mockito.Mockito


class MakelaarRepositoryTest : BaseDatabaseTest() {


    private lateinit var makelaarRemoteDataSource: MakelaarRemoteDataSource
    private lateinit var makelaarLocalDataSource: MakelaarLocalDataSource
    private lateinit var service: FundaService
    private lateinit var makelaarDao: MakelaarDao
    private lateinit var lastRequestDao: LastRequestDao
    private lateinit var mapper: MakelaarToMakelaarEntity

    private lateinit var repository: MakelaarRepository

    override fun setup() {
        super.setup()

        makelaarDao = db.makelaarDao()
        lastRequestDao = db.lastRequestDao()

        makelaarRemoteDataSource = Mockito.mock(MakelaarRemoteDataSource::class.java)
        makelaarLocalDataSource = Mockito.mock(MakelaarLocalDataSource::class.java)

        val txRunner = RoomTransactionRunner(db)

        repository = MakelaarRepository(
            makelaarDao,
            lastRequestDao,
            service,
            testAppRxSchedulers,
            mapper,
            txRunner
        )
    }


}
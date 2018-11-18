package dadoufi.topmakelaar.data

import dadoufi.topmakelaar.data.daos.LastRequestDao
import dadoufi.topmakelaar.util.*
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class LastPageTest : BaseDatabaseTest() {
    lateinit var lastRequestDao: LastRequestDao

    @Before
    fun setUp() {
        super.setup()
        lastRequestDao = db.lastRequestDao()

    }


    @Test
    fun testMakelaarInsert() {
        lastRequestDao.insertReplace(lastRequest1)
        lastRequestDao.getLastRequest(amsterdam).test().assertValue(lastRequest1)


        lastRequestDao.insertReplace(lastRequest2)
        lastRequestDao.getLastRequest(amsterdam).test().assertValue(lastRequest2)
        assertThat(lastRequestDao.getCount(amsterdam), `is`(1))

        lastRequestDao.insertReplace(lastRequest3)
        lastRequestDao.getLastRequest(tuin).test().assertValue(lastRequest3)

        lastRequestDao.insertReplace(lastRequest4)
        lastRequestDao.getLastRequest(tuin).test().assertValue(lastRequest4)
        assertThat(lastRequestDao.getCount(tuin), `is`(1))
    }


}

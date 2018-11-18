package dadoufi.topmakelaar.data

import dadoufi.topmakelaar.data.daos.MakelaarDao
import dadoufi.topmakelaar.util.*
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class MakelaarTest : BaseDatabaseTest() {

    lateinit var makelaarDao: MakelaarDao

    @Before
    fun setUp() {
        super.setup()
        makelaarDao = db.makelaarDao()
    }


    @Test
    fun testMakelaarInsert() {
        makelaarDao.insertAll(makelaarEntity1, makelaarEntity2)
        assertThat(makelaarDao.getCount("amsterdam"), `is`(2))

        makelaarDao.insertAll(makelaarEntity5, makelaarEntity6)
        assertThat(makelaarDao.getCount("amsterdam/tuin"), `is`(2))
    }


    @Test
    fun testMakelaarDeleteAmsterdam() {
        makelaarDao.insertAll(makelaarEntity1, makelaarEntity2, makelaarEntity3, makelaarEntity4, makelaarEntity5)

        makelaarDao.deleteAllFiltered("amsterdam")
        assertThat(makelaarDao.getCount("amsterdam"), `is`(0))
        assertThat(makelaarDao.getCount("amsterdam/tuin"), `is`(1))
    }


    @Test
    fun testMakelaarDeleteAmsterdamTuin() {
        makelaarDao.insertAll(makelaarEntity1, makelaarEntity2, makelaarEntity3, makelaarEntity4, makelaarEntity5)

        makelaarDao.deleteAllFiltered("amsterdam/tuin")
        assertThat(makelaarDao.getCount("amsterdam"), `is`(4))
        assertThat(makelaarDao.getCount("amsterdam/tuin"), `is`(0))
    }


    @Test
    fun testMakelaarGetTopMakelaar() {
        makelaarDao.insertAll(makelaarList)

        makelaarDao.getTopMakelaar("amsterdam").test().assertValue(topMakelaarAmsterdam)
        makelaarDao.getTopMakelaar("amsterdam/tuin").test().assertValue(topMakelaarTuin)
    }

}
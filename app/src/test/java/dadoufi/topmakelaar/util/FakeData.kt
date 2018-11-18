package dadoufi.topmakelaar.util

import dadoufi.topmakelaar.data.entities.LastRequestEntity
import dadoufi.topmakelaar.data.entities.MakelaarEntity
import dadoufi.topmakelaar.data.entities.TopMakelaar


const val amsterdam = "amsterdam"
const val tuin = "amsterdam/tuin"

val makelaarEntity1 = MakelaarEntity(
    id = 1,
    mid = 1,
    name = "John",
    query = amsterdam,
    page = 1
)

val makelaarEntity2 = MakelaarEntity(
    id = 2,
    mid = 2,
    name = "John",
    query = amsterdam,
    page = 1
)

val makelaarEntity3 = MakelaarEntity(
    id = 3,
    mid = 3,
    name = "George",
    query = amsterdam,
    page = 2
)

val makelaarEntity4 = MakelaarEntity(
    id = 4,
    mid = 4,
    name = "Tom",
    query = amsterdam,
    page = 3
)


val makelaarEntity5 = MakelaarEntity(
    id = 5,
    mid = 5,
    name = "Mat",
    query = tuin,
    page = 1
)

val makelaarEntity6 = MakelaarEntity(
    id = 6,
    mid = 6,
    name = "John",
    query = tuin,
    page = 1
)


val makelaarEntity7 = MakelaarEntity(
    id = 7,
    mid = 7,
    name = "Mark",
    query = tuin,
    page = 1
)


val makelaarEntity8 = MakelaarEntity(
    id = 8,
    mid = 8,
    name = "Mark",
    query = tuin,
    page = 1
)


val makelaarList = listOf(
    makelaarEntity1,
    makelaarEntity2,
    makelaarEntity3,
    makelaarEntity4,
    makelaarEntity5,
    makelaarEntity6,
    makelaarEntity7,
    makelaarEntity8
)

val topMakelaarAmsterdam = listOf(TopMakelaar("John", 2), TopMakelaar("George", 1), TopMakelaar("Tom", 1))
val topMakelaarTuin = listOf(TopMakelaar("Mark", 2), TopMakelaar("John", 1), TopMakelaar("Mat", 1))


val lastRequest1 = LastRequestEntity(
    id = 1,
    query = amsterdam,
    requestPage = 1,
    totalPages = 125,
    timestamp = -1
)

val lastRequest2 = LastRequestEntity(
    id = 2,
    query = amsterdam,
    requestPage = 2,
    totalPages = 125,
    timestamp = 123434
)

val lastRequest3 = LastRequestEntity(
    id = 3,
    query = tuin,
    requestPage = 2,
    totalPages = 35,
    timestamp = 5656456345
)

val lastRequest4 = LastRequestEntity(
    id = 4,
    query = tuin,
    requestPage = 5,
    totalPages = 35,
    timestamp = 12313123
)





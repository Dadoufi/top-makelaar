package dadoufi.topmakelaar.data.mapper

import dadoufi.topmakelaar.data.entities.MakelaarEntity
import dadoufi.topmakelaar.remote.model.Makelaar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MakelaarToMakelaarEntity @Inject constructor() :
    PagedParamMapper<Makelaar, MakelaarEntity, String?> {
    override fun map(from: Makelaar, param: String?, page: Int) = MakelaarEntity(
        mid = from.makelaarId,
        name = from.makelaarName,
        query = param,
        page = page
    )

}
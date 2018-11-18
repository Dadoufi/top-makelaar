package dadoufi.topmakelaar.ui.makelaar

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.AutoModel
import com.airbnb.epoxy.TypedEpoxyController
import dadoufi.topmakelaar.data.entities.TopMakelaar
import dadoufi.topmakelaar.util.epoxy.common.EmptyModel_

class MakelaarController :
    TypedEpoxyController<List<TopMakelaar>>() {

    @AutoModel
    lateinit var emptyState: EmptyModel_


    init {
        isDebugLoggingEnabled = true
    }

    override fun onExceptionSwallowed(exception: RuntimeException) {
        throw exception
    }

    override fun buildModels(data: List<TopMakelaar>?) {
        data?.forEachIndexed { index, topMakelaar ->
            MakelaarModel_()
                .id(topMakelaar.hashCode())
                .count(topMakelaar.listings.toString())
                .name(topMakelaar.name)
                .addTo(this)
        }

        emptyState.addIf(data.isNullOrEmpty(), this)

    }

    public override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            (recyclerView.layoutManager as LinearLayoutManager).recycleChildrenOnDetach = true
        }
    }
}

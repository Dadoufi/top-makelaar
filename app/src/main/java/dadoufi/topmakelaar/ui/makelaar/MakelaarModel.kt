package dadoufi.topmakelaar.ui.makelaar

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import dadoufi.topmakelaar.R
import dadoufi.topmakelaar.util.epoxy.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_top_makelaar)
abstract class MakelaarModel : EpoxyModelWithHolder<MakelaarHolder>() {


    @EpoxyAttribute
    lateinit var name: String
    @EpoxyAttribute
    lateinit var count: String

    override fun bind(holder: MakelaarHolder) {
        with(holder) {

            nameTextView.text = name
            countView.text = count
        }

    }

}

class MakelaarHolder : KotlinEpoxyHolder() {
    val countView by bind<TextView>(R.id.count)
    val nameTextView by bind<TextView>(R.id.name)

}




package dadoufi.topmakelaar.util.epoxy.common

import android.content.Context
import android.util.AttributeSet
import com.airbnb.epoxy.EpoxyRecyclerView

class ClearEpoxyRecyclerView(context: Context?, attrs: AttributeSet?) :
    EpoxyRecyclerView(context, attrs) {

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        swapAdapter(null, true)
    }
}
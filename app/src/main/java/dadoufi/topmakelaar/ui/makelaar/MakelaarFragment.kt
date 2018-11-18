package dadoufi.topmakelaar.ui.makelaar


import android.os.Bundle
import android.view.View
import com.google.android.material.chip.Chip
import dadoufi.topmakelaar.R
import dadoufi.topmakelaar.base.BaseFragment
import dadoufi.topmakelaar.data.entities.TopMakelaar
import kotlinx.android.synthetic.main.fragment_top_makelaar.*
import kotlinx.android.synthetic.main.include_recycler_view.*


class MakelaarFragment : BaseFragment<List<TopMakelaar>, MakelaarViewModel>() {

    override val classToken: Class<MakelaarViewModel>
        get() = MakelaarViewModel::class.java

    private lateinit var controller: MakelaarController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = MakelaarController()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.run {
            setController(controller)
        }


        chipGroup.setOnCheckedChangeListener { chipGroup, i ->
            when (chipGroup.checkedChipId) {
                R.id.noFilterChip -> viewModel.setQuery("")
                R.id.tuinFilterChip -> viewModel.setQuery("tuin")
            }
        }

        if (savedInstanceState != null) {
            view.findViewById<Chip>(savedInstanceState.getInt("filter")).isChecked = true
        } else {
            noFilterChip.isChecked = true
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("filter", chipGroup.checkedChipId)
    }

    override fun handleSuccessState(data: List<TopMakelaar>?) {
        super.handleSuccessState(data)
        controller.setData(data)
    }


    override fun getLayoutId(): Int = R.layout.fragment_top_makelaar


}

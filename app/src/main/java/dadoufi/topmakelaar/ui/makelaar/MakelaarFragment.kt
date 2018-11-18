package dadoufi.topmakelaar.ui.makelaar


import android.os.Bundle
import android.view.View
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
            setItemSpacingRes(R.dimen.margin_normal)
        }



        tuinFilterChip.setOnCheckedChangeListener { buttonView, isChecked ->
            handleChecked(isChecked)
        }


        if (savedInstanceState != null) { //request data from view model
            handleChecked(savedInstanceState.getBoolean("filter", false))
        } else {
            viewModel.setQuery("")
        }


    }

    private fun handleChecked(isChecked: Boolean) {
        if (isChecked) {
            viewModel.setQuery("tuin")
        } else {
            viewModel.setQuery("")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(
            "filter",
            tuinFilterChip.isChecked
        ) // save selected filter onSavedInstance for orientation changes
    }

    override fun handleSuccessState(data: List<TopMakelaar>?) {
        super.handleSuccessState(data)
        controller.setData(data) //update recycler view with data
    }


    override fun getLayoutId(): Int = R.layout.fragment_top_makelaar


}

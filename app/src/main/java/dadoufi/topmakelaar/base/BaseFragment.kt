/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dadoufi.topmakelaar.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import dadoufi.topmakelaar.R
import dadoufi.topmakelaar.data.entities.UiState
import dadoufi.topmakelaar.util.ProgressTimeLatch
import dadoufi.topmakelaar.util.createSnackBar
import dadoufi.topmakelaar.util.observeK
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Base fragment class which supports LifecycleOwner and Dagger injection.
 */
abstract class BaseFragment<Result : Any, VM : BaseViewModel<Result>> :
    DaggerFragment() {

    var epoxyRecyclerView: EpoxyRecyclerView? = null
    lateinit var swipeRefreshLatch: ProgressTimeLatch

    private var startedTransition = false
    private var postponed = false
    private var hasPendingSnackBar: Snackbar? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: VM

    protected abstract val classToken: Class<VM>

    private lateinit var parentView: View
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(getLayoutId(), container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(classToken)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentView = view
        epoxyRecyclerView = view.findViewById(R.id.recyclerView)
        setupSwipeToRefresh()
        setUpAppbarElevation()


        if (savedInstanceState != null) {
            swipeRefreshLayout?.isRefreshing = savedInstanceState.getBoolean("refreshing", false)
        }

        viewModel.uiStateLiveData.observeK(this) {
            //observe State changes and handle ui
            handleUiState(it)
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        swipeRefreshLayout?.let {
            outState.putBoolean("refreshing", it.isRefreshing)
        }

    }

    private fun handleUiState(it: UiState<Result>?) {
        when (it) {
            is UiState.Loading -> {
                handleLoadingState()
            }
            is UiState.LoadMore -> {
                handleLoadMoreState()
            }
            is UiState.Error -> {
                handleErrorState(it)
            }
            is UiState.Success -> {
                handleSuccessState(it.data)
            }
        }
    }


    open fun handleSuccessState(data: Result?) {
        swipeRefreshLatch.refreshing = false
    }

    open fun handleErrorState(uiState: UiState.Error) {
        swipeRefreshLatch.refreshing = false
        when (uiState) {
            is UiState.Error.PagedError -> {
                handlePagedError()
            }
            is UiState.Error.RefreshError -> {

                createSnackBar(parentView, uiState) {
                    viewModel.callRefresh()
                }.show()

            }
            is UiState.Error.PagedEmptyError -> {
                handlePagedEmptyError()
            }
        }
    }


    open fun handlePagedError() = Unit

    open fun handlePagedEmptyError() = Unit

    open fun handleLoadMoreState() = Unit

    open fun handleLoadingState() {
        swipeRefreshLatch.refreshing = true

    }

    open fun setupSwipeToRefresh() { //create swipe to refresh with a minimum showing time and showing delay
        swipeRefreshLayout = parentView.findViewById(R.id.swipeRefresh)
        swipeRefreshLayout?.let { swipeRefreshLayout ->
            swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimaryDark,
                R.color.colorPrimary
            )
            swipeRefreshLatch = ProgressTimeLatch {
                swipeRefreshLayout.isRefreshing = it
                swipeRefreshLayout.setOnRefreshListener {
                    viewModel.callRefresh() //fetch fresh data
                    swipeRefreshLatch.refreshing = true
                }
            }

        }

    }

    private fun setUpAppbarElevation() {
        val appbar: AppBarLayout? = parentView.findViewById(R.id.appbar)
        appbar?.let {
            epoxyRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    appbar.isSelected = recyclerView.canScrollVertically(-1)
                }
            })
        }

    }

    abstract fun getLayoutId(): Int

    fun navController() = findNavController()
}
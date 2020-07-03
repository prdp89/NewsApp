package com.android.grab.newsapp.ui.main

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import com.android.grab.newsapp.R
import com.android.grab.newsapp.databinding.MainFragmentBinding
import com.android.grab.newsapp.datasource.roomdb.entity.NewsRowEntity
import com.android.grab.newsapp.ui.newsdetail.NewsDetailFragment
import com.android.grab.newsapp.utils.*
import com.android.grab.newsapp.utils.imagecache.ImageLoader
import com.android.grab.newsapp.vo.AppConstants
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject
import com.android.grab.newsapp.utils.PreferenceUtil.Companion as PreferenceUtil1

class MainFragment : DaggerFragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var mViewModel: MainViewModel

    @Inject
    lateinit var mAppExecutors: AppExecutors

    private var mBinding: MainFragmentBinding? = null

    private var mAdapter: NewsFeedAdapter? = null

    private var imageLoader: ImageLoader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dataBinding = DataBindingUtil.inflate<MainFragmentBinding>(
            inflater,
            R.layout.main_fragment,
            container,
            false
        )

        imageLoader =
            ImageLoader(context)

        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        mBinding = dataBinding
        return mBinding?.root!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as DaggerAppCompatActivity).setSupportActionBar(toolbar)

        mViewModel.triggerLiveData(false)

        initRecyclerView()
        subscribeLiveData()
        observeViewEvents()
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    //region private Methods
    private fun observeViewEvents() {

        mBinding?.swipeRefreshLayout?.setOnRefreshListener {
            //Added for Espresso Test
            //Toast.makeText(context, "On Refresh Called", Toast.LENGTH_SHORT).show()

            if (ApplicationUtils.isNetworkAvailable(context!!)) {
                mViewModel.refreshLiveData().observe(viewLifecycleOwner, Observer {
                    mViewModel.triggerLiveData(true)

                    mBinding?.rvNewsList?.visibility = View.GONE
                    mBinding?.tvEmpty?.visibility = View.INVISIBLE
                    mBinding?.tvEmpty?.text = getString(R.string.txt_loading)

                    imageLoader?.clearCache()
                })
            } else {
                ApplicationUtils.showSnackBar(mBinding?.root, getString(R.string.txt_alien))
            }
            swipe_refresh_layout.isRefreshing = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_bulb -> {
                // Get new UI mode.
                val mode =
                    if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                        Configuration.UI_MODE_NIGHT_NO
                    ) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    }

                //toggle the Shared-preference value.
                PreferenceUtil1[context!!, AppConstants.SharedPreferenceConstants.KEY_IS_DARK_MODE] =
                    !PreferenceUtil1[context!!, AppConstants.SharedPreferenceConstants.KEY_IS_DARK_MODE, false]

                AppCompatDelegate.setDefaultNightMode(mode)
                true
            }
            else -> true
        }
    }

    private fun initRecyclerView() {
        val adapter = NewsFeedAdapter(mAppExecutors, imageLoader!!) {
            //Added for Espresso Test
            //Toast.makeText(context, "On Item Click : " + it.title, Toast.LENGTH_SHORT).show()

            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.addSharedElement(
                    it.ivPerson,
                    ViewCompat.getTransitionName(it.ivPerson)!!
                )
                ?.addToBackStack(null)
                ?.replace(R.id.container, getTransition(it.entity?.id!!))
                ?.commit()
        }

        this.rv_news_list.adapter = adapter
        this.mAdapter = adapter
    }

    private fun getTransition(id: Int): NewsDetailFragment {
        val repoDetail = NewsDetailFragment.newInstance(id)

        repoDetail.sharedElementEnterTransition = NewsDetailTransition()
        repoDetail.sharedElementReturnTransition = NewsDetailTransition()

        repoDetail.enterTransition = Fade()
        repoDetail.exitTransition = Fade()

        return repoDetail
    }

    private fun subscribeLiveData() {
        mViewModel.newsLiveData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    mBinding?.shimmerViewContainer?.visibility = View.VISIBLE
                    mBinding?.shimmerViewContainer?.startShimmer()
                }
                Status.SUCCESS -> {
                    val result = it.data as List<NewsRowEntity>

                    if (result.isNotEmpty()) {
                        val filteredList =
                            result.filter { item -> null != item.title && !item.title.isNullOrBlank() }

                        mAdapter?.submitList(filteredList)
                        mAdapter?.notifyDataSetChanged()

                        mBinding?.rvNewsList?.visibility = View.VISIBLE
                        mBinding?.tvEmpty?.visibility = View.GONE
                    } else {
                        showEmptyLayout()
                    }
                    stopShimmer()
                }
                Status.ERROR -> {
                    showEmptyLayout()
                    stopShimmer()
                }
            }
        })
    }

    private fun stopShimmer() {
        mBinding?.shimmerViewContainer?.stopShimmer()
        mBinding?.shimmerViewContainer?.visibility = View.GONE
    }

    private fun showEmptyLayout() {
        mBinding?.rvNewsList?.visibility = View.GONE
        mBinding?.tvEmpty?.visibility = View.VISIBLE
        mBinding?.tvEmpty?.text = getString(R.string.it_s_empty_here)
    }
    //endregion
}

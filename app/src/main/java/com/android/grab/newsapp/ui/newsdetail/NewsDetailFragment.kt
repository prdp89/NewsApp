package com.android.grab.newsapp.ui.newsdetail

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.webkit.WebSettings
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.transition.TransitionInflater
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.android.grab.newsapp.R
import com.android.grab.newsapp.databinding.NewsDetailFragmentBinding
import com.android.grab.newsapp.utils.CustomWebViewClient
import com.android.grab.newsapp.utils.PreferenceUtil
import com.android.grab.newsapp.utils.Status
import com.android.grab.newsapp.utils.imagecache.ImageLoader
import com.android.grab.newsapp.vo.AppConstants
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject

class NewsDetailFragment : DaggerFragment() {

    companion object {
        private const val NEWS_ID = "newsId"
        fun newInstance(repoId: Int): NewsDetailFragment =
            NewsDetailFragment().apply {
                arguments = Bundle().apply {
                    this.putInt(NEWS_ID, repoId)
                }
            }
    }

    @Inject
    lateinit var viewModel: NewsDetailViewModel

    private var mBinding: NewsDetailFragmentBinding? = null

    private var mNewsId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            mNewsId = bundle.getInt(NEWS_ID)
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<NewsDetailFragmentBinding>(
            inflater,
            R.layout.news_detail_fragment,
            container,
            false
        )

        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        mBinding = dataBinding
        return mBinding?.root!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as DaggerAppCompatActivity).setSupportActionBar(toolbar)

        mNewsId?.let { viewModel.triggerLiveData(it) }

        mBinding?.webview?.setBackgroundColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorSilver
            )
        )

        subscribeLiveData()
        observeViewEvents()
    }

    private fun subscribeLiveData() {
        viewModel.newsLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == Status.SUCCESS && null != it.data) {
                mBinding?.entity = it.data

                mBinding?.executePendingBindings()
                startPostponedEnterTransition()

                ImageLoader(context)
                    .displayImage(it.data.urlToImage!!, mBinding?.ivCategory!!)

                configureWebView()

                mBinding?.webview?.webViewClient = CustomWebViewClient(mBinding?.progressbar!!)

                mBinding?.webview?.loadUrl(it.data.url)
            } else if (it.status == Status.SUCCESS && null == it.data) {
                mBinding?.progressbar?.visibility = View.INVISIBLE
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        val webSettings = mBinding?.webview?.settings
        webSettings?.javaScriptEnabled = true

        webSettings?.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings?.setAppCacheEnabled(true)
        webSettings?.setAppCachePath(context?.cacheDir?.path)

        mBinding?.webview?.settings?.displayZoomControls = true
        mBinding?.webview?.settings?.builtInZoomControls = true

        mBinding?.webview?.canGoBack()
        mBinding?.webview?.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK
                && event.action === MotionEvent.ACTION_UP
                && mBinding?.webview?.canGoBack()!!
            ) {
                mBinding?.webview?.goBack()
                return@OnKeyListener true
            }
            false
        })

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            if (PreferenceUtil[context!!, AppConstants.SharedPreferenceConstants.KEY_IS_DARK_MODE, false])
                WebSettingsCompat.setForceDark(
                    mBinding?.webview?.settings!!,
                    WebSettingsCompat.FORCE_DARK_ON
                )
            else
                WebSettingsCompat.setForceDark(
                    mBinding?.webview?.settings!!,
                    WebSettingsCompat.FORCE_DARK_OFF
                )
        }
    }

    private fun observeViewEvents() {
        mBinding?.ivBack?.setOnClickListener {
            if (mBinding?.webview?.canGoBack()!!)
                mBinding?.webview?.goBack()
            else
                this.activity?.supportFragmentManager?.popBackStack()
        }
    }
}

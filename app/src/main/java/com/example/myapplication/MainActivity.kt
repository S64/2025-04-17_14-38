package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.fluct.fluctsdk.FluctAdSize
import jp.fluct.fluctsdk.FluctAdView
import jp.fluct.fluctsdk.FluctErrorCode
import java.util.UUID

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {

        private const val TAG = "MainActivity"

        private const val GROUP_ID = "1000158639"
        private const val UNIT_ID = "1000254228"
        private val AD_SIZE = FluctAdSize.LARGE_BANNER

    }

    private val placeNewContainerButton: Button by lazy { findViewById(R.id.placeNewContainerButton) }
    private val removeContainerButton: Button by lazy { findViewById(R.id.removeContainerButton) }

    private val placeNewAdView: Button by lazy { findViewById(R.id.placeNewAdView) }

    private val removeAdView: Button by lazy { findViewById(R.id.removeAdView) }
    private val removeAdViewNotice: TextView by lazy { findViewById(R.id.removeAdViewNotice) }

    private val loadAdView: Button by lazy { findViewById(R.id.loadAdView) }
    private val loadAdViewNotice: TextView by lazy { findViewById(R.id.loadAdViewNotice) }

    private val unloadAdView: Button by lazy { findViewById(R.id.unloadAdView) }

    private val placeholderNotice: TextView by lazy { findViewById(R.id.placeholderNotice) }
    private val placeholder: FrameLayout by lazy { findViewById(R.id.placeholder) }

    private val logRecycler: RecyclerView by lazy { findViewById(R.id.logRecycler) }

    private val logAdapter = LogAdapter()
    private var currentContainer: FrameLayout? = null
    private var currentAdView: FluctAdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()

        placeNewContainerButton.setOnClickListener {
            if (currentContainer != null) {
                TODO()
            }

            currentContainer = FrameLayout(placeholder.context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }.also {
                placeholder.addView(it)
            }

            log("--- ${placeNewContainerButton.text} ---")
            refreshUiStatus()
        }

        removeContainerButton.setOnClickListener {
            currentContainer?.let { container ->
                placeholder.removeView(container)
                currentContainer = null
            } ?: TODO()

            log("--- ${removeContainerButton.text} ---")
            refreshUiStatus()
        }

        placeNewAdView.setOnClickListener {
            if (currentAdView != null) {
                TODO()
            }

            currentContainer?.let { container ->
                currentAdView = FluctAdView(
                    container.context,
                    GROUP_ID,
                    UNIT_ID,
                    AD_SIZE,
                    null,
                    newListener()
                ).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                }.also {
                    container.addView(it)
                }
            } ?: TODO()

            log("--- ${placeNewAdView.text} ---")
            refreshUiStatus()
        }

        removeAdView.setOnClickListener {
            currentContainer?.let { container ->
                currentAdView?.let { adView ->
                    container.removeView(adView)
                    currentAdView = null
                } ?: TODO()
            } ?: TODO()

            log("--- ${removeAdView.text} ---")
            refreshUiStatus()
        }

        loadAdView.setOnClickListener {
            currentAdView?.let { adView ->
                adView.loadAd()
            } ?: TODO()

            log("--- ${loadAdView.text} ---")
            refreshUiStatus()
        }

        unloadAdView.setOnClickListener {
            currentAdView?.let { adView ->
                adView.unloadAd()
            } ?: TODO()

            log("--- ${unloadAdView.text} ---")
            refreshUiStatus()
        }

        logRecycler.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = logAdapter
        }

        refreshUiStatus()
    }

    private fun initActivity() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshUiStatus() {
        currentContainer.let { container ->
            (container != null).let { containerAttached ->
                placeNewContainerButton.isEnabled = !containerAttached
                removeContainerButton.isEnabled = containerAttached
                placeholderNotice.text = "Container view: ${ if (containerAttached) System.identityHashCode(container) else "N/A" }"
                currentAdView.let { adView ->
                    removeAdViewNotice.visibility = if (adView?.isUnloaded == false) View.VISIBLE else View.GONE
                    loadAdViewNotice.visibility = if (adView?.isLoaded == true) View.VISIBLE else View.GONE
                    (adView != null).let { adViewAttached ->
                        placeNewAdView.isEnabled = containerAttached && !adViewAttached
                        removeAdView.isEnabled = adViewAttached
                        loadAdView.isEnabled = adViewAttached
                        unloadAdView.isEnabled = adViewAttached
                    }
                }
            }
        }

    }

    private fun log(msg: String) {
        Log.d(TAG, msg)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        logAdapter.append(msg)
    }

    private fun newListener(): FluctAdView.Listener {
        val prefix = "[${UUID.randomUUID()}]"
        return object : FluctAdView.Listener {
            override fun onLoaded() {
                log("$prefix onLoaded")
                refreshUiStatus()
            }

            override fun onFailedToLoad(p0: FluctErrorCode) {
                log("$prefix onFailedToLoad")
                refreshUiStatus()
            }

            override fun onLeftApplication() {
                log("$prefix onLeftApplication")
                refreshUiStatus()
            }

            override fun onUnloaded() {
                log("$prefix onUnloaded")
                refreshUiStatus()
            }
        }
    }

}

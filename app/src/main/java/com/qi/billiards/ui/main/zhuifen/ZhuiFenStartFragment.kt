package com.qi.billiards.ui.main.zhuifen

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.navArgs
import com.qi.billiards.R
import com.qi.billiards.ui.base.BaseFragment

private const val TAG = "ZhuiFenStartFragment"

class ZhuiFenStartFragment : BaseFragment() {
    private val args: ZhuiFenStartFragmentArgs by navArgs()
    val game by lazy { args.zhuiFenGame }

    private val wakeLock by lazy {
        val powerManager =
            getSystemService(requireContext(), PowerManager::class.java) as PowerManager
        powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "ZhuiFenStartFragment::MyWakeLockTag"
        )
    }

    override fun getLayoutId() = R.layout.fragment_zhuifen_start

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        wakeLock.setReferenceCounted(false)
        wakeLock.acquire(10 * 60 * 1000L)

        initView(view)
        Log.d(TAG, "onViewCreated: $game")
    }

    private fun initView(view: View) {

        view.findViewById<TextView>(R.id.tv_foul).setOnClickListener {

        }
        view.findViewById<TextView>(R.id.tv_foul_r).setOnClickListener {

        }
        view.findViewById<TextView>(R.id.tv_win).setOnClickListener {

        }
        view.findViewById<TextView>(R.id.tv_win_r).setOnClickListener {

        }
        view.findViewById<TextView>(R.id.tv_xiaojin).setOnClickListener {

        }
        view.findViewById<TextView>(R.id.tv_xiaojin_r).setOnClickListener {

        }
        view.findViewById<TextView>(R.id.tv_dajin).setOnClickListener {

        }
        view.findViewById<TextView>(R.id.tv_undo).setOnClickListener {

        }

        view.findViewById<TextView>(R.id.tv_next).setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        wakeLock.release()
    }

}
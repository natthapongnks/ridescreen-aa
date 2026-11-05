// SPDX-License-Identifier: AGPL-3.0-or-later
// Copyright (C) 2026 Alexandru <https://alexandru.rocks> and the OpenCfMoto contributors.
// Part of OpenCfMoto. Free software under the GNU AGPL v3 or later; see LICENSE and NOTICE.
package dev.zanderp.opencfmoto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity

/** ~1.8s MTL mark: pulse, light wipe, then fade into home. Tap to skip. Later launches skip it. */
class SplashActivity : AppCompatActivity() {

    private val prefs by lazy { getSharedPreferences("mtl_splash", MODE_PRIVATE) }
    private var gone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (prefs.getBoolean(KEY_SEEN, false)) {
            openHome()
            return
        }
        setContentView(R.layout.activity_splash)
        val root = findViewById<View>(R.id.splash_root)
        val mark = findViewById<View>(R.id.splash_mark)
        val ring = findViewById<ConnectionRingView>(R.id.splash_ring)
        val wipe = findViewById<View>(R.id.splash_wipe)
        val sub = findViewById<View>(R.id.splash_sub)
        ring.useNetflixRed()
        ring.setFraction(0.85f)
        mark.scaleX = 0.72f
        mark.scaleY = 0.72f
        mark.alpha = 0f
        mark.animate().scaleX(1.06f).scaleY(1.06f).alpha(1f).setDuration(560)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                mark.animate().scaleX(1f).scaleY(1f).setDuration(500).start()
            }.start()
        wipe.translationX = 180f * resources.displayMetrics.density
        wipe.animate().translationX(-180f * resources.displayMetrics.density).alpha(0.2f)
            .setStartDelay(150).setDuration(1200).start()
        sub.animate().alpha(1f).translationY(0f).setStartDelay(550).setDuration(480).start()
        root.setOnClickListener { finishSplash() }
        root.postDelayed({ finishSplash() }, 1800)
    }

    private fun finishSplash() {
        if (gone) return
        gone = true
        prefs.edit().putBoolean(KEY_SEEN, true).apply()
        val root = findViewById<View?>(R.id.splash_root) ?: run { openHome(); return }
        root.animate().alpha(0f).setDuration(420).withEndAction { openHome() }.start()
    }

    private fun openHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    companion object {
        private const val KEY_SEEN = "seen"
    }
}

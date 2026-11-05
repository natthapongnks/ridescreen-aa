// SPDX-License-Identifier: AGPL-3.0-or-later
// Copyright (C) 2026 Alexandru <https://alexandru.rocks> and the OpenCfMoto contributors.
// Part of OpenCfMoto. Free software under the GNU AGPL v3 or later; see LICENSE and NOTICE.
package dev.zanderp.opencfmoto

import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity

/** Short branded intro. Skipped after the first completed play. Tap to skip. */
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
        val root = findViewById<android.view.View>(R.id.splash_root)
        val ring = findViewById<ConnectionRingView>(R.id.splash_ring)
        val word = findViewById<android.view.View>(R.id.splash_word)
        ring.setFraction(0.15f)
        ring.animate().scaleX(1.08f).scaleY(1.08f).setDuration(900)
            .setInterpolator(AccelerateDecelerateInterpolator()).withEndAction {
                ring.animate().scaleX(1f).scaleY(1f).setDuration(700).start()
            }.start()
        word.alpha = 0f
        word.animate().alpha(1f).setDuration(700).start()
        root.setOnClickListener { finishSplash() }
        root.postDelayed({ finishSplash() }, 2000)
    }

    private fun finishSplash() {
        if (gone) return
        gone = true
        prefs.edit().putBoolean(KEY_SEEN, true).apply()
        val root = findViewById<android.view.View?>(R.id.splash_root) ?: run { openHome(); return }
        root.animate().alpha(0f).setDuration(280).withEndAction { openHome() }.start()
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

package dev.delath.housha.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Applies status-bar top inset to [rootView] and bottom inset to [scrollableViews].
 * [bottomNavView] is measured at dispatch time so the last list item always clears it,
 * even on the very first layout pass.
 */
fun applySystemBarInsets(
    rootView: View,
    vararg scrollableViews: View,
    bottomNavView: View? = null
) {
    val applyInsets = {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBar    = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(top = statusBar.top)
            val extraBottom = bottomNavView?.height ?: 0
            scrollableViews.forEach { it.updatePadding(bottom = navBar.bottom + extraBottom) }
            insets
        }
        ViewCompat.requestApplyInsets(rootView)
    }

    if (bottomNavView != null && bottomNavView.height == 0) {
        // Bottom nav not yet measured — wait for it, then register + dispatch
        bottomNavView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, l: Int, t: Int, r: Int, b: Int,
                                        ol: Int, ot: Int, or2: Int, ob: Int) {
                if (v.height > 0) {
                    v.removeOnLayoutChangeListener(this)
                    applyInsets()
                }
            }
        })
    } else {
        applyInsets()
    }
}

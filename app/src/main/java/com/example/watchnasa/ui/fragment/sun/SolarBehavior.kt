package com.example.watchnasa.ui.fragment.sun

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class SolarBehavior(context: Context, attr: AttributeSet): CoordinatorLayout.Behavior<View>(context, attr) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        val appbar = dependency as AppBarLayout
        if (abs(appbar.y) > appbar.height * 2 / 3) {
            child.visibility = View.GONE
        } else {
            child.visibility = View.VISIBLE
            val rate = (appbar.height + appbar.y) / appbar.height
            child.alpha = rate
            child.scaleX = rate
            child.scaleY = rate
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }


}
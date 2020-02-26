package com.lucidity.haolu.duelking

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lucidity.haolu.duelking.databinding.ActivityBottomNavigationBinding
import java.util.*

class BottomNavController(
    private val activity: AppCompatActivity,
    private val binding: ActivityBottomNavigationBinding,
//    private val navDestinations: Array<Int>,
    private val navHostMap: Map<Int, Int>,
    private val startNavHostId: Int,
    private val bottomNavView: BottomNavigationView
) {

    private val navControllerMap: MutableMap<Int, NavController> = mutableMapOf()
    private val navHostFragmentMap: MutableMap<Int, View> = mutableMapOf()
    private var currNavController: NavController? = null

    private val stack = Stack<Int>().apply { push(startNavHostId) }
    private var currDestination: Int? = startNavHostId

    init {
        navHostMap.forEach { nav ->
            navControllerMap.put(nav.key, findNavController(activity, nav.value))
            navHostFragmentMap.put(nav.key, activity.findViewById(nav.value))
        }
//        navDestinations.forEach { navId ->
//            navControllerMap.put(navId, findNavController(activity, navId))
//            navHostFragmentMap.put(navId, activity.findViewById(navId))
//        }
    }

    fun onBackPressed() {
        currNavController?.let { navController ->
            if (navController.currentDestination?.id == navController.graph.startDestination) {
                if (stack.isNotEmpty()) {
                    var navDestId = stack.pop()
                    switchDestination(stack.peek())
//                    bottomNavView.selectedItemId = R.id.nav_search
                    bottomNavView.selectedItemId = stack.peek()
//                    bottomNavView.menu.findItem(stack.peek()).isChecked = true
                } else {
                    activity.finish()
                }
            } else {
                navController.popBackStack()
            }
//            if (navController.currentDestination == null || navController.currentDestination?.id == navHostMap[stack.peek()]) {
//                if (stack.isNotEmpty()) {
//                    val navDestId = stack.pop()
////                    switchDestination(navDestId)
//                    bottomNavView.menu.findItem(navDestId).isChecked = true
//                } else {
//                    activity.finish()
//                }
//            }
//            navController.popBackStack()
        } ?: run {
            activity.finish()
        }
    }

    fun switchDestination(navId: Int) {
        currNavController = navControllerMap[navId]
        navHostFragmentMap[navId]?.let { navHostFragment ->
            invisibleTabContainerExcept(navHostFragment)
        }
        stack.push(navId)
    }

    private fun invisibleTabContainerExcept(navHostFragment: View) {
        navHostFragmentMap.forEach { navHost ->
//            navHost.value.visibility = View.INVISIBLE
            navHost.value.isInvisible = true
        }
        navHostFragment.isInvisible = false
    }

}
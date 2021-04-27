package com.common.tool.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import java.util.*

/**
 * @author 李雄厚
 *
 * @features 如果使用navigate库里面的FragmentNavigator在返回上个页面时候会重新创建，自定义一个防止重新创建
 */
@Navigator.Name("fixFragment")
class FixFragmentNavigator(context: Context, manager: FragmentManager, containerId: Int) :
    FragmentNavigator(context, manager, containerId) {
    companion object{
        private val TAG = FixFragmentNavigator::class.java.simpleName
    }
    private val mContext: Context = context
    private val mFragmentManager: FragmentManager = manager
    private val mContainerId = containerId

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        if (mFragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already"
                    + " saved its state")
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = mContext.packageName + className
        }
        val ft = mFragmentManager.beginTransaction()


        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1){
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }
        //当前正在显示的fragment
        val fragment = mFragmentManager.primaryNavigationFragment
        if (fragment != null) {
            ft.hide(fragment)
        }

        var frag: Fragment? = null
        val tag = destination.id.toString()
        frag = mFragmentManager.findFragmentByTag(tag)
        if (frag != null) {
            ft.show(frag)
        } else {
            frag = instantiateFragment(mContext, mFragmentManager, className, args)
            frag.arguments = args
            ft.add(mContainerId, frag, tag)
        }

        ft.setPrimaryNavigationFragment(frag)
        var mBackStack: ArrayDeque<Int>? = null
        @IdRes val destId = destination.id
        try {
            val field = FragmentNavigator::class.java.getDeclaredField("mBackStack")
            field.isAccessible = true
            mBackStack = field.get(this) as ArrayDeque<Int>
            field.isAccessible = false
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        val initialNavigation = mBackStack!!.isEmpty()
        // TODO Build first class singleTop behavior for fragments
        val isSingleTopReplacement = (navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId)
        val isAdded: Boolean
        if (initialNavigation) {
            isAdded = true
        } else if (isSingleTopReplacement) {
            // Single Top means we only want one instance on the back stack
            if (mBackStack.size > 1) {
                // If the Fragment to be replaced is on the FragmentManager's
                // back stack, a simple replace() isn't enough so we
                // remove it from the back stack and put our replacement
                // on the back stack in its place
                mFragmentManager.popBackStack(
                    generateBackStackName(mBackStack.size, mBackStack.peekLast()),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
            }
            isAdded = false
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
            isAdded = true
        }
        if (navigatorExtras is Extras) {
            val extras: Extras = navigatorExtras
            for (sharedElement: Map.Entry<View, String>  in extras.sharedElements.entries) {
                ft.addSharedElement(sharedElement.key, sharedElement.value)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()
        // The commit succeeded, update our view of the world
        return if (isAdded) {
            mBackStack.add(destId)
            destination
        } else {
            null
        }
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int): String? {
        return "$backStackIndex-$destId"
    }

}
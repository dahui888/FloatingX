package com.petterp.floatingx.assist.helper

import android.app.Activity
import android.app.Application
import com.petterp.floatingx.assist.FxLifecycleExpand
import com.petterp.floatingx.util.FxScopeEnum
import com.petterp.floatingx.util.navigationBarHeight
import com.petterp.floatingx.util.statusBarHeight

/** AppHelper构建器 */
class AppHelper(
    val application: Application,
    val blackList: MutableList<Class<*>>?,
    val enableAllBlackClass: Boolean,
    val fxLifecycleExpand: FxLifecycleExpand?
) : BasisHelper() {

    internal fun updateNavigationBar(activity: Activity?) {
        navigationBarHeight = activity?.navigationBarHeight ?: navigationBarHeight
        fxLog?.v("system-> navigationBar-$navigationBarHeight")
    }

    internal fun updateStatsBar(activity: Activity?) {
        statsBarHeight = activity?.statusBarHeight ?: statsBarHeight
        fxLog?.v("system-> statusBarHeight-$statsBarHeight")
    }

    class Builder : BasisHelper.Builder<Builder, AppHelper>() {
        private var application: Application? = null
        private var blackList: MutableList<Class<*>>? = null
        private var fxLifecycleExpand: FxLifecycleExpand? = null
        private var enableAllBlackClass: Boolean = false

        fun setContext(application: Application): Builder {
            this.application = application
            return this
        }

        /**
         * 设置显示悬浮窗的Activity生命周期回调
         * @param tagActivityLifecycle 生命周期实现类回调
         * */
        fun setTagActivityLifecycle(tagActivityLifecycle: FxLifecycleExpand.() -> Unit): Builder {
            this.fxLifecycleExpand = FxLifecycleExpand().apply(tagActivityLifecycle)
            return this
        }

        /** 添加允许显示悬浮窗的activity类
         * @param c 允许显示的activity类
         * */
        fun addBlackClass(vararg c: Class<out Activity>): Builder {
            if (blackList == null) blackList = mutableListOf()
            blackList?.addAll(c)
            return this
        }

        /**
         * 允许所有activity都显示全局悬浮窗
         * @param isEnable 默认false
         *
         * 启用了这个方法,addBlackClass就不用设置了
         * */
        fun setEnableAllBlackClass(isEnable: Boolean): Builder {
            enableAllBlackClass = isEnable
            return this
        }

        override fun buildHelper(): AppHelper =
            if (application == null)
                throw NullPointerException("To build AppHelper, you must set application!")
            else AppHelper(application!!, blackList, enableAllBlackClass, fxLifecycleExpand)

        override fun build(): AppHelper {
            val helper = super.build()
            helper.initLog(FxScopeEnum.APP_SCOPE.tag)
            return helper
        }
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}

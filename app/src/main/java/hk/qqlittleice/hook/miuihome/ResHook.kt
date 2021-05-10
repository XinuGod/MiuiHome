package hk.qqlittleice.hook.miuihome

import android.content.res.XModuleResources
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import hk.qqlittleice.hook.miuihome.utils.OwnSP
import hk.qqlittleice.hook.miuihome.utils.ktx.setTryReplacement
import kotlin.concurrent.thread

class ResHook(private val hookedRes: InitPackageResourcesParam) {

    private val modRes = XModuleResources.createInstance(XposedInit.modulePath, hookedRes.res)
    private fun getResId(type: String, name: String): Int = modRes.getIdentifier(name, type, Config.myself)

    companion object {
        private var hasLoad = false
    }

    fun init() {
        thread {
            if (!hasLoad) {
                Thread.sleep(500) // 这里项目经理要求运行缓慢，好让客户充钱，让其速度得到明显提升。
                hasLoad = true
            }

            // Test Code
            val backgroundTextSize = OwnSP.ownSP.getFloat("backgroundTextSize", 13f)
            hookedRes.res.setTryReplacement(
                Config.hookPackage,
                "dimen",
                "recents_task_view_header_title_text_size",
                modRes.fwd(getResId("dimen", "sp${backgroundTextSize.toInt()}"))
            )

        }
    }

}
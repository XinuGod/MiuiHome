package hk.qqlittleice.hook.miuihome.view

import android.app.AlertDialog
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.*
import hk.qqlittleice.hook.miuihome.HomeContext
import hk.qqlittleice.hook.miuihome.utils.LogUtil
import hk.qqlittleice.hook.miuihome.utils.OwnSP
import hk.qqlittleice.hook.miuihome.utils.dp2px

class SettingSeekBarDialog(private val mText: String,
                           private val mKey: String,
                           private val minValue: Int,
                           private val maxValue: Int,
                           private val minText: String,
                           private val maxText: String,
                           private val divide: Int = 100,
                           private val canUserInput: Boolean) {

    private val sharedPreferences = OwnSP.ownSP
    private val editor by lazy { sharedPreferences.edit() }

    private fun userInputDialog() {
        lateinit var editText: EditText
        val dialogBuilder = AlertDialog.Builder(HomeContext.activity)
        dialogBuilder.setView(ScrollView(HomeContext.activity).apply {
            overScrollMode = 2
            addView(LinearLayout(HomeContext.activity).apply {
                orientation = LinearLayout.VERTICAL
                addView(SettingTextView.FastBuilder(mText = "请输入[${mText}]的值：", mSize = SettingTextView.textSize).build())
                addView(EditText(HomeContext.context).apply {
                    hint = if (divide != 1) "输入的值会被除以$divide" else ""
                    editText = this
                    inputType = EditorInfo.TYPE_CLASS_NUMBER
                })
                addView(SettingTextView.FastBuilder(mText = "可输入的最小值为：$minValue", mSize = SettingTextView.text2Size).build())
                addView(SettingTextView.FastBuilder(mText = "可输入的最大值为：$maxValue", mSize = SettingTextView.text2Size).build())
            })
        })
        dialogBuilder.setPositiveButton("保存") { dialog, _ ->
            if (saveValue(editText.text.toString().toFloat() / divide)) {
                dialog.dismiss()
            } else{
                LogUtil.toast("设置成功！")
            }
        }
        dialogBuilder.setNeutralButton("取消") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.show()
    }

    fun saveValue(value: Float): Boolean {
        if ((value < (minValue.toFloat() / divide)) or (value > (maxValue.toFloat() / divide))) {
            LogUtil.toast("输入的值大于或小于允许设定的值！")
            return false
        }
        editor.putFloat(mKey, value)
        editor.apply()
        return true
    }

    fun build(): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(HomeContext.activity)
        var tempValue: Float = sharedPreferences.getFloat(mKey, 0f)
        lateinit var valueTextView: TextView
        lateinit var dialog: AlertDialog
        dialogBuilder.setView(ScrollView(HomeContext.activity).apply {
            overScrollMode = 2
            addView(LinearLayout(HomeContext.activity).apply {
                orientation = LinearLayout.VERTICAL
                addView(SettingTextView.FastBuilder(mText = mText, mSize = SettingTextView.text2Size).build())
                addView(SeekBar(HomeContext.context).apply {
                    min = minValue
                    max = maxValue
                    progress = (tempValue * divide).toInt()
                    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            saveValue(progress.toFloat() / divide)
                            valueTextView.text = "${tempValue}f"
                            tempValue = (progress.toFloat() / divide)
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    })
                })
                addView(LinearLayout(HomeContext.context).apply {
                    addView(TextView(HomeContext.context).apply {
                        text = minText
                        layoutParams = LinearLayout.LayoutParams(70, LinearLayout.LayoutParams.MATCH_PARENT)
                    })
                    addView(TextView(HomeContext.context).apply {
                        text = "${tempValue}f"
                        weightSum = 1f
                        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        valueTextView = this
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                    })
                    addView(TextView(HomeContext.context).apply {
                        text = maxText
                        textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
                        layoutParams = LinearLayout.LayoutParams(70, LinearLayout.LayoutParams.MATCH_PARENT)
                    })
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    gravity = Gravity.CENTER_VERTICAL
                    (this.layoutParams as LinearLayout.LayoutParams).apply {
                        topMargin = dp2px(HomeContext.context, 5f)
                        setPaddingRelative(dp2px(HomeContext.context, 12f), dp2px(HomeContext.context, 6f), dp2px(HomeContext.context, 12f), dp2px(HomeContext.context, 5f))
                    }
                })
                if (canUserInput) {
                    addView(SettingTextView.FastBuilder(mText = "手动输入", mSize = SettingTextView.text2Size) {
                        dialog.dismiss()
                        userInputDialog()
                    }.build())
                }
            })
        })
        dialog = dialogBuilder.show()
        return dialog
    }
}
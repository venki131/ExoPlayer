package com.example.exovideoplayer.dynamic_icon

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.exovideoplayer.R
import com.example.exovideoplayer.databinding.ActivityDynamicIconBinding

class DynamicIconActivity : AppCompatActivity() {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityDynamicIconBinding.inflate(layoutInflater)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        val iconName = "google_logo"
        //setAppIcon(iconName)
        setAppIconOnClick()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setAppIcon(iconName: String) {
        val packageName = packageName
        val componentName = ComponentName(packageName, "$packageName.dynamic_icon.DynamicIconActivity")

        val resId = resources.getIdentifier(iconName, "drawable", packageName)
        if (resId != 0) {
            val iconDrawable = resources.getDrawable(resId, theme)
            val bitmap = (iconDrawable as BitmapDrawable).bitmap

            val iconBitmap = Bitmap.createScaledBitmap(bitmap, 192, 192, false)
            val adaptiveIcon = Icon.createWithAdaptiveBitmap(iconBitmap)

            val shortcutManager = getSystemService(ShortcutManager::class.java)
            val dynamicShortcut = ShortcutInfo.Builder(this, "dynamicShortcut")
                .setIcon(adaptiveIcon)
                .setShortLabel(getString(R.string.app_name))
                .setIntent(Intent(Intent.ACTION_MAIN).setComponent(componentName))
                .build()

            shortcutManager?.dynamicShortcuts = listOf(dynamicShortcut)
        }

        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun setAppIconOnClick() {
        viewBinding.one.setOnClickListener{
            packageManager.setComponentEnabledSetting(ComponentName(this@DynamicIconActivity, OneLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(ComponentName(this@DynamicIconActivity, TwoLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(ComponentName(this@DynamicIconActivity, ThreeLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP)

            Toast.makeText(this, "Launcher one has been applied successfully",Toast.LENGTH_SHORT).show()
        }

        viewBinding.two.setOnClickListener{
            packageManager.setComponentEnabledSetting(ComponentName(this@DynamicIconActivity, OneLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(ComponentName(this@DynamicIconActivity, TwoLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(ComponentName(this@DynamicIconActivity, ThreeLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP)
            Toast.makeText(this, "Launcher two has been applied successfully",Toast.LENGTH_SHORT).show()
        }

        viewBinding.three.setOnClickListener{
            packageManager.setComponentEnabledSetting(ComponentName(this@DynamicIconActivity, OneLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(ComponentName(this@DynamicIconActivity, TwoLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(ComponentName(this@DynamicIconActivity, ThreeLauncherAlias::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP)
            Toast.makeText(this, "Launcher three has been applied successfully",Toast.LENGTH_SHORT).show()
        }
    }
}
package com.chat.androidaddtoviewdemo

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.chat.androidaddtoviewdemo.databinding.ActivityMainBinding
import io.flutter.FlutterInjector
import io.flutter.embedding.android.ExclusiveAppComponent
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.platform.PlatformPlugin

class MainActivity : AppCompatActivity(), ExclusiveAppComponent<Activity>{
    private lateinit var binding: ActivityMainBinding
    private lateinit var engine: FlutterEngine
    private var platformPlugin: PlatformPlugin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        engine = FlutterEngine(this)
        engine.dartExecutor.executeDartEntrypoint(

            DartExecutor.DartEntrypoint(
                FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                "nativeLoad"
            )
        )

        engine.activityControlSurface.attachToActivity(
            this, this.lifecycle
        )

        binding.flutterView.attachToFlutterEngine(engine)
    }


    override fun onDestroy() {
        super.onDestroy()
        detachFlutterView()
    }

    private fun detachFlutterView() {
        engine.activityControlSurface.detachFromActivity()
        engine.lifecycleChannel.appIsDetached()
        binding.flutterView.detachFromFlutterEngine()
        platformPlugin?.destroy()
        platformPlugin = null
    }

    override fun detachFromFlutterEngine() {
        detachFlutterView()
    }

    override fun getAppComponent(): ComponentActivity {
        return this
    }

    override fun onResume() {
        super.onResume()
        engine.lifecycleChannel.appIsResumed()
    }

    override fun onPause() {
        super.onPause()
        engine.lifecycleChannel.appIsPaused()
    }


    override fun onBackPressed() {
        engine.navigationChannel.popRoute()
    }



}
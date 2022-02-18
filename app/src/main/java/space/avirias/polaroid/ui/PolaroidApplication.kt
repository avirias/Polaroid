package space.avirias.polaroid.ui

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import space.avirias.polaroid.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class PolaroidApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
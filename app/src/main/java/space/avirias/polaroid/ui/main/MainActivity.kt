package space.avirias.polaroid.ui.main

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import space.avirias.polaroid.R
import space.avirias.polaroid.databinding.ActivityMainBinding
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment

        navController = navHostFragment.navController

        viewModel.shouldOpenEdit
            .onEach { (shouldShow, uri) ->
                if (shouldShow && uri != null) startEditActivity(uri)
            }.launchIn(lifecycleScope)
    }

    private fun startEditActivity(sourceUri: Uri) {
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val destinationFilename = "$name.jpg"
        val destinationFile = File(filesDir, destinationFilename)
        val destinationUri = Uri.fromFile(destinationFile)
        val intent = UCrop.of(sourceUri, destinationUri)
            .getIntent(this).apply {
                flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
        cropImageResult.launch(intent)
    }

    private val cropImageResult = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Timber.d("Result came")
            val output = UCrop.getOutput(result.data!!)
            val contentValues = ContentValues().apply {
                val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis())
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Polaroid")
            }
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let {
                val bytes = output?.toFile()?.readBytes()
                contentResolver.openOutputStream(it)
                    .use { stream ->
                        stream ?: return@use
                        stream.write(bytes)
                    }
                viewModel.shouldOpenEditPage(false to null)
                viewModel.getAllPhotos()
            }
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
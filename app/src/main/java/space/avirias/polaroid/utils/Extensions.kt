package space.avirias.polaroid

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

val Context.hasCameraPermission: Boolean
    get() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED


fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun <T> Cursor.toList(
    block: Cursor.() -> T,
): List<T> = use {
    buildList {
        while (moveToNext()) {
            add(block())
        }
    }
}
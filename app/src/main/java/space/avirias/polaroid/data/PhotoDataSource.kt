package space.avirias.polaroid.data

import android.content.ContentResolver
import android.content.ContentUris
import android.database.DatabaseUtils
import android.os.Build
import android.provider.MediaStore
import space.avirias.polaroid.domain.Image
import space.avirias.polaroid.toList
import timber.log.Timber
import java.util.*

class PhotoDataSource(
    private val contentResolver: ContentResolver
) {

    fun getAllPhotos(): List<Image> {
        val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.MediaColumns.RELATIVE_PATH + " like ? "
        else MediaStore.Images.Media.DATA + " like ? "

        val selectionArgs = arrayOf("Polaroid")
        return contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID
            ),
            null, null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            Timber.d("count is ${cursor.count}")
            Timber.d("data is ${DatabaseUtils.dumpCursorToString(cursor)}")
            cursor.toList {
                Image(
                    getString(0),
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        getLong(2)
                    ),
                    Date(getLong(1) * 1000),
                )
            }
        } ?: emptyList()
    }
}
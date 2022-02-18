package space.avirias.polaroid.domain

import android.net.Uri
import java.util.*

data class Image(
    val name: String,
    val uri: Uri,
    val createdAt: Date
)
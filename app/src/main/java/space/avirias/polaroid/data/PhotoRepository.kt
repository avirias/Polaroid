package space.avirias.polaroid.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PhotoRepository @Inject constructor(
    private val source: PhotoDataSource
){

    suspend fun fetchPhotos() = withContext(Dispatchers.IO) {
        source.getAllPhotos()
    }
}
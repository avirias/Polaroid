package space.avirias.polaroid.ui.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import space.avirias.polaroid.domain.Image
import space.avirias.polaroid.data.PhotoRepository
import space.avirias.polaroid.domain.Resource
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _shouldOpenEdit = MutableSharedFlow<Pair<Boolean, Uri?>>(0)
    private val _allImages = MutableSharedFlow<Resource<List<Image>>>(1)
    val allImages = _allImages.asSharedFlow()


    val shouldOpenEdit = _shouldOpenEdit.asSharedFlow()

    fun shouldOpenEditPage(data: Pair<Boolean, Uri?>) {
        viewModelScope.launch {
            _shouldOpenEdit.emit(data)
        }
    }

    fun getAllPhotos() {
        Timber.d("get all images called")
        val handler = CoroutineExceptionHandler { _, throwable ->
            _allImages.tryEmit(Resource.Failure(throwable))
        }
        viewModelScope.launch(handler) {
            _allImages.emit(Resource.Loading())
            val photos = photoRepository.fetchPhotos()
            _allImages.emit(Resource.Success(photos))
        }
    }
}
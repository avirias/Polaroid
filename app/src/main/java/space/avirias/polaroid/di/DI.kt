package space.avirias.polaroid.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import space.avirias.polaroid.data.PhotoDataSource
import space.avirias.polaroid.data.PhotoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesPhotoSource(
        @ApplicationContext context: Context
    ): PhotoDataSource {
        return PhotoDataSource(context.contentResolver)
    }

}

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @ViewModelScoped
    fun providesPhotoRepository(
        photoDataSource: PhotoDataSource
    ) = PhotoRepository(photoDataSource)
}
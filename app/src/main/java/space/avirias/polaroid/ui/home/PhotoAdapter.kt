package space.avirias.polaroid.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import space.avirias.polaroid.domain.Image
import space.avirias.polaroid.databinding.HolderItemImageBinding
import timber.log.Timber


class PhotoAdapter(
    val itemClick: Image.() -> Unit
) : ListAdapter<Image, PhotoAdapter.PhotoViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            HolderItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(currentList[position]) {
            itemClick(it)
        }
    }

    inner class PhotoViewHolder(
        private val itemBinding: HolderItemImageBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: Image, onCLick: (Image) -> Unit) {
            Timber.d("date is ${item.createdAt}")
            itemBinding.imageView.load(item.uri)
            itemBinding.root.setOnClickListener {
                onCLick(item)
            }
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Image>() {
            override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem.name == newItem.name
            }

        }
    }
}
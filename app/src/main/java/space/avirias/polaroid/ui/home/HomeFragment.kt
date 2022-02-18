package space.avirias.polaroid.ui.home

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import space.avirias.polaroid.domain.Resource
import space.avirias.polaroid.databinding.FragmentHomeBinding
import space.avirias.polaroid.ui.main.MainViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val photoAdapter = PhotoAdapter {
        val action =
            HomeFragmentDirections.actionHomeFragmentToPhotoViewFragment(uri.toString())
        findNavController().navigate(action)
    }

    private val permissionResult = registerForActivityResult(RequestPermission()) { granted ->
        if (granted) {
            val action =
                HomeFragmentDirections.actionHomeFragmentToCameraFragment()
            findNavController().navigate(action)
        } else Toast.makeText(requireContext(), "Please grant camera permission", Toast.LENGTH_LONG)
            .show()
    }

    private val pickImage = registerForActivityResult(GetContent()) {
        it?.let { uri ->
            viewModel.shouldOpenEditPage(true to uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cameraButton.setOnClickListener {
            permissionResult.launch(Manifest.permission.CAMERA)
        }

        binding.picker.setOnClickListener {
            pickImage.launch("image/**")
        }

        viewModel.allImages
            .onEach {
                if (it is Resource.Success) {
                    photoAdapter.submitList(it.data) {
                        binding.photoRecylcerView.smoothScrollToPosition(0)
                    }
                }
            }.launchIn(lifecycleScope)

        viewModel.getAllPhotos()
        binding.photoRecylcerView.apply {
            adapter = photoAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package space.avirias.polaroid.ui.camera

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.filter.Filters
import dagger.hilt.android.AndroidEntryPoint
import space.avirias.polaroid.databinding.FragmentCameraBinding
import space.avirias.polaroid.hasCameraPermission
import space.avirias.polaroid.ui.main.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100
    private var currentFilter = 0

    private val filterList = Filters.values()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (requireContext().hasCameraPermission) prepareCamera()
        binding.shuttorButton.setOnClickListener {
            binding.camera.takePictureSnapshot()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun prepareCamera() {
        binding.camera.setLifecycleOwner(viewLifecycleOwner)
        binding.camera.facing = Facing.FRONT
        binding.camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis())
                result.toFile(File(requireContext().filesDir, "$name.jpg")) {
                    it?.let { file ->
                        val uri = Uri.fromFile(file)
                        viewModel.shouldOpenEditPage(true to uri)
                    }.also {
                        findNavController().navigateUp()
                    }
                }
            }
        }
        )

        val gestureDetector = GestureDetector(requireContext(), gestureListener)
        binding.camera.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }


    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                        if (diffX > 0) {
                            // Left to Right swipe gesture
                            setFilter()
                        } else {
                            // Right to Left swipe gesture
                            setFilter(false)
                        }
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            binding.camera.toggleFacing()
            return true
        }
    }

    private fun setFilter(isRightSwipe: Boolean = true) {
        if (isRightSwipe) {
            currentFilter += 1
            if (currentFilter > filterList.size) currentFilter = 0
        } else {
            currentFilter -= 1
            if (currentFilter < 0) currentFilter = filterList.size - 1
        }
        binding.camera.filter = filterList[currentFilter].newInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
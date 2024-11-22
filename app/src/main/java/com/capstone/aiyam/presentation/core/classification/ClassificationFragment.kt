package com.capstone.aiyam.presentation.core.classification

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.RectF
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.OrientationEventListener
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.capstone.aiyam.R
import com.capstone.aiyam.databinding.FragmentClassificationBinding
import com.capstone.aiyam.domain.model.AuthenticationResponse
import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.presentation.auth.signin.SignInFragmentDirections
import com.capstone.aiyam.presentation.auth.signin.SignInViewModel
import com.capstone.aiyam.utils.ResponseWrapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ClassificationFragment : Fragment() {
    private var _binding: FragmentClassificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClassificationViewModel by viewModels()

    private val multiplePermissionId = 14
    private val permissions = arrayOf(Manifest.permission.CAMERA)

    private lateinit var videoCapture: VideoCapture<Recorder>
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraProvider: ProcessCameraProvider
    private var recording: Recording? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var aspectRatio = AspectRatio.RATIO_16_9
    private var isPhoto = true

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            // Pass the selected image/video URI to a callback function
            onMediaSelected(uri)
        } ?: showToast("Failed to select media")
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (hasPermissions()) {
            startCamera()
        } else {
            requestPermissions(permissions, multiplePermissionId)
        }

        binding.galleryIB.setOnClickListener {
            startGallery()
        }

        binding.flipCameraIB.setOnClickListener {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            bindCameraUseCases()
        }

        binding.aspectRatioTxt.setOnClickListener {
            aspectRatio = if (aspectRatio == AspectRatio.RATIO_16_9) {
                AspectRatio.RATIO_4_3
            } else {
                AspectRatio.RATIO_16_9
            }
            binding.aspectRatioTxt.text = if (aspectRatio == AspectRatio.RATIO_16_9) "16:9" else "4:3"
            bindCameraUseCases()
        }

        binding.changeCameraToVideoIB.setOnClickListener {
            isPhoto = !isPhoto
            binding.changeCameraToVideoIB.setImageResource(
                if (isPhoto) R.drawable.ic_photo else R.drawable.ic_videocam
            )
            binding.captureIB.setImageResource(
                if (isPhoto) R.drawable.camera else R.drawable.ic_start
            )
        }

        binding.captureIB.setOnClickListener {
            if (isPhoto) takePhoto() else captureVideo()
        }
    }

    private fun hasPermissions() = permissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        val preview = Preview.Builder()
            .setTargetAspectRatio(aspectRatio)
            .setTargetRotation(binding.previewView.display.rotation)
            .build().also {
                it.surfaceProvider = binding.previewView.surfaceProvider
            }

        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(aspectRatio)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetRotation(binding.previewView.display.rotation)
            .build()

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()

        videoCapture = VideoCapture.withOutput(recorder)

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        cameraProvider.unbindAll()
        try {
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner, cameraSelector, preview, imageCapture, videoCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun takePhoto() {
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, generateFileName("jpg"))
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Pass the captured image file to a callback function
                    onImageCaptured(outputFileResults.savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun captureVideo() {
        if (recording != null) {
            recording?.stop()
            recording = null
            return
        }

        val outputOptions = MediaStoreOutputOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).apply {
            setContentValues(ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, generateFileName("mp4"))
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            })
        }.build()

        recording = videoCapture.output.prepareRecording(requireContext(), outputOptions)
            .start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
                if (recordEvent is VideoRecordEvent.Finalize) {
                    if (!recordEvent.hasError()) {
                        // Pass the recorded video file to a callback function
                        onVideoCaptured(recordEvent.outputResults.outputUri)
                    }
                }
            }
    }

    private fun generateFileName(extension: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "VID_$timestamp.$extension"
    }

    private fun onMediaSelected(uri: Uri) {
        val file = uriToFile(uri)
        val mediaType = getMediaType(uri)
        classify(file, mediaType)
    }

    private fun onImageCaptured(imageUri: Uri?) {
        imageUri?.let {
            val file = uriToFile(it)
            classify(file, "image/*")
        }
    }

    private fun onVideoCaptured(videoUri: Uri?) {
        videoUri?.let {
            val file = uriToFile(it)
            classify(file, "video/*")
        }
    }

    private fun getMediaType(uri: Uri): String {
        val contentResolver = requireContext().contentResolver
        return contentResolver.getType(uri) ?: "*/*"
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = requireContext().contentResolver
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("temp_", ".jpg", storageDir)

        try {
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
        } catch (e: IOException) {
            showToast(e.message.toString())
            e.printStackTrace()
        }

        return file
    }

    private fun classify(file: File, mediaType: String) { lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.classify(file, mediaType).collect {
                handleOnClassify(it)
            }
        }
    }}

    private fun handleOnClassify(result: ResponseWrapper<Classification>) {
        when(result) {
            is ResponseWrapper.Error -> {
                showToast(result.error)
            }
            is ResponseWrapper.Loading -> {

            }
            is ResponseWrapper.Success -> {
                showToast(result.data.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

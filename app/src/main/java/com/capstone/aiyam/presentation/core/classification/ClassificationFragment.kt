package com.capstone.aiyam.presentation.core.classification

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.capstone.aiyam.R
import com.capstone.aiyam.databinding.FragmentClassificationBinding
import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.TokenResponse
import com.capstone.aiyam.utils.gone
import com.capstone.aiyam.utils.toFormattedTime
import com.capstone.aiyam.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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

        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.photoButton -> {
                    if (isChecked) {
                        isPhoto = true
                        binding.captureIBLogo.setImageResource(R.drawable.baseline_photo_camera_24)
                    }
                }
                R.id.videoButton -> {
                    if (isChecked) {
                        isPhoto = false
                        binding.captureIBLogo.setImageResource(R.drawable.ic_start)
                    }
                }
            }
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
            cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture, videoCapture)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Failed: ${e.message}")
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
                    onImageCaptured(outputFileResults.savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    showToast("Failed: ${exception.message}")
                }
            }
        )
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimer = object : Runnable{
        override fun run() {
            val currentTime = SystemClock.elapsedRealtime() - binding.recodingTimerC.base
            val timeString = currentTime.toFormattedTime()
            binding.recodingTimerC.text = timeString
            handler.postDelayed(this,1000)
        }
    }

    private fun captureVideo() {
        if (recording != null) {
            recording?.stop()
            recording = null

            binding.recodingTimerC.gone()
            binding.recodingTimerC.stop()
            handler.removeCallbacks(updateTimer)
            binding.captureIBLogo.setImageResource(R.drawable.ic_start)
            return
        } else {
            binding.recodingTimerC.visible()
            binding.recodingTimerC.base = SystemClock.elapsedRealtime()
            binding.recodingTimerC.start()
            binding.captureIBLogo.setImageResource(R.drawable.ic_stop)
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

        recording = videoCapture.output.prepareRecording(requireContext(), outputOptions).start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
            if (recordEvent is VideoRecordEvent.Finalize) {
                if (!recordEvent.hasError()) {
                    onVideoCaptured(recordEvent.outputResults.outputUri)
                } else {
                    showToast("Failed: ${recordEvent.error}")
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
        alertDialog(file, mediaType).show()
    }

    private fun onImageCaptured(imageUri: Uri?) {
        imageUri?.let {
            val file = uriToFile(it)
            alertDialog(file, "image/*").show()
        }
    }

    private fun onVideoCaptured(videoUri: Uri?) {
        videoUri?.let {
            val file = uriToFile(it)
            alertDialog(file, "video/*").show()
        }
    }

    private fun getMediaType(uri: Uri): String {
        val contentResolver = requireContext().contentResolver
        return contentResolver.getType(uri) ?: "*/*"
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(uri) ?: "*/*"
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "tmp"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("temp_", ".$fileExtension", storageDir)

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
            viewModel.getToken().collect { token ->
                when (token) {
                    is TokenResponse.Failed -> {
                        showToast("Unauthorized")
                    }
                    is TokenResponse.Loading -> {
                        showToast("File has been uploaded")
                    }
                    is TokenResponse.Success -> {
                        viewModel.classify(token.token, file, mediaType).collect {
                            Log.d("Firebase", token.token)
                            handleOnClassify(it)
                        }
                    }
                }
            }
        }
    }}

    private fun handleOnClassify(result: ResponseWrapper<Classification>) {
        when(result) {
            is ResponseWrapper.Error -> {
                showToast(result.error)
            }
            is ResponseWrapper.Loading -> {
                showToast("File has been uploaded")
            }
            is ResponseWrapper.Success -> {
                val action = ClassificationFragmentDirections.actionClassificationFragmentToDetailFragment(result.data)
                findNavController().navigate(action)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun alertDialog(file: File, mediaType: String): AlertDialog {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.custom_dialog, null)

        dialogView.findViewById<TextView>(R.id.dialog_title).text = "Confirmation"
        dialogView.findViewById<TextView>(R.id.dialog_message).text = "Proceed with classification?"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val positiveButton = dialogView.findViewById<Button>(R.id.dialog_positive_button)
        val negativeButton = dialogView.findViewById<Button>(R.id.dialog_negative_button)

        positiveButton.setOnClickListener {
            classify(file, mediaType)
            dialog.dismiss()
        }

        negativeButton.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

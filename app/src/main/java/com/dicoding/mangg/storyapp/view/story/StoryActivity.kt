package com.dicoding.mangg.storyapp.view.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mangg.storyapp.R
import com.dicoding.mangg.storyapp.data.network.response.BaseResponse
import com.dicoding.mangg.storyapp.data.result.Result
import com.dicoding.mangg.storyapp.databinding.ActivityStoryBinding
import com.dicoding.mangg.storyapp.view.ViewModelFactory
import com.dicoding.mangg.storyapp.view.createCustomTempFile
import com.dicoding.mangg.storyapp.view.main.MainActivity
import com.dicoding.mangg.storyapp.view.reduceFileImage
import com.dicoding.mangg.storyapp.view.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var aStoryViewModel: StoryViewModel
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isLocationEnabled: Boolean = false

    private var getFile: File? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

        private val REQUIRED_LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )

        private const val REQUEST_CODE_LOCATION_PERMISSIONS = 11
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                if (!allPermissionsGranted()) {
                    Toast.makeText(
                        this,
                        getString(R.string.didnt_get_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            REQUEST_CODE_LOCATION_PERMISSIONS -> {
                if (!allLocationPermissionsGranted()) {
                    Toast.makeText(
                        this,
                        getString(R.string.didnt_get_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    getMyLastLocation()
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun allLocationPermissionsGranted() = REQUIRED_LOCATION_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupViewModel()

        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadImage() }

        binding.switchLocation.setOnCheckedChangeListener { buttonView, isChecked ->
            isLocationEnabled = isChecked
            if (isLocationEnabled) {
                requestLocation()
                Toast.makeText(this, "GPS location is enabled", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(this, "GPS location is disabled", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun requestLocation() {
        if (!allLocationPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_LOCATION_PERMISSIONS,
                REQUEST_CODE_LOCATION_PERMISSIONS
            )
        } else {
            getMyLastLocation()
        }
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        aStoryViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
    }

    private fun getMyLastLocation() {
        if (allLocationPermissionsGranted()) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                    } else {
                        Toast.makeText(
                            this@StoryActivity,
                            getString(R.string.location_not_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: SecurityException) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun uploadImage() {
        aStoryViewModel.getUser().observe(this@StoryActivity) { user ->
            val token = "Bearer ${user.token}"
            if (getFile != null) {
                val file = reduceFileImage(getFile as File)
                val description = binding.edAddDescription.text.toString()
                    .toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                if (isLocationEnabled) {
                    aStoryViewModel.addStory(
                        token,
                        imageMultipart,
                        description,
                        latitude,
                        longitude
                    )
                        .observe(this@StoryActivity) { result ->
                            handleStoryResult(result)
                        }
                } else {
                    aStoryViewModel.addStory(token, imageMultipart, description, null, null)
                        .observe(this@StoryActivity) { result ->
                            handleStoryResult(result)
                        }
                }
            }
        }
    }

    private fun handleStoryResult(result: Result<BaseResponse>) {
        when (result) {
            is Result.Success -> {
                Toast.makeText(
                    this@StoryActivity,
                    result.data.message,
                    Toast.LENGTH_SHORT
                ).show()
                showLoad(false)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            is Result.Loading -> showLoad(true)
            is Result.Error -> {
                Toast.makeText(this@StoryActivity, result.error, Toast.LENGTH_SHORT).show()
                showLoad(false)
            }
        }
    }


    private fun startGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@StoryActivity,
                "com.dicoding.mangg.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@StoryActivity)

            getFile = myFile

            binding.previewImage.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewImage.setImageBitmap(result)
        }
    }

    private fun showLoad(isLoad: Boolean) {
        binding.progressBar.visibility = if (isLoad) View.VISIBLE else View.GONE
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            getMyLastLocation()
        } else {
            Toast.makeText(this, getString(R.string.didnt_get_permission), Toast.LENGTH_SHORT)
                .show()
        }
    }
}

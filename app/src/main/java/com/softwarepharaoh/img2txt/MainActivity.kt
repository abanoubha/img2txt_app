package com.softwarepharaoh.img2txt

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.googlecode.leptonica.android.WriteFile
import com.googlecode.tesseract.android.ResultIterator
import com.googlecode.tesseract.android.TessBaseAPI
import com.softwarepharaoh.img2txt.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var bmp: Bitmap
    private lateinit var cameraPermission: Array<String>

    // private var currentPhotoPath: String? = null
    private var photoURI: Uri? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var baseAPI: TessBaseAPI
    private lateinit var takeImage: ActivityResultLauncher<Intent>
    private lateinit var grabImage: ActivityResultLauncher<String>
    private lateinit var camUri: Uri
    private lateinit var cropImage: ActivityResultLauncher<CropImageContractOptions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.resultTextView.setTextIsSelectable(true)
//        binding.resultTextView.text = getString(R.string.main_notice)
//            HtmlCompat.fromHtml(
//            getString(R.string.main_notice),
//            HtmlCompat.FROM_HTML_MODE_LEGACY
//        )

        binding.scroll.isSmoothScrollingEnabled = true

        cropImage = registerForActivityResult(
            CropImageContract()
        ) { result ->
            if (result.isSuccessful) {
                val uriContent = result.uriContent
                // val uriFilePath = result.getUriFilePath(context = applicationContext) // optional usage
                bmp = uriToBitmap(uriContent!!)
                binding.ocrImage.visibility = View.VISIBLE
                updateImageView()
                binding.resultTextView.visibility = View.GONE
                updateImageView()
                recognize()
                deleteAllPhotos()
            } else {
                // An error occurred.
                "Error: ${result.error}".also { binding.resultTextView.text = it }
            }
        }

        binding.copyBtn.setOnClickListener {
            val toBeCopied = binding.resultTextView.text.toString()
            if (toBeCopied.isNotEmpty()) {
                copy2Clipboard(toBeCopied)
            } else {
                showNotification(getString(R.string.no_text))
            }
        }

        binding.fbBtn.setOnClickListener {
            openUrl("https://www.facebook.com/AbanoubHannaDotCom/")
        }

        binding.inBtn.setOnClickListener {
            openUrl("https://www.linkedin.com/in/abanoub-hanna/")
        }

        binding.gitBtn.setOnClickListener {
            openUrl("https://github.com/abanoubha")
        }

        binding.telegramBtn.setOnClickListener {
            openUrl("https://t.me/abanoubchan")
        }

        binding.twitterBtn.setOnClickListener {
            openUrl("https://twitter.com/abanoubha")
        }

        binding.ytBtn.setOnClickListener {
            openUrl("https://youtube.com/@abanoubha")
        }

        binding.gpBtn.setOnClickListener {
            openUrl("https://play.google.com/store/apps/details?id=com.softwarepharaoh.img2txt")
        }

        grabImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            deleteAllPhotos()
            binding.ocrImage.setImageURI(uri)
            cropImage.launch(
                CropImageContractOptions(
                    uri,
                    CropImageOptions(activityBackgroundColor = Color.GRAY)
                )
            )
            // cropFun(uri)
        }

        binding.galleryBtn.setOnClickListener {
            deleteAllPhotos()
            grabImage.launch("image/*")
        }

        takeImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    deleteAllPhotos()
                    binding.ocrImage.setImageURI(camUri)
                    cropImage.launch(
                        CropImageContractOptions(
                            camUri,
                            CropImageOptions(activityBackgroundColor = Color.GRAY)
                        )
                    )
                    // cropFun(camUri)
                }
            }
        )

        binding.cameraBtn.setOnClickListener {
            deleteAllPhotos()
            if (checkCameraPermission()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "New Picture")
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                camUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                )!!
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, camUri)
                takeImage.launch(cameraIntent)
            } else {
                showDialogMsg() //if yes see the permission requests
            }
        }

        loadAds()
        onSharedIntent()

    } // onCreate

    private fun updateImageView() {
        binding.ocrImage.postOnAnimation {
            binding.ocrImage.setImageBitmap(bmp)
            binding.ocrImage.layoutParams.height = bmp.height
            binding.ocrImage.requestLayout()
        }
    }

    private fun openUrl(url: String?) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }

    private fun loadAds() {
        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()

        //if network available, load the ad
        if (isNetworkAvailable(this)) {
            binding.adView.loadAd(adRequest)
        }

        binding.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                binding.adView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                binding.adView.visibility = View.GONE
            }

            override fun onAdOpened() {}

            override fun onAdClicked() {}

            override fun onAdClosed() {}
        }
    }

    override fun onStart() {
        super.onStart()
        loadAds()
    }

    override fun onResume() {
        super.onResume()
        loadAds()
    }

    private fun copy2Clipboard(text: CharSequence?) {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copy text", text)
        clipboard.setPrimaryClip(clip)
        showNotification(getString(R.string.copied))
    }

    private fun showNotification(text: String?) {
        Snackbar.make(findViewById(R.id.copyBtn), text as CharSequence, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.camera) {
            deleteAllPhotos()
            if (checkCameraPermission()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "New Picture")
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                camUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                )!!
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, camUri)
                takeImage.launch(cameraIntent)
            } else {
                showDialogMsg() //if yes see the permission requests
            }
            return true
        } else if (id == R.id.gallery) {
            deleteAllPhotos()
            grabImage.launch("image/*")
            return true
        }
        return super.onOptionsItemSelected(item)
    }

//    un-used code rn
//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
//        val imageFileName = "pharaoh_" + timeStamp + "_"
//        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val image = File.createTempFile(
//            imageFileName,  /* prefix */
//            ".jpg",  /* suffix */
//            storageDir /* directory */
//        )
//
//        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.absolutePath
//        return image
//    }

    private fun showDialogMsg() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage(R.string.permissionHint)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                // dialog, id
                // User agree to accept permissions, show them permissions requests
                requestCameraPermission()
                //re-call the camera button
                findViewById<View>(R.id.camera).performClick()
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                // dialog, id
                // No -> so user can not use camera to take picture of papers
                val notifBuilder = AlertDialog.Builder(this@MainActivity)
                notifBuilder.setMessage(getString(R.string.permissions_notice))
                    .setPositiveButton(
                        getString(R.string.done)
                    ) { _, _ ->
                        // dialog, id
                        //do nothing
                    }.show()
            }.show()
    }

    private fun showDialogNotice(msg: String) {
        AlertDialog
            .Builder(this@MainActivity)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                // dialog, id
                // no nothing
            }.show()
    }

    private fun requestCameraPermission() {
        cameraPermission = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermission(): Boolean {
        val result0 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == (PackageManager.PERMISSION_GRANTED)
        val result1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
        return result0 || result1 // use OR instead of AND
    }

//    old code w old deprecated method n old dep/lib
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            if (requestCode == UCrop.REQUEST_CROP) {
//                // final Uri resultUri = UCrop.getOutput(data);
//                val resultUri = UCrop.getOutput(data!!)
//                bmp = uriToBitmap(resultUri!!)
//                binding.ocrImage.visibility = View.VISIBLE
//                binding.ocrImage.setImageBitmap(bmp)
//
//                //val result = CropImage.getActivityResult(data)
////                if (result != null) {
////                    bmp = uriToBitmap(result.uri)
////                    binding.ocrImage.visibility = View.VISIBLE
////                    binding.ocrImage.setImageBitmap(bmp)
////                } else {
////                    showNotification("Error: Result is NULL")
////                }
//                binding.resultTextView.visibility = View.GONE
//
//                binding.ocrImage.postOnAnimation { binding.ocrImage.setImageBitmap(bmp) }
//
//                //if (isBmpBlack()) invert()
//                //invert()
//                //ocrImage.postOnAnimation { ocrImage.setImageBitmap(bmp) }
//
//                //bmp = imageDenoise(bmp)
//                //imageDenoise()
//                //bmp = grayscaleToBin(bmp)
//                //ocrImage.postOnAnimation { ocrImage.setImageBitmap(bmp) }
//
////                if (bmp.width < 1000 || bmp.height < 1000){
////                    bmp = createScaledBitmap(bmp, bmp.width * 2, bmp.height * 2, false)
////                    ocrImage.postOnAnimation { ocrImage.setImageBitmap(bmp) }
////                }
//
//                recognize()
//                //preProcessing()
//
//                deleteAllPhotos()
//            } else if (resultCode == UCrop.RESULT_ERROR) {
//                showNotification("Error 721 : error occurred in cropping")
//            }
//        }
//    }

//    old dep/lib
//    private fun cropFun(imageUri: Uri) {
//        val destUri = StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()
//        UCrop.of(imageUri, Uri.fromFile(File(cacheDir, destUri))).start(this)
//    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        var inputStream: InputStream? = null
        try {
            inputStream = contentResolver.openInputStream(uri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return BitmapFactory.decodeStream(inputStream)
    }

    override fun onDestroy() {
        deleteAllPhotos()
        super.onDestroy()
    }

    private fun deleteAllPhotos() {
        if (photoURI != null) {
            this.contentResolver.delete(photoURI!!, null, null)
        }
    }

//    private val isNetworkAvailable: Boolean
//        get() {
//            val connectivityManager: ConnectivityManager =
//                getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//            val activeNetworkInfo = connectivityManager.activeNetworkInfo
//            return activeNetworkInfo != null && activeNetworkInfo.isConnected
//        }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    private fun onSharedIntent() {
        val receivedIntent = intent
        val receivedAction = receivedIntent.action
        val receivedType = receivedIntent.type
        if ((receivedAction != null)
            && (receivedAction == Intent.ACTION_SEND)
            && (receivedType != null)
            && receivedType.startsWith("image/")
        ) {
            // val receiveUri: Uri? = receivedIntent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri?
            val receiveUri: Uri? = receivedIntent.parcelable(Intent.EXTRA_STREAM) as Uri?
            if (receiveUri != null) {
                //UCrop.of(receiveUri, photoURI!!).start(this)
                //CropImage.activity(receiveUri).start(this)
                cropImage.launch(
                    CropImageContractOptions(
                        receiveUri,
                        CropImageOptions(activityBackgroundColor = Color.GRAY)
                    )
                )
            }
        }
    }

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    private fun recognize() {
        binding.resultTextView.text = ""

        binding.progressbar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {

            initAPI()

            //baseAPI.setImage(ReadFile.readBitmap(bmp))
            baseAPI.setImage(bmp)

            //show native thresholded image
            bmp = WriteFile.writeBitmap(baseAPI.thresholdedImage)
            withContext(Dispatchers.Main) {
                updateImageView()
            }

            val recognizedText = StringBuilder()

            try {
                //recognizedText =
                baseAPI.utF8Text
                //recognizedText += "<br/>mean conf : " + baseAPI.meanConfidence() + " <br/>----------<br/>"
            } catch (e: Exception) {
                Log.e("error", "err msg: " + e.message.toString(), e)
                recognizedText.append("Error: $e")
                //recognizedText = "Error: $e"
            }

            withContext(Dispatchers.Main) {
                if (baseAPI.meanConfidence() < 60) {
                    showDialogNotice(getString(R.string.conf_notice))
                }
            }

            val iter: ResultIterator = baseAPI.resultIterator
            val level = TessBaseAPI.PageIteratorLevel.RIL_WORD
            iter.begin()
            bmp = bmp.copy(Bitmap.Config.RGB_565, true)
            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.alpha = 0xA0
            paint.color = Color.BLUE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f

            do {
                if (iter.confidence(level) > 90) {
                    recognizedText.append(iter.getUTF8Text(level))
                    recognizedText.append(" ")
                } else if (iter.confidence(level) > 80) {
                    recognizedText.append("<span style='color:purple;'>")
                    recognizedText.append(iter.getUTF8Text(level))
                    recognizedText.append("</span> ")
                } else { // accuracy less than 80
                    recognizedText.append("<span style='color:red;'>")
                    recognizedText.append(iter.getUTF8Text(level))
                    recognizedText.append("</span> ")
                }

//                recognizedText += if (iter.confidence(level) > 90) {
//                    iter.getUTF8Text(level) + " "
//                } else if (iter.confidence(level) > 80) {
//                    "<span style='color:purple;'>" + iter.getUTF8Text(level) + "</span> "
//                } else { // accuracy less than 80
//                    "<span style='color:red;'>" + iter.getUTF8Text(level) + "</span> "
//                }

                canvas.drawRect(iter.getBoundingRect(level), paint)

                withContext(Dispatchers.Main) {
                    updateImageView()
                }

            } while (iter.next(level))

            // from docs : "The returned iterator must be deleted after use."
            iter.delete()

            withContext(Dispatchers.Main) {
                binding.resultTextView.postOnAnimation {
                    binding.resultTextView.text = HtmlCompat.fromHtml(
                        recognizedText.toString(),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    binding.resultTextView.visibility = View.VISIBLE
                }
                binding.scroll.post {
                    binding.scroll.smoothScrollTo(0, binding.resultTextView.top)
                }
            }

            //baseAPI.clear();
            baseAPI.recycle()

            withContext(Dispatchers.Main) {
                binding.progressbar.postOnAnimation {
                    binding.progressbar.visibility = View.GONE
                }
            }

        } // end of IO coroutine
    } // end of recognize func

    private fun initAPI() {
        baseAPI = TessBaseAPI()
        baseAPI.pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO_OSD
        //val dataPath: String = MainApplication().instance!!.getTessDataParentDirectory()
        val dataPath: String = applicationContext.getExternalFilesDir(null)!!.absolutePath
        //val dataPath: String? = this.getExternalFilesDir(null)!!.absolutePath
        //baseAPI.init(dataPath, "ara", TessBaseAPI.OEM_LSTM_ONLY) // mean conf = 71
        //baseAPI.init(dataPath, "ara+eng", TessBaseAPI.OEM_DEFAULT) // mean conf = 74

        //val inited = baseAPI.init(dataPath, "ara+eng", TessBaseAPI.OEM_TESSERACT_LSTM_COMBINED) // best accuracy
        // ^ this causes error

        val inited = baseAPI.init(
            dataPath,
            "ara+eng",
            TessBaseAPI.OEM_LSTM_ONLY
        ) // mean conf = 77, 70, 83
        if (!inited) {
            // Error initializing Tesseract (wrong data path or language)
            baseAPI.recycle()
            return
        }
    }

    companion object {
        // const val IMAGE_GALLERY_REQUEST = 10
        const val CAMERA_REQUEST_CODE = 20
    }
}

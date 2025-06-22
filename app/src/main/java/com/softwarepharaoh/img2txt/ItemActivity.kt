package com.softwarepharaoh.img2txt

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import com.google.android.material.snackbar.Snackbar
import com.softwarepharaoh.img2txt.databinding.ActivityItemBinding
import java.io.FileNotFoundException
import java.io.InputStream

class ItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(applicationContext)

        val itemId = intent.getLongExtra("ITEM_ID", -1) // -1 is a default value if not found
        if (itemId.toInt() == -1){
            Toast.makeText(this, "Invalid item ID", Toast.LENGTH_LONG).show()
            onBackPressedDispatcher.onBackPressed()
        }
        val itemText = intent.getStringExtra("ITEM_TEXT")
        val itemUri = intent.getStringExtra("ITEM_URI")

        val uri: Uri? = itemUri?.toUri()

        var inputStream: InputStream? = null
        try {
            inputStream = contentResolver.openInputStream(uri!!)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val bmp = BitmapFactory.decodeStream(inputStream)

        binding.img.setImageBitmap(bmp)
        binding.txt.setText(
            HtmlCompat.fromHtml(
                itemText.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        )

        binding.copyBtn.setOnClickListener {
            val toBeCopied = binding.txt.text.toString()
            if (toBeCopied.isNotEmpty()) {
                copy2Clipboard(toBeCopied)
            } else {
                showNotification(getString(R.string.no_text))
            }
        }

        binding.colorCodeSummary.setOnClickListener {
            if (binding.colorCodeDetails.isGone) {
                binding.colorCodeDetails.visibility = View.VISIBLE
            } else {
                binding.colorCodeDetails.visibility = View.GONE
            }
        }

        binding.saveBtn.setOnClickListener {
            val updatedText = binding.txt.text.toString()
            val ret = dbHelper.updateText(itemId, updatedText)
            if (ret != 1){
                Toast.makeText(this, "Error: can not save the item. ret = $ret", Toast.LENGTH_LONG).show()
            }
        }

        binding.deleteBtn.setOnClickListener {
            this.contentResolver.delete(uri!!, null, null)
            val ret = dbHelper.delete(itemId)
            if (ret != 1){
                Toast.makeText(this, "Error: can not save the item. ret = $ret", Toast.LENGTH_LONG).show()
            }
            onBackPressedDispatcher.onBackPressed()
        }

    } // onCreate

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

}
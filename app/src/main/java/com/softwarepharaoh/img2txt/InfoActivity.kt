package com.softwarepharaoh.img2txt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.softwarepharaoh.img2txt.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
    }

    private fun openUrl(url: String?) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }

}

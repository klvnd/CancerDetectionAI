package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

@Suppress("DEPRECATION")
class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Result"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(imageUriString)
        binding.resultImage.setImageURI(imageUri)

        val imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Log.d(TAG, "ShowImage: $imageUri")
                }

                @SuppressLint("SetTextI18n")
                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    results?.let {
                        val topResult = it[0]
                        val label = topResult.categories[0].label
                        val score = topResult.categories[0].score

                        fun Float.formatToString(): String {
                            return String.format("%.2f%%", this * 100)
                        }
//                        binding.resultText.text = "$label ${score.formatToString()}"
                        binding.resultText.text = label
                        binding.resultConfidenceScore.text = score.formatToString()

                        if (label.equals("cancer", ignoreCase = true)) {
                            binding.tvDisclaimer.visibility = View.VISIBLE
                            binding.disclaimer.visibility = View.VISIBLE
                        } else {
                            binding.tvDisclaimer.visibility = View.GONE
                            binding.disclaimer.visibility = View.GONE
                        }
                    }
                }
            }
        )
        imageClassifierHelper.classifyStaticImage(imageUri)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        return true
    }

    companion object {
        const val TAG = "imagePicker"
    }

}
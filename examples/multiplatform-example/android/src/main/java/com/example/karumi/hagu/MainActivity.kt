package com.example.karumi.hagu

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.karumi.hagu.generated.HaguConfig

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)

    findViewById<TextView>(R.id.messageTextView).text = "$HaguConfig.API_KEY"
  }
}

package uz.khan.firebase

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uz.khan.firebase.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.save.setOnClickListener {

            val name = binding.nameUser.text.toString()
            val phone  = binding.phoneNumber.text.toString()

            val i = Intent(this, MainActivity::class.java)
              i.putExtra("name",name)
              i.putExtra("phone",phone)

            startActivity(i)


        }

    }
}
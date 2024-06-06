package com.dicoding.mangg.storyapp.view.user.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mangg.storyapp.R
import com.dicoding.mangg.storyapp.data.result.Result
import com.dicoding.mangg.storyapp.databinding.ActivitySignupBinding
import com.dicoding.mangg.storyapp.view.ViewModelFactory
import com.dicoding.mangg.storyapp.view.user.UserViewModel
import com.dicoding.mangg.storyapp.view.user.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titletext =
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameview = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val layoutedit =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailtext =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val layoutemail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passtext =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val layoutpass =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                titletext,
                nameview,
                layoutedit,
                emailtext,
                layoutemail,
                passtext,
                layoutpass,
                signup
            )
            start()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            if (valid()) {
                val name = binding.edRegisterName.text.toString()
                val email = binding.edRegisterEmail.text.toString()
                val password = binding.edRegisterPassword.text.toString()
                userViewModel.userRegister(name, email, password).observe(this) {
                    when (it) {
                        is Result.Success -> {
                            showLoad(false)
                            showSuccessDialog()
                        }

                        is Result.Loading -> showLoad(true)
                        is Result.Error -> {
                            showLoad(false)
                            if (it.error.contains("Email is already taken", ignoreCase = true)) {
                                showEmailAlreadyRegisteredDialog()
                            } else {
                                Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.check_input),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showSuccessDialog() {
        if (!isFinishing && !isDestroyed) {
            AlertDialog.Builder(this).apply {
                setTitle("Yeah!")
                setMessage("Akun sudah jadi nih. Yuk, login dan bikin story kamu.")
                setPositiveButton("Lanjut") { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finishAffinity()
                }
                create()
                show()
            }
        }
    }

    private fun valid() =
        binding.edRegisterName.error == null &&
                binding.edRegisterEmail.error == null &&
                binding.edRegisterPassword.error == null &&
                !binding.edRegisterName.text.isNullOrEmpty() &&
                !binding.edRegisterEmail.text.isNullOrEmpty() &&
                !binding.edRegisterPassword.text.isNullOrEmpty()


    private fun showLoad(isLoad: Boolean) {
        binding.progressBar.visibility = if (isLoad) View.VISIBLE else View.GONE
    }

    private fun showEmailAlreadyRegisteredDialog() {
        if (!isFinishing && !isDestroyed) {
            AlertDialog.Builder(this).apply {
                setTitle("Email Already Registered")
                setMessage("The email you entered is already registered. Please use a different email or log in.")
                setPositiveButton("Login") { _, _ ->
                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    finishAffinity()
                }
                setNegativeButton("Cancel", null)
                create()
                show()
            }
        }
    }
}

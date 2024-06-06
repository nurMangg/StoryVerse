package com.dicoding.mangg.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mangg.storyapp.R
import com.dicoding.mangg.storyapp.databinding.ActivityMainBinding
import com.dicoding.mangg.storyapp.view.ViewModelFactory
import com.dicoding.mangg.storyapp.view.map.MapsActivity
import com.dicoding.mangg.storyapp.view.story.StoryActivity
import com.dicoding.mangg.storyapp.view.story.adapter.LoadingState
import com.dicoding.mangg.storyapp.view.story.adapter.StoryAdapter
import com.dicoding.mangg.storyapp.view.user.UserViewModel
import com.dicoding.mangg.storyapp.view.welcome.WelcomeActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
        onClick()
    }

    private fun onClick() {
        val menu = binding.menu
        binding.addStory.setOnClickListener {
            startActivity(Intent(this, StoryActivity::class.java))
            menu.close(false)
        }
        binding.maps.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
            menu.close(false)
        }
    }

    private fun setupView() {
        storyAdapter = StoryAdapter()

        mainViewModel.getUser().observe(this@MainActivity) { user ->
            if (user.isLogin) {
                setStory()
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        with(binding.rvStory) {
            setHasFixedSize(true)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingState {
                    storyAdapter.retry()
                })
        }
    }

    private fun setStory() {
        mainViewModel.getStory().observe(this@MainActivity) {
            storyAdapter.submitData(lifecycle, it)
            showLoad(false)
        }
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)

        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
    }

    private fun showLoad(isLoad: Boolean) {
        if (isLoad) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }

            R.id.menu_logout -> {
                showLogoutConfirmationDialog()
                true
            }

            else -> true
        }
    }

    private fun showLogoutConfirmationDialog() {
        val delayDuration = 2000L
        val handler = Handler()

        handler.postDelayed({
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Apakah Kamu Yakin Untuk Keluar?")
            builder.setPositiveButton("Yes") { dialog, which ->
                userViewModel.logout()
                Toast.makeText(
                    this,
                    resources.getString(R.string.success_logout),
                    Toast.LENGTH_SHORT
                ).show()
                userViewModel.logout()
                startActivity(Intent(this, WelcomeActivity::class.java))
                finishAffinity()
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }, delayDuration)
    }

}
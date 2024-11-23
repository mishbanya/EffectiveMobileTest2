package com.mishbanya.effectivemobiletest2.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mishbanya.effectivemobiletest2.R
import com.mishbanya.effectivemobiletest2.databinding.ActivityMainBinding
import com.mishbanya.effectivemobiletest2data.courses.model.CourseModel
import com.mishbanya.effectivemobiletest2domain.viewmodels.MainViewModel
import com.mishbanya.effectivemobiletest2domain.main.usecase.FragmentChangeListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.LinkedList
import java.util.Locale

@AndroidEntryPoint
@SuppressLint("UseCompatLoadingForDrawables")
class MainActivity : AppCompatActivity(), FragmentChangeListener {
    private val binding by viewBinding(ActivityMainBinding::bind)
    private lateinit var mainViewModel: MainViewModel

    private val labels = LinkedList<Pair<ImageButton, TextView>>()
    private val icons = LinkedList<Drawable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setIcons()
        setLabels()
        setLocationLanguage("en")
        initMainViewModel()
        //mainViewModel.clearCourses() //for testing
        startUpLoading()

        initListenerSearchButton()
        initListenerFavoritesButton()
        initListenerProfileButton()
    }
    private fun initMainViewModel() {
        Log.d("Hilt", "Creating MainViewModel client instance")
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }
    private fun initListenerSearchButton(){
        labels[0].first.setOnClickListener {
            onMainClicked()
            updateLabels(0)
        }
    }
    private fun initListenerFavoritesButton(){
        labels[1].first.setOnClickListener {
            onFavoritesClicked()
            updateLabels(1)
        }
    }
    private fun initListenerProfileButton(){
        labels[2].first.setOnClickListener {
            onProfileClicked()
            updateLabels(4)
        }
    }

    override fun onMainClicked() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolderId, MainFragment())
            .commit()
    }

    override fun onBackToMainClicked() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolderId, MainFragment())
            .commit()
    }

    override fun onFavoritesClicked() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolderId, FavoritesFragment())
            .commit()
    }

    override fun onProfileClicked() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolderId, ProfileFragment())
            .commit()
    }

    override fun onCourseClicked(data: CourseModel) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolderId, CourseFragment(data))
            .commit()
    }

    private fun startUpLoading() {
        greetingMessageShow()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolderId, MainFragment())
            .commit()
    }
    private fun setIcons(){
        icons.add(getResources().getDrawable(R.drawable.main_active))
        icons.add(getResources().getDrawable(R.drawable.fav_active))
        icons.add(getResources().getDrawable(R.drawable.profile_active))

        icons.add(getResources().getDrawable(R.drawable.profile))
        icons.add(getResources().getDrawable(R.drawable.fav))
        icons.add(getResources().getDrawable(R.drawable.main ))
    }
    private fun setLabels(){
        labels.add(Pair(binding.searchButton, binding.searchButtonLabel))
        labels.add(Pair(binding.favoritesButton, binding.favoritesButtonLabel))
        labels.add(Pair(binding.profileButton, binding.profileButtonLabel))
    }
    private fun updateLabels(id: Int){
        for(i in 0..2){
            if(id==i){
                labels[i].first.setImageDrawable(icons[i])
                labels[i].second.setTextColor(getResources().getColor(R.color.green))
            }else{
                labels[i].first.setImageDrawable(icons[5-i])
                labels[i].second.setTextColor(getResources().getColor(R.color.dark_grey))
            }
        }
    }

    private fun setLocationLanguage(language: String){
        val context: Context = applicationContext
        context.resources.configuration.setLocale(Locale(language))
    }

    private fun greetingMessageShow(){
        showToast(this.getString(R.string.welcome))
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
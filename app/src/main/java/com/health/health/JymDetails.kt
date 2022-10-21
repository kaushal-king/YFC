package com.health.health

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.health.health.adapter.sliderAdapter
import com.health.health.databinding.ActivityJymDetailsBinding
import com.health.health.dataclass.Gym
import com.smarteist.autoimageslider.SliderView


class JymDetails : AppCompatActivity() {
    private lateinit var binding: ActivityJymDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJymDetailsBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(binding.root)


        val bundle = intent.getBundleExtra("jymdetail")
        var model = bundle?.getSerializable("jymdetail") as Gym



        val adapter = sliderAdapter(model.images,this)


        binding.imageSlider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR)

        binding.imageSlider.setSliderAdapter(adapter)

        binding.imageSlider.setScrollTimeInSec(3)

        binding.imageSlider.setAutoCycle(true)

        binding.imageSlider.startAutoCycle()




        binding.tvName.text=model.name

        var amenities : String=""

        model.amenities.forEach {

            amenities=amenities+it+", "
        }

        binding.tvJymActivity.text=amenities

        binding.tvLocation.text=model.location


        Glide.with(this).load(model.logo)
            .placeholder(R.drawable.imageloading).error(R.drawable.imageloading)
            .into(binding.ivSmallLogo)


        binding.tvDescription.text=model.description


        model.amenities.forEach {

            val chip = Chip(this)
            chip.setText(it)
            chip.setChipBackgroundColorResource(R.color.white)
            chip.setCloseIconVisible(false)

                chip.chipStrokeColor= ColorStateList.valueOf(
                    Color.parseColor("#7A8D9E")
                )
            chip.chipStrokeWidth= 2F

            chip.setTextColor(resources.getColor(R.color.darkgrey))
//            chip.setTextAppearance(R.style.ChipTextAppearance)

            binding.chipGroup.addView(chip)
        }


        binding.ivBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }




    }





}
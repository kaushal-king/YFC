package com.health.health

import Api
import ApiClient
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.health.health.databinding.ActivitySplashBinding
import com.health.health.dataclass.gymlist
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class splash : AppCompatActivity() {

     var  PACKAGE_NAME:String = "com.google.android.apps.fitness";
    var builder: AlertDialog.Builder? = null

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)

        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT

        setContentView(binding.root)


        try {
            packageManager.getPackageInfo(PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
             val animator = ObjectAnimator.ofInt( binding. scrollView, "scrollY", 4000)
             animator.duration = 7000
             animator.start()
//             showDialoge()
             loadGymListApi()
            true



        } catch (e: PackageManager.NameNotFoundException) {

             showDialoge()
             val animator = ObjectAnimator.ofInt( binding. scrollView, "scrollY", 4000)
             animator.duration = 8000
             animator.start()
            false
        }



    }

     fun showDialoge() {

         builder = AlertDialog.Builder(this)
         var inflater: LayoutInflater? = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
         val v: View = inflater!!.inflate(R.layout.alertdialoge, null)
         builder!!.setView(v)
         builder!!.setCancelable(true)
         val alert: AlertDialog = builder!!.create()
         var save = v.findViewById<Button>(R.id.subutton2)
         save.setOnClickListener{
             this.finish();
             System.exit(0);
         }
         //alert.dismiss();
         alert.show()
    }

    fun loadGymListApi(){
        val client = ApiClient()
        val api = client.getClient()?.create(Api::class.java)
        val call = api?.gymlist("Basic YWRtaW46VG0mYUxKVGdhOGJJSTFQTk9sVTNIKjImQDZkaEBG")
        call?.enqueue(object : Callback<gymlist> {
            override fun onResponse(
                call: Call<gymlist>,
                response: Response<gymlist>
            ) {
                if(response.isSuccessful){
                    var statuscode=response.code()
                    Log.e("TAG", "Statuscode of codelist " + statuscode)

                    if(statuscode==200){
                        Log.e("TAG", "onResponse: " + response.body().toString())
                        ConstantHelper.gymList=response.body()!!.gyms
                        val intent = Intent(this@splash, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    else    {
                        Toast.makeText(this@splash,
                            "Some Error occured", Toast.LENGTH_SHORT)
                            .show()
                    }


                }
                else{
                    Toast.makeText(this@splash,
                        "Some Error occured", Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(call: Call<gymlist>, t: Throwable) {
                Log.e("TAG", "onFailure: " + t.localizedMessage)
                Toast.makeText(this@splash, "offline Mode", Toast.LENGTH_SHORT)
                    .show()



            }

        })
    }

}
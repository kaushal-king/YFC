package com.health.health

import Api
import ApiClient
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnSuccessListener
import com.health.health.adapter.jymlistadapter
import com.health.health.databinding.ActivityMainBinding
import com.health.health.dataclass.Gym
import com.health.health.dataclass.gymlist
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), OnSuccessListener<DataSet> {


    private lateinit var binding: ActivityMainBinding

    lateinit var fitnessOptions: FitnessOptions
    private val REQUEST_OAUTH_REQUEST_CODE = 1
    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACTIVITY_RECOGNITION

        )
    lateinit var PermissionLauncher: ActivityResultLauncher<Array<String>>

    val theDates: MutableList<String> = mutableListOf()
    val totalAvgSteps: MutableList<Float> = mutableListOf()
    private var adapter: jymlistadapter? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var  locationn : Location? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       PermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (!permissions.containsValue(false)) {
//                    Log.e("TAG", "PermissionLauncher: yes ")
                    checkGooglefitPermission()

                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                    val cancellationTokenSource = CancellationTokenSource()
                    fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                        .addOnSuccessListener { location ->
                            Log.e("Location", "location is found: $location")
                            locationn=location
                            ConstantHelper.location=location

                            loadjymlist(ConstantHelper.gymList)
                        }
                        .addOnFailureListener { exception ->


                            loadjymlist(ConstantHelper.gymList)
                            Toast.makeText(this,"Oops location failed to Fetch: $exception",Toast.LENGTH_SHORT).show()
                            Log.e("Location", "Oops location failed with exception: $exception")

                        }




                }else{

                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                    }
                }
            }




        checkPermissions()


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    Log.e("Location", "location is found: $location")
                    locationn=location
                    ConstantHelper.location=location

                    loadjymlist(ConstantHelper.gymList)
                }
                .addOnFailureListener { exception ->


                    loadjymlist(ConstantHelper.gymList)
                    Toast.makeText(this,"Oops location failed to Fetch: $exception",Toast.LENGTH_SHORT).show()
                    Log.e("Location", "Oops location failed with exception: $exception")

                }

        }







       // loadGymListApi()



    }



    private fun checkPermissions() {


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()

        }
        else{
            checkGooglefitPermission()

        }

    }


    private fun requestPermissions() {

        when {
            hasPermissions(this, *permission) -> {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    checkGooglefitPermission()
                }
            }
            else -> {

                Toast.makeText(this, " Allow the  Permission", Toast.LENGTH_LONG).show()
                locationPermission()
            }
        }

    }
    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    private fun locationPermission() {
        PermissionLauncher.launch(permission)
    }



    fun checkGooglefitPermission(){

         fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .build()

       var account:  GoogleSignInAccount= getGoogleAccount()!!


        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {

            GoogleSignIn.requestPermissions(
                this,
                REQUEST_OAUTH_REQUEST_CODE,
                account,
                fitnessOptions);
        } else {

                 dataReading()
            if(ConstantHelper.location!=null) {
                loadjymlist(ConstantHelper.gymList)
            }

        }

    }


    fun getGoogleAccount():GoogleSignInAccount?{
        return  GoogleSignIn.getAccountForExtension(this,fitnessOptions)
    }

    fun dataReading(){

        getTodayData()
        invokeHistoryApiForWeeklySteps()


    }

    private fun getTodayData() {

        Fitness.getHistoryClient(this, getGoogleAccount()!!)
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener(this);
        Fitness.getHistoryClient(this, getGoogleAccount()!!)
            .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
            .addOnSuccessListener(this);
        Fitness.getHistoryClient(this, getGoogleAccount()!!)
            .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
            .addOnSuccessListener(this);



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {

                dataReading()
                if(ConstantHelper.location!=null) {
                    loadjymlist(ConstantHelper.gymList)
                }
            }else{

            }
        }else if (resultCode == Activity.RESULT_CANCELED) {

        }
    }




    override fun onSuccess(dataset: DataSet?) {
        if(dataset != null){

            getDataFromDataset(dataset)
        }
    }

    private fun getDataFromDataset(dataset: DataSet) {

        var dataPoints:List<DataPoint> = dataset.dataPoints


        for (dataPoint in dataPoints) {
           // Log.e("TAG", "dataManual:  " + dataPoint.originalDataSource.streamName)
            for (field in dataPoint.dataType.fields) {

                var value= dataPoint.getValue(field).toString()
              //  Log.e("TAG", "data: " + value)

                if(field.name.equals(Field.FIELD_STEPS.name)){

                   var step=DecimalFormat("#.##").format(value.toFloat())
                    Log.e("TAG", "step: " + step)

                    var progress=(step.toFloat()*100f)/12000
                    binding.progressBar.progress=progress.toInt()
                    binding.tvStep.text=step.toString()

                }else if(field.name.equals(Field.FIELD_CALORIES.name)){
                   var cal=DecimalFormat("#.##").format(value.toFloat())
                    Log.e("TAG", "CALORIES: " + cal)
                }else if(field.name.equals(Field.FIELD_DISTANCE.name)){
                  var cal=DecimalFormat("#.##").format(value.toFloat())
                    Log.e("TAG", "DISTANCE: " + value.toString())
                }
            }
        }
    }



    private fun invokeHistoryApiForWeeklySteps() {

        // Read the data that's been collected throughout the past week.

        val cal = Calendar.getInstance()
        cal.time = Date()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val endTime = cal.timeInMillis

        cal.add(Calendar.WEEK_OF_MONTH, -1)
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val startTime = cal.timeInMillis

        Log.e("TAG", "Range Start: $startTime")
        Log.e("TAG", "Range End: $endTime")
        val ESTIMATED_STEP_DELTAS: DataSource = DataSource.Builder()
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .setAppPackageName("com.google.android.gms")
            .build()
        val readRequest: DataReadRequest = DataReadRequest.Builder()
            .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build()
        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .readData(readRequest)
            .addOnSuccessListener { response: DataReadResponse ->


                // The aggregate query puts datasets into buckets, so convert to a
                // single list of datasets
                for (bucket in response.buckets) {

                    //convert days in bucket to milliseconds
                    val days = bucket.getStartTime(TimeUnit.MILLISECONDS)
                    //convert milliseconds to date
                    val stepsDate = Date(days)
                    //convert date to day of the week eg: monday, tuesday etc
                    @SuppressLint("SimpleDateFormat")
                    val df: DateFormat = SimpleDateFormat("EEE")
                    val weekday: String = df.format(stepsDate)
//                    Log.e("TAG", "Date :: "+stepsDate.toString())
                    theDates.add(weekday)
                    for (dataSet in bucket.dataSets) {
//                        Log.e("TAG", dumpDataSet(dataSet).toString())
                        totalAvgSteps.add(dumpDataSet(dataSet).toFloat())
                    }
                }
                Log.e("TAG", theDates.toString())
                Log.e("TAG", totalAvgSteps.toString())
                showChart()

            }
            .addOnFailureListener { e: Exception? ->
                Log.w(
                    "TAG",
                    "There was an error reading data from Google Fit",
                    e
                )
            }
    }

    private fun dumpDataSet(dataSet: DataSet): Int {

        var totalSteps = 0

        for (dp in dataSet.dataPoints) {
            Log.e("TAG", "Data point:")
            Log.e("TAG", "\tType: ${dp.dataType.name}")

            for (field in dp.dataType.fields) {
                val fieldName = field.name
                totalSteps += dp.getValue(field).asInt()
                Log.e("TAG", "\tfield: " + fieldName + "value: " + dp.getValue(field))
            }
        }
        return totalSteps
    }



    fun showChart() {


        binding.chart.setViewPortOffsets(95f, 0f, 15f, 50f)

        //        chart.setBackgroundColor(Color.rgb(104, 241, 175));


        binding.chart.getDescription().setEnabled(false)

        binding.chart.setTouchEnabled(true)

        binding.chart.setDragEnabled(false)
        binding.chart.setScaleEnabled(true)

        binding.chart.setPinchZoom(true)

        binding.chart.setDrawGridBackground(false)
        binding.chart.setMaxHighlightDistance(300f)

        val xLabel: ArrayList<String> = ArrayList()
        theDates.forEach {
            xLabel.add(it)
        }


        val x: XAxis = binding.chart.getXAxis()
        x.setLabelCount(6, false)
        x.textColor = Color.BLACK
        x.position = XAxis.XAxisPosition.BOTTOM
        x.setDrawGridLines(false)

        x.axisLineColor = Color.WHITE
        x.setValueFormatter(IAxisValueFormatter { value, axis -> xLabel.get(value.toInt()) })

        val y: YAxis = binding.chart.getAxisLeft()

        y.setLabelCount(6, false)
        y.textColor = Color.BLACK
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        y.setDrawGridLines(true)
        y.axisLineColor = Color.WHITE


        binding.chart.getAxisRight().setEnabled(false)

        binding.chart.getLegend().setEnabled(false   )

        binding.chart.animateXY(2000, 2000)
        setData( "", R.drawable.fade_blue)

        binding.chart.invalidate()

    }


    private fun setData( s: String, c: Int) {
        val values = ArrayList<Entry>()
        for (i in 0 until totalAvgSteps.size) {
            values.add(Entry(i.toFloat(), totalAvgSteps.get(i)))
        }
        val set1: LineDataSet
        if (binding.chart.getData() != null &&
            binding.chart.getData().getDataSetCount() > 0
        ) {
            set1 = binding.chart.getData().getDataSetByIndex(0) as LineDataSet
            val drawable = ContextCompat.getDrawable(this, c)
            set1.fillDrawable = drawable
            set1.values = values
            set1.label = s
            binding. chart.getData().notifyDataChanged()
           binding.chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, s)
            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            set1.cubicIntensity = 0.01f
            set1.setDrawFilled(true)
            set1.label=""
            set1.setDrawCircles(true)
            set1.lineWidth = 4f

            set1.circleRadius = 6f
            set1.circleHoleRadius=0f
            set1.setCircleColorHole(Color.parseColor("#4C8AFF"))
            set1.setCircleColor(Color.parseColor("#4C8AFF"))
            set1.highLightColor = Color.rgb(244, 117, 117)
            set1.color = Color.parseColor("#FBD06C")
           set1.fillColor = Color.WHITE
//            set1.fillAlpha = 100
            set1.setDrawHorizontalHighlightIndicator(false)


            val data = LineData(set1)

            data.setValueTextSize(9f)
            data.setDrawValues(false)


           binding.chart.setData(data)
        }
    }



    private fun loadjymlist(gymlist: List<Gym>) {

        adapter = jymlistadapter(
            gymlist,
            this

        )
        binding.rvGym.adapter = adapter
        binding.rvGym.adapter?.notifyDataSetChanged()


    }



}

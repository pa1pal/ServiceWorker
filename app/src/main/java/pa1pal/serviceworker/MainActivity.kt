package pa1pal.serviceworker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class MainActivity : AppCompatActivity() {

    private val serviceWorker1: ServiceWorker = ServiceWorker("service1")
    private val serviceWorker2: ServiceWorker = ServiceWorker("service2")
    val IMAGE_1 = "https://cdn.pixabay.com/photo/2020/04/05/07/25/sunset-5004905_1280.jpg"
    val IMAGE_2 = "https://cdn.pixabay.com/photo/2016/01/05/17/51/dog-1123016_1280.jpg"
    lateinit var okHttpClient: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        okHttpClient = OkHttpClient()
        button1.setOnClickListener { fetchImage1AndSet() }
        button2.setOnClickListener { fetchImage2AndSet() }
    }

    private fun fetchImage1AndSet() {
        serviceWorker1.addTask(object : Task<Bitmap>() {
            override fun onExecuteTask() {
                Thread(Runnable {
                    val request: Request = Request.Builder().url(IMAGE_1).build()
                    val response: Response = okHttpClient.newCall(request).execute()
                    val bitmap = BitmapFactory.decodeStream(response.body?.byteStream())
                    sendData(bitmap)
                }).start()
            }

            override fun onTaskComplete(result: Bitmap) {
                runOnUiThread {
                    imageView1.setImageBitmap(result)
                    Log.d("data", "image found")
                }
            }
        })
    }

    private fun fetchImage2AndSet() {
        serviceWorker2.addTask(object : Task<Bitmap>() {
            override fun onExecuteTask() {
                Thread(Runnable {
                    val request: Request = Request.Builder().url(IMAGE_2).build()
                    val response: Response = okHttpClient.newCall(request).execute()
                    val bitmap = BitmapFactory.decodeStream(response.body?.byteStream())
                    sendData(bitmap)
                }).start()
            }

            override fun onTaskComplete(result: Bitmap) {
                runOnUiThread {
                    imageView2.setImageBitmap(result)
                }
            }
        })
    }
}

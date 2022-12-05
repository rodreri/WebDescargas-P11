package dgtic.unam.webdescargas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dgtic.unam.webdescargas.databinding.ActivityMainBinding

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

//    private lateinit var binding: ActivityMainBinding

    private lateinit var imageView: ImageView
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1000
            )
        }
        button = findViewById(R.id.button)
        button.setOnClickListener {
//            hiloSecundario("https://poole.ncsu.edu/news/wp-content/uploads/sites/27/2022/06/Heidi-4.png")
            hiloSecundario("https://latam-editorial.pxcrush.net/soloautos/general/editorial/golf8_34-front.jpg?width=1024&height=683&pxc_method=crop")
        }
    }

    private fun openConnection(urlRuta: String): InputStream? {
        var entrada: InputStream? = null
        var response: Int = -1
        var url = URL(urlRuta)
        var cnn = url.openConnection()
        if (cnn !is HttpURLConnection) {
            throw IOException("Recurso no encontrado")
        } else {
            try {
                var httpConn: HttpURLConnection = cnn
                httpConn.allowUserInteraction = false
                httpConn.instanceFollowRedirects = true
                httpConn.requestMethod = "GET"
                httpConn.connect()
                response = httpConn.responseCode
                if (response == HttpURLConnection.HTTP_OK) {
                    entrada = httpConn.inputStream
                }
            } catch (ex: Exception) {
                Toast.makeText(this, "Recurso no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
        return entrada
    }

    private fun downloadImage(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        var entrada: InputStream
        try {
            entrada = openConnection(url)!!
            bitmap = BitmapFactory.decodeStream(entrada)
            entrada.close()
        } catch (ex: Exception) {
            println(ex.printStackTrace())
        }
        return bitmap
    }

    private fun hiloSecundario(url: String) {
        Thread(Runnable {
            var img = downloadImage(url)
            this@MainActivity.runOnUiThread(java.lang.Runnable {
                imageView.setImageBitmap(img)
                saveImage(img)
            })
        }).start()
    }

    private fun saveImage(bitmap: Bitmap?) {
        var fileOutStream: FileOutputStream? = null
        var uri: Uri
        try {
            var file: File = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            var imageDirectory = File.createTempFile(
                "image1",
                ".jpg",
                file
            )
            uri = Uri.fromFile(imageDirectory);
            println(imageDirectory)
            fileOutStream = FileOutputStream(imageDirectory);
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, fileOutStream)
            fileOutStream!!.flush()
            fileOutStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
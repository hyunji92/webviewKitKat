package com.cashslide.testwebviewupload

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        val TYPE_IMAGE = "image/*"
        val INPUT_FILE_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            test_webview.webViewClient = WebClient()
        }
        test_webview.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
            allowFileAccess = true
            loadsImagesAutomatically = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
            }
            defaultTextEncodingName = "utf-8"
        }
        test_webview.loadUrl("https://api-ao-dev.adison.co/help_requests/new?uid=darkstormy&google_ad_id=1234&sdk_ver=0.2&os_ver=24&app_id=h5TKfDvtMnBN9WnszgpjVvrG&key=test")

    }

    inner class WebClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url :String): Boolean {
            view.loadUrl(url)
            return true
        }

        // For 4.1 <= Android <5.0
        fun openFileChooser(uploadFile: ValueCallback<Uri>, acceptType : String, capture : String) {
            var mUploadMessage = uploadFile
            if ( mUploadMessage  !=  null) {
                mUploadMessage.onReceiveValue( null )
            }
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = TYPE_IMAGE

            startActivityForResult (intent, INPUT_FILE_REQUEST_CODE )
        }

        /**
         * 파일 업로드. input tag를 클릭했을 때 호출된다.<br></br>
         * 카메라와 갤러리 리스트를 함께 보여준다.
         * @param uploadMsg
         */
        fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
            val mUploadMessage = uploadMsg

            val directory = File(Environment.getExternalStorageDirectory().toString() + File.separator + "test")

            if (!directory.exists()) {
                directory.mkdir()
            }
            val mTempFile = File(directory, "photo_" + Date().time + ".jpg")


            val cameraIntents = ArrayList<Intent>()
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val packageManager = packageManager
            val listCam = packageManager.queryIntentActivities(captureIntent, 0)

            for (res in listCam) {
                val packageName = res.activityInfo.packageName
                val i = Intent(captureIntent)
                i.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                i.setPackage(packageName)
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile))
                cameraIntents.add(i)

            }

            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = TYPE_IMAGE
            val chooserIntent = Intent.createChooser(i, "File Chooser")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray<Parcelable>())
            this@MainActivity.startActivityForResult(chooserIntent, 200)
        }
    }
}

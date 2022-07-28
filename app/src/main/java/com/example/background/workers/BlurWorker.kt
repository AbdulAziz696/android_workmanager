package com.example.background.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.nfc.Tag
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R

private const val TAG ="BlurWorker"
class BlurWorker (context: Context,Workerparams:WorkerParameters):Worker(context,Workerparams){

    override fun doWork(): Result {
        val appContext= applicationContext

        val reesourceUri=inputData.getString(KEY_IMAGE_URI)


        makeStatusNotification("Blurring Image",appContext)

        return try {
//            val picture=BitmapFactory.decodeResource(
//                appContext.resources,
//                R.drawable.android_cupcake
//            )

            if (TextUtils.isEmpty(reesourceUri)){
                Log.e(TAG,"Invalid input Uri")
                throw IllegalArgumentException("Invalid input Uri")
            }

            val resolver = appContext.contentResolver

            val picture=BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(reesourceUri))
            )
            val output = blurBitmap(picture,appContext)

            val outputUri = writeBitmapToFile(appContext,output)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            makeStatusNotification("Output is $outputUri",appContext)

            Result.success(outputData)
        }catch (throwable : Throwable){
            Log.e(TAG,"Error applying blur")
            Result.failure()
        }
    }
}
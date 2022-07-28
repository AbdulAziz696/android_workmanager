package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "SaveImageToFileWorker"
class SaveImageToFileWorker(context: Context,workerParams:WorkerParameters):Worker(context,workerParams) {
    private val title= "Blur Image"
    private val dateFormater = SimpleDateFormat("yyyy:MM:dd 'at' HH:mm:ss z", Locale.getDefault())
    override fun doWork(): Result {
        makeStatusNotification("Saving Image",applicationContext)
            sleep()
        val resolver=applicationContext.contentResolver

        return try {

            val resourceUri=inputData.getString(KEY_IMAGE_URI)
            val bitmap=BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )
            val imageUrl= MediaStore.Images.Media.insertImage(
                resolver,bitmap,title,dateFormater.format(Date())
            )
            if (!imageUrl.isNullOrEmpty()){
                val output= workDataOf(KEY_IMAGE_URI to imageUrl)
                return Result.success(output)
            }else{
                Log.e(TAG, "Writing to MediaStore failed")
                Result.failure()
            }


        }catch (exeption:Exception){
            exeption.printStackTrace()
            Result.failure()
        }
    }
}
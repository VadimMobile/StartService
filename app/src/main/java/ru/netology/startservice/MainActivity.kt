package ru.netology.startservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this@MainActivity, StartService::class.java)
        startService(intent)
    }
}

class StartService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Log.d("SecondService", "onStartCommand called")
        thread {
            Thread.sleep(5000)
            val intent = Intent(this@StartService, SecondService::class.java)
            startService(intent)
            stopSelf()
        }
        return START_STICKY
    }
}

class SecondService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channel = NotificationChannel("id", "Фоновый сервис", IMPORTANCE_DEFAULT)
        channel.description = "Уведомления от фонового сервиса"
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        Log.d("SecondService", "NotificationChannel created")
        val notification = NotificationCompat.Builder(this, "id")
            .setContentTitle("SecondService работает")
            .setContentText("Активити будет запущена через 40 секунд")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1, notification)
       // Log.d("SecondService", "Foreground started")
        thread {
            Thread.sleep(40000)
            val intent = Intent(this@SecondService, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            stopSelf()
                //Log.d("SecondService", "SecondService завершён")
        }
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
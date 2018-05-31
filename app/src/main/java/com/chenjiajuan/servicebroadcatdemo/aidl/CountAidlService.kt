package com.chenjiajuan.servicebroadcatdemo.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.chenjiajuan.servicebroadcatdemo.ICountManager

/**
 * Created by chenjiajuan on 2018/5/31.
 */
class CountAidlService :Service() {

    var iCountManager=object : ICountManager.Stub(){
        override fun getCount(str: String?): Int {
            return 5
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.e("TAG","onBind")
        return iCountManager
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("TAG","onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
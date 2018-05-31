package com.chenjiajuan.servicebroadcatdemo.other

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.*

/**
 * Created by chenjiajuan on 2018/5/17.
 */
class CountService :Service() {
    private var TAG:String="CountService"
    private var count:Int =0
   private var timer:Timer?=null

    override fun onCreate() {
        Log.e(TAG,"onCreate")
        startRunCount()
        super.onCreate()
    }


    override fun onBind(intent: Intent?): IBinder? {
        Log.e(TAG,"onBind")
        return CountBinder()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG,"onStartCommand")
        startRunCount()
        return START_STICKY
    }

    private fun startRunCount() {
        timer=Timer()
        timer!!.schedule(object :TimerTask(){
            override fun run() {
                count++
                Log.e(TAG,"count : "+count)
                //sendBroadcastRefresh(count)

            }

        },0,1000)
    }


    fun  getCount():Int{
        return count
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG,"onDestroy")
        if (timer!= null){
            timer!!.cancel()
            timer=null
        }
    }

    /**
     * 发送广播，刷新activtiy数据
     */
    fun  sendBroadcastRefresh(count:Int){
        var intent=Intent()
        intent.putExtra("count",count)
        intent.action="com.cjj.kotlindemo.Counter_Receiver"
        sendBroadcast(intent)
    }

     inner  class CountBinder: Binder() {
        open fun  getService ():CountService {
            return this@CountService
        }
    }


}
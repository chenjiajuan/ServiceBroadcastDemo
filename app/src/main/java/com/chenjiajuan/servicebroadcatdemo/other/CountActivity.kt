package com.chenjiajuan.servicebroadcatdemo.other

import android.app.Activity
import android.content.*
import android.os.*
import android.util.Log
import android.widget.TextView
import java.util.*

/**
 * Created by chenjiajuan on 2018/5/17.
 */
class CountActivity : Activity() {
    private var TAG:String="CountActivity"
    private var tvCount:TextView?=null
    private var counterReceiver:CounterReceiver ?=null
    private var serviceIntent:Intent?=null
    private var serviceBinder: CountService.CountBinder?=null
    private var countService:CountService?=null
    private var timer:Timer?=null
    private var timerTask:TimerTask?=null
    private var serviceConnection:ServiceConnection=object:ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e(TAG,"onServiceDisconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e(TAG,"onServiceConnected")
           serviceBinder= service as CountService.CountBinder?
           countService=serviceBinder?.getService()
            definedBindRefresh()
        }

    }

    private var countHandler:Handler=object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            var count=msg!!.obj as String
            tvCount!!.text=count
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count)
        tvCount=findViewById(R.id.tvCount)
        //启动service
        serviceIntent=Intent(this, CountService::class.java)

        definedBindService()
        //定义启动方式
       // definedStartService()

        //定义注册广播接收器，接收更新值
        //definedBroadcastReceiver()


    }

    /**
     * 采用bind方式启动service
     */
    private  fun definedBindService(){
        // onCreate  -> onBind  -> onServiceConnected
        bindService(serviceIntent,serviceConnection,Context.BIND_AUTO_CREATE)
    }

    private fun  unBindService(){
        unbindService(serviceConnection)
    }


    private fun definedBindRefresh(){
       timer=Timer()
       timerTask=object :TimerTask(){
           override fun run() {
               Log.e("TAG","timer run")
               var  count=countService?.getCount().toString()
               Log.e("TAG", "run : count : "+count)
               var massage=Message()
               massage.obj=count
               countHandler.sendMessage(massage)
           }
       }
        timer!!.schedule(timerTask,0,1000)
    }


    /**
     * 采用start的方式启动service
     */
    private fun  definedStartService(){
        // onCreate  -> onStartCommand
        startService(serviceIntent)
    }


    /**
     * 定义注册广播接收器，接收更新值
     */
    private fun  definedBroadcastReceiver(){
        counterReceiver=CounterReceiver()
        var intentFilter=IntentFilter()
        intentFilter.addAction("com.cjj.kotlindemo.Counter_Receiver")
        registerReceiver(counterReceiver,intentFilter)
    }

    /**
     * 广播接收器，刷新数据
     */
    inner class CounterReceiver : BroadcastReceiver() {
        private var counter:Int=0
        override fun onReceive(context: Context?, intent: Intent?) {
            counter=intent!!.getIntExtra("count",0)
            tvCount!!.text=counter.toString()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        //unregisterReceiver(counterReceiver)
        unBindService()
        stopService(serviceIntent)
        timerTask!!.cancel()
        timer!!.cancel()
        timerTask=null
        timer=null

        Log.e("TAG","onDestroy")



    }


}
package com.chenjiajuan.servicebroadcatdemo

import android.app.Activity
import android.content.*
import android.os.*
import android.util.Log
import android.view.View
import android.widget.TextView
import java.util.*

/**
 * Created by chenjiajuan on 2018/5/17.
 */
class CountActivity : Activity() {
    private var TAG:String="CountActivity"
    private var tvCount:TextView?=null
    private var tvBindMode:TextView?=null
    private var tvUnBindMode:TextView?=null
    private var tvStartMode:TextView?=null
    private var tvStopService:TextView?=null
    private var counterReceiver: CounterReceiver?=null
    private var serviceIntent:Intent?=null
    private var serviceBinder: CountService.CountBinder?=null
    private var countService: CountService?=null
    private var timer:Timer?=null
    private var timerTask:TimerTask?=null
    private var unBinde:Boolean=false

    enum class StartType{
        TYPE_BIND,
        TYPE_UNBIND,
        TYPE_START,
        TYPE_STOP
    }


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
        tvBindMode=findViewById(R.id.tvBindMode)
        tvUnBindMode=findViewById(R.id.tvUnBindMode)
        tvStartMode=findViewById(R.id.tvStartMode)
        tvStopService=findViewById(R.id.tvStopService)
         //启动service
        serviceIntent=Intent(this, CountService::class.java)

        //定义启动方式
       // definedStartService()

        //定义注册广播接收器，接收更新值
        //definedBroadcastReceiver()

    }


     fun  bindService(view:View?){
        definedBindService()
    }

     fun unBindService(view: View?){
        unBindService()

    }

     fun  startService(view: View?){
        definedStartService()
    }

     fun  stopService(view: View?){
        stopService(serviceIntent)
         cancelTimer()
    }


    /**
     * 采用bind方式启动service
     */
    private  fun definedBindService(){
        // onCreate  -> onBind  -> onServiceConnected
        serviceIntent!!.putExtra("type", StartType.TYPE_BIND)
        bindService(serviceIntent,serviceConnection,Context.BIND_AUTO_CREATE)
    }

    private fun  unBindService(){
        if (!unBinde){
            //IllegalArgumentException: Service not registered:
            unbindService(serviceConnection)
            cancelTimer()
            unBinde=!unBinde
        }

    }


    private fun definedBindRefresh(){
       timer=Timer()
       timerTask=object :TimerTask(){
           override fun run() {
               var  count=countService?.getCount().toString()
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
        serviceIntent!!.putExtra("type", StartType.TYPE_START)
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
        cancelTimer()

        Log.e("TAG","onDestroy")

    }

    private fun cancelTimer(){
        if (timerTask!=null){
            timerTask!!.cancel()
            timerTask=null

        }
        if (timer!=null){
            timer!!.cancel()
            timer=null
        }

    }


}
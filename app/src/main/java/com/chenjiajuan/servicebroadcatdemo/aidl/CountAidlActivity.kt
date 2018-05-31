package com.chenjiajuan.servicebroadcatdemo.aidl

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.chenjiajuan.servicebroadcatdemo.ICountManager
import com.chenjiajuan.servicebroadcatdemo.R

/**
 * Created by chenjiajuan on 2018/5/31.
 */
class CountAidlActivity: Activity() {
    private lateinit var iCountManager:ICountManager
    private var countNumber:Int=0
    private var serviceIntent:Intent?=null
    private var serviceConnect:ServiceConnection=object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            //将服务器返回的binder转化成aidl
            iCountManager= ICountManager.Stub.asInterface(service)
            countNumber=iCountManager.getCount("count")
            Log.e("TAG","countNumber: "+countNumber)

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aidl_count)
        serviceIntent=Intent()
        //绑定service
        bindService()
    }



    fun  bindService(){
        serviceIntent!!.action="com.chenjiajuan.servicebroadcat.CountAidlService"
        serviceIntent!!.`package`="com.chenjiajuan.servicebroadcatdemo"
        bindService(serviceIntent,serviceConnect,Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnect)
    }
}
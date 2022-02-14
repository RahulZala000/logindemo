package com.example.logindemo

import android.app.Activity
import android.app.AlertDialog

class LoadingDialog(var mActivity:Activity) {
private lateinit var isdialng:AlertDialog
    fun startloading(){
        var inflater=mActivity.layoutInflater
        var dialogView=inflater.inflate(R.layout.load,null)
        var builder=AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(true)
        isdialng=builder.create()
        isdialng.show()
    }
    fun isdismis()
    {
        isdialng.dismiss()
    }
}
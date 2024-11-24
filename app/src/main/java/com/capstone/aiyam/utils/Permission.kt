package com.capstone.aiyam.utils

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun appSettingOpen(context: Context){
    Toast.makeText(
        context,
        "Go to Settings and Enable All Permissions",
        Toast.LENGTH_LONG
    ).show()

    val settingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    settingIntent.data = Uri.parse("package:${context.packageName}")
    context.startActivity(settingIntent)
}

fun warningPermissionDialog(context: Context, listener: DialogInterface.OnClickListener){
    MaterialAlertDialogBuilder(context)
        .setMessage("All Permissions are Required for this app")
        .setCancelable(false)
        .setPositiveButton("Ok", listener)
        .create()
        .show()
}

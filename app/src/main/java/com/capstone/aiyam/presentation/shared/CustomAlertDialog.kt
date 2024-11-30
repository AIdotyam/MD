package com.capstone.aiyam.presentation.shared

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.capstone.aiyam.R

class CustomAlertDialog(
    private val context: Context,
    private val title: String,
    private val message: String,
    private val negativeButtonClick: () -> Unit,
    private val positiveButtonClick: () -> Unit
) {
    fun alert(): AlertDialog {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)

        // Set the dialog's title and message dynamically
        dialogView.findViewById<TextView>(R.id.dialog_title).text = title
        dialogView.findViewById<TextView>(R.id.dialog_message).text = message

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        val positiveButton = dialogView.findViewById<Button>(R.id.dialog_positive_button)
        val negativeButton = dialogView.findViewById<Button>(R.id.dialog_negative_button)

        positiveButton.setOnClickListener {
            positiveButtonClick.invoke()
            dialog.dismiss()
        }

        negativeButton.setOnClickListener {
            negativeButtonClick.invoke()
            dialog.dismiss()
        }

        return dialog
    }
}

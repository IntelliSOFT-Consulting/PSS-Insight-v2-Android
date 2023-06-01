package com.intellisoft.pss.models

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.intellisoft.pss.R
import com.intellisoft.pss.room.Comments
import com.intellisoft.pss.room.PssViewModel

class Utils {

  fun generateHypeLink(image: String): String {
    return " <a href='#'> $image</a><br>"
  }

  fun onAlertDialog(context: Context,submissionId:String, myViewModel: PssViewModel, userId: String, indicatorId: String) {
    val dialog = Dialog(context)
    // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.custom_comment_dialog)
    val width = ViewGroup.LayoutParams.MATCH_PARENT
    val height = ViewGroup.LayoutParams.WRAP_CONTENT
    dialog.window?.setLayout(width, height)

    var editText: EditText? = null

    editText = dialog.findViewById(R.id.dialog_edittext)

    val btnok = dialog.findViewById(R.id.dialog_button_ok) as Button
    btnok.setOnClickListener {
      val value = editText.text.toString()
      val comments = Comments(userId.toString(), indicatorId, submissionId, value)
      myViewModel.addComment(comments)
//      notifyDataSetChanged()

      dialog.dismiss()
    }

    val btncn = dialog.findViewById(R.id.dialog_button_cancel) as Button
    btncn.setOnClickListener { dialog.dismiss() }

    dialog.show()
  }

  fun onAttachmentDialog(context: Context,submissionId:String, myViewModel: PssViewModel, userId: String, indicatorId: String) {

    val dialog = Dialog(context)
    // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.custom_image_dialog)
    val width = ViewGroup.LayoutParams.MATCH_PARENT
    val height = ViewGroup.LayoutParams.WRAP_CONTENT
    dialog.window?.setLayout(width, height)

    val imgSuccess = dialog.findViewById(R.id.img_success) as ImageView
    try {
      val image = myViewModel.getImage(context, userId, indicatorId, submissionId)
      if (image != null) {
        val byteArray = image.image
        if (image.isImage) {
          val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
          imgSuccess.setImageBitmap(bitmap)
          imgSuccess.scaleType = ImageView.ScaleType.FIT_CENTER
        }

      }
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
    }

    val btncn = dialog.findViewById(R.id.dialog_cancel_image) as ImageView
    btncn.setOnClickListener { dialog.dismiss() }

    dialog.show()
  }
}

package com.intellisoft.pss.adapter

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.pss.R
import com.intellisoft.pss.helper_class.*
import com.intellisoft.pss.models.Utils
import com.intellisoft.pss.navigation_drawer.fragments.FragmentDataEntry
import com.intellisoft.pss.room.Comments
import com.intellisoft.pss.room.IndicatorResponse
import com.intellisoft.pss.room.PssViewModel

class DataEntryFormsAdapter(
    private var dbDataEntryFormList: ArrayList<DbIndicators>,
    private val context: Context,
    private val submissionId: String,
    private val fragmentDataEntry: FragmentDataEntry,
    private val status: String,
    private val canProvideAnswers: Boolean
) : RecyclerView.Adapter<DataEntryFormsAdapter.Pager2ViewHolder>() {

  inner class Pager2ViewHolder(itemView: View) :
      RecyclerView.ViewHolder(itemView), View.OnClickListener, TextWatcher {

    val formatterClass = FormatterClass()
    val myViewModel = PssViewModel(context.applicationContext as Application)
    val userId = FormatterClass().getSharedPref("username", context)

    val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
    val tvComment: TextView = itemView.findViewById(R.id.tvComment)
    val tvAttachment: TextView = itemView.findViewById(R.id.tv_image)
    val etValue: EditText = itemView.findViewById(R.id.etValue)
    val radioGroup: RadioGroup = itemView.findViewById(R.id.rg_group)
    val radioYes: RadioButton = itemView.findViewById(R.id.rb_yes)
    val radioNo: RadioButton = itemView.findViewById(R.id.rb_no)
    val tvUserComment: TextView = itemView.findViewById(R.id.tv_user_comment)
    val tvUserAttachment: TextView = itemView.findViewById(R.id.tv_user_attachment)
    val lnComment: LinearLayout = itemView.findViewById(R.id.ln_comment)
    val lnAttachment: LinearLayout = itemView.findViewById(R.id.ln_attachment)

    init {

      tvComment.setOnClickListener(this)
      tvAttachment.setOnClickListener(this)
      tvUserAttachment.setOnClickListener(this)
      etValue.addTextChangedListener(this)
      radioYes.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
          val pos = adapterPosition
          val id = dbDataEntryFormList[pos].id
          val name = dbDataEntryFormList[pos].name
          val indicatorResponse = IndicatorResponse(userId.toString(), submissionId, id, "Yes")
          myViewModel.addResponse(indicatorResponse)
          Handler().postDelayed({ fragmentDataEntry.updateProgress() }, 2000)
        }
      }
    }

    override fun onClick(view: View) {
      val pos = adapterPosition
      val id = dbDataEntryFormList[pos].id
      when (view.id) {
        R.id.tv_user_attachment -> {
          Utils().onAttachmentDialog(context, submissionId, myViewModel, userId.toString(), id)
        }
        R.id.tvComment -> {
          onAlertDialog(myViewModel, userId.toString(), id)
        }
        R.id.tv_image -> {

          formatterClass.saveSharedPref(SubmissionQueue.INITIATED.name, submissionId, context)
          formatterClass.saveSharedPref(SubmissionQueue.RESPONSE.name, id, context)
          formatterClass.saveSharedPref(
              NavigationValues.NAVIGATION.name, NavigationValues.DATA_ENTRY.name, context)
          fragmentDataEntry.uploadImage(userId.toString(), id, submissionId)
        }
      }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
      val pos = adapterPosition
      val id = dbDataEntryFormList[pos].id
      val name = dbDataEntryFormList[pos].name
      val value = p0.toString()

      val indicatorResponse = IndicatorResponse(userId.toString(), submissionId, id, value)
      myViewModel.addResponse(indicatorResponse)
      Handler().postDelayed({ fragmentDataEntry.updateProgress() }, 2000)
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHolder {
    return Pager2ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.data_entry_form, parent, false))
  }

  override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
    val myViewModel = PssViewModel(context.applicationContext as Application)
    val userId = FormatterClass().getSharedPref("username", context)
    val name = dbDataEntryFormList[position].name
    val indicatorId = dbDataEntryFormList[position].id
//    if (canProvideAnswers) {
//      holder.etValue.isEnabled = true
//      holder.radioYes.isEnabled = true
//      holder.radioNo.isEnabled = true
//      holder.radioYes.isChecked = true
//      holder.radioNo.isChecked = true
//
//    } else {
//
//      holder.etValue.isEnabled = false
//      holder.radioYes.isEnabled = false
//      holder.radioNo.isEnabled = false
//      holder.radioYes.isChecked = false
//      holder.radioNo.isChecked = false
//    }

    when (dbDataEntryFormList[position].valueType) {
      "BOOLEAN" -> {
        holder.radioGroup.visibility = VISIBLE
        holder.etValue.visibility = GONE
      }
      "NUMBER" -> {
        holder.radioGroup.visibility = GONE
        holder.etValue.visibility = VISIBLE
      }
      else -> {
        holder.radioGroup.visibility = GONE
        holder.etValue.visibility = VISIBLE
      }
    }

    // Get saved responses
    val value = holder.myViewModel.getMyResponse(context, indicatorId, submissionId)
    val comment = holder.myViewModel.getMyComment(context, indicatorId, submissionId)
    val image = holder.myViewModel.getMyImage(context, indicatorId, submissionId)
    if (value != null) {
      holder.etValue.setText(value)
      if (value == "Yes") {
        holder.radioYes.isChecked = true
      } else {
        holder.radioNo.isChecked = true
      }
    }
    if (comment != null) {
      holder.lnComment.visibility = VISIBLE
      holder.tvUserComment.text = comment
    }
    if (image != null) {
      holder.lnAttachment.visibility = VISIBLE

      val spanned = Html.fromHtml(Utils().generateHypeLink(image))
      holder.tvUserAttachment.text = spanned
      holder.tvUserAttachment.movementMethod = LinkMovementMethod.getInstance()
    }
    holder.tvQuestion.text = name
    if (status == SubmissionsStatus.SUBMITTED.name||status == SubmissionsStatus.PUBLISHED.name) {

      holder.radioNo.isEnabled = false
      holder.radioYes.isEnabled = false
      holder.etValue.isEnabled = false
      holder.tvAttachment.isEnabled = false
      holder.tvComment.isEnabled = false
    }
    holder.radioNo.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        val id = dbDataEntryFormList[position].id
        val indicatorResponse = IndicatorResponse(userId.toString(), submissionId, id, "No")
        myViewModel.addResponse(indicatorResponse)
        Handler().postDelayed({ fragmentDataEntry.updateProgress() }, 2000)
      }
    }
  }

  override fun getItemCount(): Int {
    return dbDataEntryFormList.size
  }

  fun onAlertDialog(myViewModel: PssViewModel, userId: String, indicatorId: String) {
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
      notifyDataSetChanged()
      Toast.makeText(context, "posting comments...", Toast.LENGTH_SHORT).show()

      dialog.dismiss()
    }

    val btncn = dialog.findViewById(R.id.dialog_button_cancel) as Button
    btncn.setOnClickListener { dialog.dismiss() }

    dialog.show()
  }
  //
  //  fun onAttachmentDialog(myViewModel: PssViewModel, userId: String, indicatorId: String) {
  //
  //    val dialog = Dialog(context)
  //    // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
  //    dialog.setCancelable(false)
  //    dialog.setContentView(R.layout.custom_image_dialog)
  //    val width = ViewGroup.LayoutParams.MATCH_PARENT
  //    val height = ViewGroup.LayoutParams.WRAP_CONTENT
  //    dialog.window?.setLayout(width, height)
  //
  //    val imgSuccess = dialog.findViewById(R.id.img_success) as ImageView
  //    val mWebView = dialog.findViewById(R.id.webView) as WebView
  //    try {
  //      val image = myViewModel.getImage(context, userId, indicatorId, submissionId)
  //      if (image != null) {
  //        val byteArray = image.image
  //        if (image.isImage) {
  //          val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
  //          imgSuccess.setImageBitmap(bitmap)
  //        } else {
  //          imgSuccess.visibility = GONE
  //          mWebView.visibility = VISIBLE
  //          mWebView.settings.javaScriptEnabled = true
  //          mWebView.settings.builtInZoomControls = true
  //          mWebView.settings.displayZoomControls = false
  //          val pdfBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
  //          val dataUrl = "data:application/pdf;base64,$pdfBase64"
  //          mWebView.loadUrl(dataUrl)
  //        }
  //      }
  //    } catch (e: Exception) {
  //      e.printStackTrace()
  //      Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
  //    }
  //
  //    val btncn = dialog.findViewById(R.id.dialog_cancel_image) as ImageView
  //    btncn.setOnClickListener { dialog.dismiss() }
  //
  //    dialog.show()
  //  }
}

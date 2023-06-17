package com.intellisoft.pss.adapter

import android.annotation.SuppressLint
import android.app.Application
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.pss.R
import com.intellisoft.pss.helper_class.*
import com.intellisoft.pss.models.DownloadFileTask
import com.intellisoft.pss.models.Utils
import com.intellisoft.pss.navigation_drawer.fragments.FragmentDataEntry
import com.intellisoft.pss.room.Comments
import com.intellisoft.pss.room.IndicatorResponse
import com.intellisoft.pss.room.PssViewModel
import com.intellisoft.pss.viewmodels.StatusViewModel

class DataEntryAdapter(
    private var dbDataEntryFormList: ArrayList<DbDataEntryForm>,
    private val context: Context,
    private val currentSession: String,
    private val status: String,
    private val fragmentDataEntry: FragmentDataEntry,
    private val statusViewModel: StatusViewModel
) : RecyclerView.Adapter<DataEntryAdapter.Pager2ViewHolder>() {
  private val formatterClass = FormatterClass()
  var defaultCode: String = ""
  var defaultName: String = ""
  private var canProvideAnswers = true

  inner class Pager2ViewHolder(itemView: View) :
      RecyclerView.ViewHolder(itemView), View.OnClickListener, TextWatcher {

    val pssCode: TextView = itemView.findViewById(R.id.pssCode)
    val indicatorName: TextView = itemView.findViewById(R.id.indicatorName)
    val categoryName: TextView = itemView.findViewById(R.id.categoryName)
    val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
    private val infoIcon: ImageView = itemView.findViewById(R.id.info_icon)
    val userId = FormatterClass().getSharedPref("username", context)
    val myViewModel = PssViewModel(context.applicationContext as Application)

    /*
     * Initial Survey*/
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
    val lnParent: LinearLayout = itemView.findViewById(R.id.ln_parent)
    val hidden: TextView = itemView.findViewById(R.id.hidden)
    val cbAssessmentQuestion: CheckBox = itemView.findViewById(R.id.cb_assessment_question)

    init {
      val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
      recyclerView.layoutManager = layoutManager
      infoIcon.setOnClickListener(this)
      etValue.isEnabled = false
      radioYes.isEnabled = false
      radioNo.isEnabled = false
      etValue.setText("")
      radioYes.isChecked = false
      radioNo.isChecked = false
      tvAttachment.isEnabled = false
      tvComment.isEnabled = false

      if (adapterPosition != RecyclerView.NO_POSITION &&
          adapterPosition < dbDataEntryFormList.size) {
        val pos = adapterPosition
        val forms = dbDataEntryFormList[pos].forms
        // Rest of the initialization logic
        forms.forEach {
          if (it.code != dbDataEntryFormList[pos].categoryCode) {
            it.canAnswer = true
          }
        }
      }

      cbAssessmentQuestion.setOnCheckedChangeListener { _, checked ->
        val pos = adapterPosition
        val current = hidden.text
        val forms = dbDataEntryFormList[pos].forms
        val code=dbDataEntryFormList[pos].categoryName
        if (checked) {
          fragmentDataEntry.considerParentIgnoreChildren(true,code)
          myViewModel.deleteAllSubmitted(context, current.toString(), currentSession)
          etValue.isEnabled = false
          radioYes.isEnabled = false
          radioNo.isEnabled = false
          radioYes.isChecked = false
          radioNo.isChecked = false
          tvAttachment.isEnabled = false
          tvComment.isEnabled = false
          forms.forEach {
            if (it.code != dbDataEntryFormList[pos].categoryCode) {
              it.canAnswer = true
              recyclerView.adapter?.notifyDataSetChanged()
            }
          }
        } else {
          fragmentDataEntry.considerParentIgnoreChildren(false,code)
          etValue.isEnabled = true
          radioYes.isEnabled = true
          radioNo.isEnabled = true
          tvAttachment.isEnabled = true
          tvComment.isEnabled = true
          forms.forEach {
            if (it.code != dbDataEntryFormList[pos].categoryCode) {
              deleteAllSubmitted(it, myViewModel, recyclerView)
              // disable all items on the child fragments at this positions
              it.canAnswer = false
              recyclerView.adapter?.notifyDataSetChanged()
            }
          }
        }

        recyclerView.adapter?.notifyDataSetChanged()
      }
      tvComment.setOnClickListener(this)
      tvAttachment.setOnClickListener(this)
      tvUserAttachment.setOnClickListener(this)
      etValue.addTextChangedListener(this)
    }
    override fun onClick(view: View) {
      val pos = adapterPosition
      val categoryName = dbDataEntryFormList[pos].categoryName
      val indicatorName = dbDataEntryFormList[pos].indicatorName
      val description = dbDataEntryFormList[pos].description
      val id = hidden.text.toString()
      when (view.id) {
        R.id.info_icon -> {
          showDialog(categoryName.toString(), indicatorName.toString(), description.toString())
        }
        R.id.tv_user_attachment -> {
          Utils().onAttachmentDialog(context, currentSession, myViewModel, userId.toString(), id)
        }
        R.id.tvComment -> {
          onAlertDialog(myViewModel, userId.toString(), id)
        }
        R.id.tv_image -> {

          formatterClass.saveSharedPref(SubmissionQueue.INITIATED.name, currentSession, context)
          formatterClass.saveSharedPref(SubmissionQueue.RESPONSE.name, id, context)
          formatterClass.saveSharedPref(
              NavigationValues.NAVIGATION.name, NavigationValues.DATA_ENTRY.name, context)
          fragmentDataEntry.uploadImage(userId.toString(), id, currentSession)
        }
      }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
      val pos = adapterPosition
      val id = defaultCode
      val name = defaultName
      val value = p0.toString()

      val indicatorResponse = IndicatorResponse(userId.toString(), currentSession, id, value)
      myViewModel.addResponse(indicatorResponse)
      Handler().postDelayed({ fragmentDataEntry.updateProgress() }, 2000)
    }
  }

  private fun deleteAllSubmitted(
      it: DbIndicators,
      myViewModel: PssViewModel,
      recyclerView: RecyclerView
  ) {
    myViewModel.deleteAllSubmitted(context, it.id, currentSession)

    notifyDataSetChanged()
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
      val comments = Comments(userId.toString(), indicatorId, currentSession, value)
      myViewModel.addComment(comments)

      dialog.dismiss()
    }

    val btncn = dialog.findViewById(R.id.dialog_button_cancel) as Button
    btncn.setOnClickListener { dialog.dismiss() }

    dialog.show()
  }
  private fun showDialog(categoryName: String, indicatorName: String, description: String) {
    val dialog = Dialog(context)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.definitions_dialog)
    val width = ViewGroup.LayoutParams.MATCH_PARENT
    val height = ViewGroup.LayoutParams.WRAP_CONTENT
    dialog.window?.setLayout(width, height)
    val titleTextview = dialog.findViewById<TextView>(R.id.title_textview)
    val infoTextview = dialog.findViewById<TextView>(R.id.info_textview)
    val dismissIcon = dialog.findViewById<ImageView>(R.id.cancel_button)
    val lnReferenceSheet = dialog.findViewById<LinearLayout>(R.id.ln_reference_sheet)
    dismissIcon.setOnClickListener { dialog.dismiss() }
    lnReferenceSheet.setOnClickListener {
      val referenceSheetUrl = FormatterClass().getSharedPref("referenceSheetUrl", context)
      val referenceSheet = FormatterClass().getSharedPref("referenceSheet", context)
      val complete = "$referenceSheetUrl$referenceSheet"
      dialog.dismiss()
      showReferenceDialog(complete)
    }

    titleTextview.text = categoryName
    infoTextview.text = indicatorName
    dialog.show()
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun showReferenceDialog(url: String) {
    val progressDialog = ProgressDialog(context)
    progressDialog.setTitle("Please wait..")
    progressDialog.setMessage("Download in progress..")
    progressDialog.setCanceledOnTouchOutside(false)
    progressDialog.show()
    val downloadFileTask = DownloadFileTask(context, progressDialog)
    downloadFileTask.execute(url)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHolder {
    return Pager2ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.data_entry_layout, parent, false))
  }

  override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
    val indicatorCode = dbDataEntryFormList[position].categoryName
    val formsList = dbDataEntryFormList[position].forms
    holder.categoryName.text = indicatorCode
    val userId = FormatterClass().getSharedPref("username", context)
    val viewModel = PssViewModel(context.applicationContext as Application)
    val forms: ArrayList<DbIndicators> = ArrayList()

    formsList.forEach {
      if (it.code == indicatorCode) {
        defaultCode = it.id
        defaultName = it.name
        holder.hidden.text = it.id
        when (it.valueType) {
          "BOOLEAN" -> {
            holder.radioGroup.visibility = View.VISIBLE
            holder.etValue.visibility = View.GONE
          }
          "NUMBER" -> {
            holder.radioGroup.visibility = View.GONE
            holder.etValue.visibility = View.VISIBLE
          }
          else -> {
            holder.radioGroup.visibility = View.GONE
            holder.etValue.visibility = View.VISIBLE
          }
        }
        handleSavedResponse(holder, it, userId.toString(), viewModel)
      } else {
        forms.add(it)
      }
    }
    holder.radioYes.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        val id = holder.hidden.text.toString()
        val indicatorResponse = IndicatorResponse(userId.toString(), currentSession, id, "Yes")
        viewModel.addResponse(indicatorResponse)
        Handler().postDelayed({ fragmentDataEntry.updateProgress() }, 2000)
      }
    }
    holder.radioNo.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        val id = holder.hidden.text.toString()
        val indicatorResponse = IndicatorResponse(userId.toString(), currentSession, id, "No")
        viewModel.addResponse(indicatorResponse)
        Handler().postDelayed({ fragmentDataEntry.updateProgress() }, 2000)
      }
    }
    if (holder.cbAssessmentQuestion.isChecked) {
      forms.forEach {
        // disable all items on the child fragments at this positions
        it.canAnswer = true
      }
    }
    Log.e("Data Entry Adapter", "active forms ${forms.count()}")
    val dataEntryAdapter =
        DataEntryFormsAdapter(forms, context, currentSession, fragmentDataEntry, status)
    holder.recyclerView.adapter = dataEntryAdapter

  }

  private fun handleSavedResponse(
      holder: Pager2ViewHolder,
      it: DbIndicators,
      userId: String,
      myViewModel: PssViewModel
  ) {
    // Get saved responses
    val value = holder.myViewModel.getMyResponse(context, it.id, currentSession)
    val comment = holder.myViewModel.getMyComment(context, it.id, currentSession)
    val image = holder.myViewModel.getMyImage(context, it.id, currentSession)
    if (value != null) {
      holder.etValue.setText(value)
      if (value == "Yes") {
        holder.radioYes.isChecked = true
      } else {
        holder.radioNo.isChecked = true
      }
    }
    if (comment != null) {
      holder.lnComment.visibility = View.VISIBLE
      holder.tvUserComment.text = comment
    }
    if (image != null) {
      holder.lnAttachment.visibility = View.VISIBLE

      val spanned = Html.fromHtml(Utils().generateHypeLink(image))
      holder.tvUserAttachment.text = spanned
      holder.tvUserAttachment.movementMethod = LinkMovementMethod.getInstance()
    }
    holder.tvQuestion.text = it.name
    if (status == SubmissionsStatus.SUBMITTED.name || status == SubmissionsStatus.PUBLISHED.name) {

      holder.radioNo.isEnabled = false
      holder.radioYes.isEnabled = false
      holder.etValue.isEnabled = false
      holder.tvAttachment.isEnabled = false
      holder.tvComment.isEnabled = false
    }
    holder.radioNo.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        val indicatorResponse = IndicatorResponse(userId, currentSession, it.id, "No")
        myViewModel.addResponse(indicatorResponse)
        Handler().postDelayed({ fragmentDataEntry.updateProgress() }, 2000)
      }
    }
  }

  override fun getItemCount(): Int {
    return dbDataEntryFormList.size
  }
}

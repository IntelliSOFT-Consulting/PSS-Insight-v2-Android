package com.intellisoft.pss.adapter

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.pss.R
import com.intellisoft.pss.helper_class.DbIndicators
import com.intellisoft.pss.helper_class.FormatterClass
import com.intellisoft.pss.navigation_drawer.fragments.FragmentDataEntry
import com.intellisoft.pss.room.Comments
import com.intellisoft.pss.room.IndicatorResponse
import com.intellisoft.pss.room.PssViewModel

class DataEntryFormsAdapter(
    private var dbDataEntryFormList: ArrayList<DbIndicators>,
    private val context: Context,
    private val submissionId: String,
    private val fragmentDataEntry: FragmentDataEntry
) : RecyclerView.Adapter<DataEntryFormsAdapter.Pager2ViewHolder>() {

  inner class Pager2ViewHolder(itemView: View) :
      RecyclerView.ViewHolder(itemView), View.OnClickListener, TextWatcher {

    val myViewModel = PssViewModel(context.applicationContext as Application)
    val userId = FormatterClass().getSharedPref("username", context)

    val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
    val tvComment: TextView = itemView.findViewById(R.id.tvComment)
    val tvAttachment: TextView = itemView.findViewById(R.id.tv_image)
    val etValue: EditText = itemView.findViewById(R.id.etValue)
    val radioGroup: RadioGroup = itemView.findViewById(R.id.rg_group)
    val radioYes: RadioButton = itemView.findViewById(R.id.rb_yes)
    val radioNo: RadioButton = itemView.findViewById(R.id.rb_no)

    init {
      tvComment.setOnClickListener(this)
      tvAttachment.setOnClickListener(this)
      etValue.addTextChangedListener(this)
      radioYes.setOnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked) {
          // The RadioButton is now checked
          // Do something here
          val pos = adapterPosition
          val id = dbDataEntryFormList[pos].id
          val name = dbDataEntryFormList[pos].name
          val indicatorResponse = IndicatorResponse(userId.toString(), submissionId, id, "Yes")
          myViewModel.addResponse(indicatorResponse)
          Handler().postDelayed({ fragmentDataEntry.updateProgress() }, 2000)
        }
      }
      radioNo.setOnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked) {
          val pos = adapterPosition
          val id = dbDataEntryFormList[pos].id
          val name = dbDataEntryFormList[pos].name
          val indicatorResponse = IndicatorResponse(userId.toString(), submissionId, id, "No")
          myViewModel.addResponse(indicatorResponse)
          Handler().postDelayed({ fragmentDataEntry.updateProgress() }, 2000)
        }
      }
    }

    override fun onClick(view: View) {
      val pos = adapterPosition
      val id = dbDataEntryFormList[pos].id
      when (view.id) {
        R.id.tvComment -> {
          onAlertDialog(myViewModel, userId.toString(), id)
        }
        R.id.tv_image -> {
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

    val code = dbDataEntryFormList[position].code
    val name = dbDataEntryFormList[position].name
    val indicatorId = dbDataEntryFormList[position].id

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
    if (value != null) {
      holder.etValue.setText(value)
      if (value == "Yes") {
        holder.radioYes.isChecked = true
      } else {
        holder.radioNo.isChecked = true
      }
    }

    holder.tvQuestion.text = name
  }

  override fun getItemCount(): Int {
    return dbDataEntryFormList.size
  }

  fun onAlertDialog(myViewModel: PssViewModel, userId: String, indicatorId: String) {
    val dialog = Dialog(context)
    // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.custom_comment_dialog)

    var editText: EditText? = null

    editText = dialog.findViewById(R.id.dialog_edittext)

    val btnok = dialog.findViewById(R.id.dialog_button_ok) as Button
    btnok.setOnClickListener {
      val value = editText.text.toString()
      val comments = Comments(userId.toString(), indicatorId, submissionId, value)
      myViewModel.addComment(comments)

      dialog.dismiss()
    }

    val btncn = dialog.findViewById(R.id.dialog_button_cancel) as Button
    btncn.setOnClickListener { dialog.dismiss() }

    dialog.show()
  }
}

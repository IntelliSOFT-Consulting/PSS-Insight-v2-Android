package com.intellisoft.pss.adapter

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.pss.DbIndicators
import com.intellisoft.pss.helper_class.FormatterClass
import com.intellisoft.pss.R
import com.intellisoft.pss.room.Comments
import com.intellisoft.pss.room.IndicatorResponse
import com.intellisoft.pss.room.PssViewModel

class DataEntryFormsAdapter (private var dbDataEntryFormList: ArrayList<DbIndicators>,
                             private val context: Context
): RecyclerView.Adapter<DataEntryFormsAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, TextWatcher {

        val myViewModel = PssViewModel(context.applicationContext as Application)
        val userId = FormatterClass().getSharedPref("username", context)

        val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
        val tvComment: TextView = itemView.findViewById(R.id.tvComment)
        val etValue: EditText = itemView.findViewById(R.id.etValue)


        init {
            tvComment.setOnClickListener(this)
            etValue.addTextChangedListener(this)

        }

        override fun onClick(p0: View?) {

            val pos = adapterPosition
            val id = dbDataEntryFormList[pos].id

            onAlertDialog(myViewModel,userId.toString(), id)


        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            val pos = adapterPosition
            val id = dbDataEntryFormList[pos].id
            val name = dbDataEntryFormList[pos].name
            val value = p0.toString()

            val indicatorResponse = IndicatorResponse(userId.toString(),id,value)
            myViewModel.addResponse(indicatorResponse)


        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Pager2ViewHolder {
        return Pager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.data_entry_form,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {

        val code = dbDataEntryFormList[position].code
        val name = dbDataEntryFormList[position].name
        val indicatorId = dbDataEntryFormList[position].id

        //Get saved responses
        val value = holder.myViewModel.getMyResponse(context, indicatorId)
        if (value != null){
            holder.etValue.setText(value)
        }

        holder.tvQuestion.text = name


    }

    override fun getItemCount(): Int {
        return dbDataEntryFormList.size
    }

    fun onAlertDialog(
        myViewModel: PssViewModel,
        userId : String,
        indicatorId: String
        ) {
        val dialog = Dialog(context)
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_comment_dialog)

        var editText: EditText? = null


        editText = dialog.findViewById(R.id.dialog_edittext)

        val btnok = dialog.findViewById(R.id.dialog_button_ok) as Button
        btnok.setOnClickListener {

            val value = editText.text.toString()
            val comments = Comments(
                userId.toString(),indicatorId,value
            )
            myViewModel.addComment(comments)

            dialog.dismiss()
        }

        val btncn = dialog.findViewById(R.id.dialog_button_cancel) as Button
        btncn.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }



}
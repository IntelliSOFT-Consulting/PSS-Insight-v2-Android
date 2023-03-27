package com.intellisoft.pss.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.pss.NavigationValues
import com.intellisoft.pss.R
import com.intellisoft.pss.SubmissionsStatus
import com.intellisoft.pss.helper_class.FormatterClass
import com.intellisoft.pss.navigation_drawer.MainActivity
import com.intellisoft.pss.room.Submissions

class SubmissionAdapter (private var submissionList: ArrayList<Submissions>,
                         private val context: Context
): RecyclerView.Adapter<SubmissionAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val formatterClass = FormatterClass()

        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvSubmitted: TextView = itemView.findViewById(R.id.tvSubmitted)
        val statusIcon: ImageView = itemView.findViewById(R.id.statusIcon)
        val linearView: LinearLayout = itemView.findViewById(R.id.linearView)


        init {
            linearView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            formatterClass.saveSharedPref(
                NavigationValues.NAVIGATION.name,
                NavigationValues.DATA_ENTRY.name,
                context
            )
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Pager2ViewHolder {
        return Pager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.submission_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {

        val date = submissionList[position].date
        val status = submissionList[position].status

        if (status == "DRAFT"){
            holder.statusIcon.setImageResource(R.drawable.ic_draft)
        }else{
            holder.statusIcon.setImageResource(R.drawable.ic_submitted_synced)

        }

        holder.tvDate.text = date

        val statusValue = if (status == SubmissionsStatus.SUBMITTED.name){
            "$status & SYNCED"
        }else{
            status
        }
        holder.tvSubmitted.text = statusValue

    }

    override fun getItemCount(): Int {
        return submissionList.size
    }

}
package com.intellisoft.pss.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.pss.R
import com.intellisoft.pss.helper_class.DbDataEntryForm
import com.intellisoft.pss.navigation_drawer.fragments.FragmentDataEntry
import java.util.ArrayList

class DataEntryAdapter(
    private var dbDataEntryFormList: ArrayList<DbDataEntryForm>,
    private val context: Context,
    private val currentSession: String,
    private val fragmentDataEntry: FragmentDataEntry
) : RecyclerView.Adapter<DataEntryAdapter.Pager2ViewHolder>() {

  inner class Pager2ViewHolder(itemView: View) :
      RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val pssCode: TextView = itemView.findViewById(R.id.pssCode)
    val indicatorName: TextView = itemView.findViewById(R.id.indicatorName)
    val categoryName: TextView = itemView.findViewById(R.id.categoryName)
    val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
    private val infoIcon: ImageView = itemView.findViewById(R.id.info_icon)

    init {
      val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
      recyclerView.layoutManager = layoutManager
      infoIcon.setOnClickListener(this)
    }
    override fun onClick(view: View) {
      val pos = adapterPosition
      val categoryName = dbDataEntryFormList[pos].categoryName
      val indicatorName = dbDataEntryFormList[pos].indicatorName
      val description = dbDataEntryFormList[pos].description
      when (view.id) {
        R.id.info_icon -> {
          showDialog(categoryName.toString(), indicatorName.toString(), description.toString())
        }
      }
    }
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
    dismissIcon.setOnClickListener { dialog.dismiss() }
    val title = "$categoryName: $indicatorName"
    titleTextview.text = title
    infoTextview.text = description
    dialog.show()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHolder {
    return Pager2ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.data_entry_layout, parent, false))
  }

  override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {

    val categoryName = dbDataEntryFormList[position].categoryName
    val indicatorCode = dbDataEntryFormList[position].categoryCode
    val indicatorName = dbDataEntryFormList[position].indicatorName
    val formsList = dbDataEntryFormList[position].forms

    holder.pssCode.text = indicatorCode
    holder.indicatorName.text = indicatorName
    holder.categoryName.text = categoryName

    val dataEntryAdapter =
        DataEntryFormsAdapter(formsList, context, currentSession, fragmentDataEntry)
    holder.recyclerView.adapter = dataEntryAdapter
  }

  override fun getItemCount(): Int {
    return dbDataEntryFormList.size
  }
}

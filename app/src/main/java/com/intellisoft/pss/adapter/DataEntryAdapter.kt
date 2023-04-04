package com.intellisoft.pss.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intellisoft.pss.DbDataEntryForm
import com.intellisoft.pss.R

class DataEntryAdapter (private var dbDataEntryFormList: ArrayList<DbDataEntryForm>,
                        private val context: Context
): RecyclerView.Adapter<DataEntryAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val pssCode: TextView = itemView.findViewById(R.id.pssCode)
        val indicatorName: TextView = itemView.findViewById(R.id.indicatorName)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)

        init {
            val layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL,
                false)
            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)

        }
        override fun onClick(p0: View?) {
//            TODO("Not yet implemented")
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Pager2ViewHolder {
        return Pager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.data_entry_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {

        val indicatorCode = dbDataEntryFormList[position].categoryName
        val indicatorName = dbDataEntryFormList[position].indicatorName
        val indicatorId = dbDataEntryFormList[position].categoryId
        val formsList = dbDataEntryFormList[position].forms

        holder.pssCode.text = indicatorCode
        holder.indicatorName.text = indicatorName

        val dataEntryAdapter = DataEntryFormsAdapter(formsList, context)
        holder.recyclerView.adapter = dataEntryAdapter



    }

    override fun getItemCount(): Int {
        return dbDataEntryFormList.size
    }

}
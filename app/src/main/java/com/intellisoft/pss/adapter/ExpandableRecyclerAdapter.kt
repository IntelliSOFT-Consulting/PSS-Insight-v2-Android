package com.intellisoft.pss.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Outline
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.intellisoft.pss.R
import com.intellisoft.pss.helper_class.FormatterClass
import com.intellisoft.pss.helper_class.SettingItem
import com.intellisoft.pss.helper_class.SettingsQueue
import com.intellisoft.pss.navigation_drawer.MainActivity
import com.intellisoft.pss.room.PssViewModel

class ExpandableRecyclerAdapter(
    val modelList: MutableList<SettingItem>,
    val context: Context,
    val myViewModel: PssViewModel,
) : RecyclerView.Adapter<ExpandableRecyclerAdapter.AdapterVH>() {
  val formatterClass = FormatterClass()
  class AdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    var linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
    var expendableLayout: LinearLayout = itemView.findViewById(R.id.expandable_layout)
    var imgArrow: ImageView = itemView.findViewById(R.id.imgArrow)
    var imgIcon: ImageView = itemView.findViewById(R.id.img_icon)
    var settingLayout: TextInputLayout = itemView.findViewById(R.id.setting_layout)
    var settingAutocomplete: AutoCompleteTextView = itemView.findViewById(R.id.setting_autocomplete)
    var btnAction: MaterialButton = itemView.findViewById(R.id.btn_action)
    var tvHeader: TextView = itemView.findViewById(R.id.tv_header)
    var tvBody: TextView = itemView.findViewById(R.id.tv_body)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVH {
    val view: View =
        LayoutInflater.from(parent.context).inflate(R.layout.item_expandable, parent, false)
    return AdapterVH(view)
  }

  override fun onBindViewHolder(holder: AdapterVH, position: Int) {
    val model = modelList[position]
    val title = model.title
    val innerList = model.innerList
    holder.tvTitle.text = title

    val isExpandable: Boolean = modelList[position].expandable
    holder.expendableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE
    holder.imgArrow.background =
        (if (isExpandable) context.getDrawable(R.drawable.baseline_keyboard_arrow_up_24)
        else context.getDrawable(R.drawable.baseline_keyboard_arrow_down_24))
    if (innerList.showEdittext) {
      holder.settingLayout.visibility = View.VISIBLE
    } else {
      holder.settingLayout.visibility = View.GONE
    }
    holder.btnAction.text = innerList.buttonName
    holder.tvHeader.text = innerList.title
    holder.tvBody.text = innerList.subTitle
    holder.imgIcon.setImageResource(model.icon)

    holder.linearLayout.setOnClickListener {
      val version = modelList[position]
      version.expandable = !model.expandable
      notifyItemChanged(position)
    }
    if (model.selector) {
      val list = ArrayList<String>()
      model.options?.let { list.addAll(it) }
      val adp = ArrayAdapter(context, android.R.layout.simple_list_item_1, list)
      holder.settingAutocomplete.setAdapter(adp)
    }else{
//      holder.settingLayout.boxBackgroundMode= TextInputLayout.BoxBackgroundMode.OUTLINE
    }

    holder.btnAction.setOnClickListener {
      val text = holder.settingAutocomplete.text
      if (TextUtils.isEmpty(text)) {
        holder.settingLayout.error = "Enter value"
        holder.settingAutocomplete.requestFocus()
        return@setOnClickListener
      }

      when (model.count) {
        3 -> {
          confirmDelete()
        }
        2 -> {

          formatterClass.saveSharedPref(SettingsQueue.RESERVED.name, text.toString(), context)
        }
        1 -> {

          formatterClass.saveSharedPref(SettingsQueue.CONFIGURATION.name, text.toString(), context)
        }
        0 -> {

          formatterClass.saveSharedPref(SettingsQueue.SYNC.name, text.toString(), context)
        }
      }
    }
  }

  private fun confirmDelete() {
    AlertDialog.Builder(context, R.style.CustomDialog)
        .setTitle(context.getString(R.string.delete_local_data))
        .setMessage(context.getString(R.string.delete_local_data_message))
        .setView(R.layout.warning_layout)
        .setPositiveButton(
            context.getString(R.string.action_accept),
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
              myViewModel.clearAppData()
              val intent = Intent(context, MainActivity::class.java)
              context.startActivity(intent)
            })
        .setNegativeButton(
            context.getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
              dialog.dismiss()
            })
        .show()
  }

  override fun getItemCount(): Int {
    return modelList.size
  }
}

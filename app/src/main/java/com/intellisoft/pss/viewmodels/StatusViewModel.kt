package com.intellisoft.pss.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intellisoft.pss.helper_class.PositionValue

class StatusViewModel : ViewModel() {

  fun updateStatus(b: Boolean, position: Int) {
    val current = PositionValue(b, position)
    this.showLiveData.value = current
  }

  var showLiveData = MutableLiveData<PositionValue>()
}

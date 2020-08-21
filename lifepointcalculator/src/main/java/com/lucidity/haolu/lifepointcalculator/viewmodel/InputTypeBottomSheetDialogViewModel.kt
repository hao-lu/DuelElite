package com.lucidity.haolu.lifepointcalculator.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidity.haolu.base.SingleLiveEvent
import com.lucidity.haolu.lifepointcalculator.model.CalculatorInputType

class InputTypeBottomSheetDialogViewModel : ViewModel() {

    private val _accumulatedCheckVisibility = MutableLiveData<Int>()
    private val _normalCheckVisibility = MutableLiveData<Int>()

    val accumulatedCheckVisibility: LiveData<Int> = _accumulatedCheckVisibility
    val normalCheckVisibility: LiveData<Int> = _normalCheckVisibility

    val accumulatedClickEvent = SingleLiveEvent<Unit>()
    val normalClickEvent = SingleLiveEvent<Unit>()

    fun initView(inputType: String) {
        when (inputType) {
            CalculatorInputType.ACCUMULATED.name -> {
                _accumulatedCheckVisibility.value = View.VISIBLE
                _normalCheckVisibility.value = View.GONE
            }
            CalculatorInputType.NORMAL.name -> {
                _accumulatedCheckVisibility.value = View.GONE
                _normalCheckVisibility.value = View.VISIBLE
            }
        }
    }

    fun onAccumulatedClick() {
        accumulatedClickEvent.call()
        _accumulatedCheckVisibility.value = View.VISIBLE
        _normalCheckVisibility.value = View.GONE
    }

    fun onNormalClick() {
        normalClickEvent.call()
        _accumulatedCheckVisibility.value = View.GONE
        _normalCheckVisibility.value = View.VISIBLE
    }

}
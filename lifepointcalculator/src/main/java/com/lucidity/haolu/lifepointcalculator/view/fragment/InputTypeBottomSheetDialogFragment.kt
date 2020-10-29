package com.lucidity.haolu.lifepointcalculator.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lucidity.haolu.base.view.fragment.RoundedBottomSheetDialogFragment
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.databinding.FragmentInputTypeBottomSheetDialogBinding
import com.lucidity.haolu.lifepointcalculator.model.CalculatorInputType
import com.lucidity.haolu.lifepointcalculator.util.Constants
import com.lucidity.haolu.lifepointcalculator.viewmodel.CalculatorViewModel
import com.lucidity.haolu.lifepointcalculator.viewmodel.InputTypeBottomSheetDialogViewModel

open class InputTypeBottomSheetDialogFragment : RoundedBottomSheetDialogFragment() {

    companion object {
        const val TAG = "InputTypeBottomSheetDialogFragment"
    }

    private lateinit var parentViewModel: CalculatorViewModel
    private lateinit var viewModel: InputTypeBottomSheetDialogViewModel
    private lateinit var binding: FragmentInputTypeBottomSheetDialogBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        parentViewModel = ViewModelProvider(requireParentFragment()).get(CalculatorViewModel::class.java)
        viewModel = ViewModelProvider(this).get(InputTypeBottomSheetDialogViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_input_type_bottom_sheet_dialog,
            container,
            false
        )
        binding.parentViewModel = parentViewModel
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        observeNormalButtonClick()
        observeAccumulatedButtonClick()
        observeNormalCheckVisibility()
        observeAccumulatedCheckVisibility()
    }

    private fun initView() {
        val inputType = sharedPreferences.getString(Constants.SHARED_PREF_INPUT_TYPE, null) ?: CalculatorInputType.NORMAL.name
        viewModel.initView(inputType)
    }

    private fun observeAccumulatedButtonClick() {
        binding.viewModel?.accumulatedClickEvent?.observe(viewLifecycleOwner, Observer {
            parentViewModel.onAccumulatedClick()
            with(sharedPreferences.edit()) {
                putString(Constants.SHARED_PREF_INPUT_TYPE, CalculatorInputType.ACCUMULATED.name)
                commit()
            }
            dismiss()
        })
    }

    private fun observeNormalButtonClick() {
        binding.viewModel?.normalClickEvent?.observe(viewLifecycleOwner, Observer {
            parentViewModel.onNormalClick()
            with(sharedPreferences.edit()) {
                putString(Constants.SHARED_PREF_INPUT_TYPE, CalculatorInputType.NORMAL.name)
                commit()
            }
            dismiss()
        })
    }

    private fun observeNormalCheckVisibility() {
        binding.viewModel?.normalCheckVisibility?.observe(viewLifecycleOwner, Observer { visibility ->
            binding.ivInputNormalCheck.visibility = visibility
        })
    }

    private fun observeAccumulatedCheckVisibility() {
        binding.viewModel?.accumulatedCheckVisibility?.observe(viewLifecycleOwner, Observer { visibility ->
            binding.ivInputAccumulatedCheck.visibility = visibility
        })
    }
}
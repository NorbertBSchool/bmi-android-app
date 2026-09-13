package com.example.lab2.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lab2.R
import com.example.lab2.data.BmiDataStore
import com.example.lab2.data.BmiEntry
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView

class HomeFragment : Fragment() {

    private lateinit var tilWeight: TextInputLayout
    private lateinit var tilHeight: TextInputLayout
    private lateinit var etWeight: TextInputEditText
    private lateinit var etHeight: TextInputEditText
    private lateinit var rbFemale: MaterialRadioButton
    private lateinit var rbMale: MaterialRadioButton
    private lateinit var switchSaveData: MaterialSwitch
    private lateinit var btnCalculate: MaterialButton
    private lateinit var cardResult: MaterialCardView
    private lateinit var tvBmiValue: MaterialTextView
    private lateinit var tvBmiCategory: MaterialTextView
    private lateinit var tvBmiRisk: MaterialTextView
    private lateinit var dataStore: BmiDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataStore = BmiDataStore(requireContext())
        initViews(view)
        setupListeners()
    }

    private fun initViews(view: View) {
        tilWeight = view.findViewById(R.id.tilWeight)
        tilHeight = view.findViewById(R.id.tilHeight)
        etWeight = view.findViewById(R.id.etWeight)
        etHeight = view.findViewById(R.id.etHeight)
        rbFemale = view.findViewById(R.id.rbFemale)
        rbMale = view.findViewById(R.id.rbMale)
        switchSaveData = view.findViewById(R.id.switchSaveData)
        btnCalculate = view.findViewById(R.id.btnCalculate)
        cardResult = view.findViewById(R.id.cardResult)
        tvBmiValue = view.findViewById(R.id.tvBmiValue)
        tvBmiCategory = view.findViewById(R.id.tvBmiCategory)
        tvBmiRisk = view.findViewById(R.id.tvBmiRisk)
    }

    private fun setupListeners() {
        btnCalculate.setOnClickListener { calculateBMI() }

        etWeight.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilWeight.error = null
        }
        etHeight.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilHeight.error = null
        }
    }

    @SuppressLint("DefaultLocale")
    private fun calculateBMI() {
        val weightStr = etWeight.text?.toString()?.trim() ?: ""
        val heightStr = etHeight.text?.toString()?.trim() ?: ""

        var valid = true

        if (weightStr.isEmpty()) {
            tilWeight.error = getString(R.string.error_empty_field)
            valid = false
        } else {
            val w = weightStr.toDoubleOrNull()
            if (w == null || w <= 0) {
                tilWeight.error = getString(R.string.error_invalid_number)
                valid = false
            } else {
                tilWeight.error = null
            }
        }

        if (heightStr.isEmpty()) {
            tilHeight.error = getString(R.string.error_empty_field)
            valid = false
        } else {
            val h = heightStr.toDoubleOrNull()
            if (h == null || h <= 0) {
                tilHeight.error = getString(R.string.error_invalid_number)
                valid = false
            } else {
                tilHeight.error = null
            }
        }

        if (!valid) return

        val weight = weightStr.toDouble()
        val heightM = heightStr.toDouble() / 100.0
        val bmi = weight / (heightM * heightM)
        val (category, risk) = classifyBMI(bmi)

        tvBmiValue.text = String.format("%.1f", bmi)
        tvBmiCategory.text = category
        tvBmiRisk.text = risk
        cardResult.visibility = View.VISIBLE

        if (switchSaveData.isChecked) {
            val gender = if (rbFemale.isChecked) "Female" else "Male"
            val entry = BmiEntry(
                weight = weight,
                height = heightStr.toDouble(),
                bmi = bmi,
                gender = gender,
                category = category,
                riskLevel = risk
            )
            dataStore.saveEntry(entry)
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_data_saved),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun classifyBMI(bmi: Double): Pair<String, String> {
        return when {
            bmi < 16.0 -> "Severe Thinness" to "Very high health risk (extreme)"
            bmi < 17.0 -> "Moderate Thinness" to "High health risk"
            bmi < 18.5 -> "Mild Thinness" to "Increased health risk"
            bmi < 25.0 -> "Normal weight" to "Minimal health risk"
            bmi < 30.0 -> "Overweight" to "Increased health risk"
            bmi < 35.0 -> "Obese Class I" to "High health risk"
            bmi < 40.0 -> "Obese Class II" to "Very high health risk"
            else -> "Obese Class III" to "Extremely high health risk"
        }
    }
}

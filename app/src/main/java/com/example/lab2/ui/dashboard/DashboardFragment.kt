package com.example.lab2.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lab2.R
import com.example.lab2.data.BmiDataStore
import com.example.lab2.data.BmiEntry
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {

    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart
    private lateinit var tvCurrentBmi: TextView
    private lateinit var tvCurrentWeight: TextView
    private lateinit var tvTotalEntries: TextView
    private lateinit var tvAvgBmi: TextView
    private lateinit var btnClearData: MaterialButton
    private lateinit var emptyState: LinearLayout
    private lateinit var chartContainer: LinearLayout
    private lateinit var dataStore: BmiDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataStore = BmiDataStore(requireContext())
        initViews(view)
        setupClearButton()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            loadData()
        }
    }

    private fun initViews(view: View) {
        lineChart = view.findViewById(R.id.lineChart)
        barChart = view.findViewById(R.id.barChart)
        tvCurrentBmi = view.findViewById(R.id.tvCurrentBmi)
        tvCurrentWeight = view.findViewById(R.id.tvCurrentWeight)
        tvTotalEntries = view.findViewById(R.id.tvTotalEntries)
        tvAvgBmi = view.findViewById(R.id.tvAvgBmi)
        btnClearData = view.findViewById(R.id.btnClearData)
        emptyState = view.findViewById(R.id.emptyState)
        chartContainer = view.findViewById(R.id.chartContainer)
    }

    private fun setupClearButton() {
        btnClearData.setOnClickListener {
            dataStore.clearAll()
            loadData()
        }
    }

    private fun loadData() {
        val entries = dataStore.getAllEntries()
        if (entries.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            chartContainer.visibility = View.GONE
            return
        }

        emptyState.visibility = View.GONE
        chartContainer.visibility = View.VISIBLE

        updateSummaryCards(entries)
        setupLineChart(entries)
        setupBarChart(entries)
    }

    private fun updateSummaryCards(entries: List<BmiEntry>) {
        val latest = entries.last()
        tvCurrentBmi.text = String.format("%.1f", latest.bmi)
        tvCurrentWeight.text = String.format("%.1f kg", latest.weight)
        tvTotalEntries.text = entries.size.toString()

        val avgBmi = entries.map { it.bmi }.average()
        tvAvgBmi.text = String.format("%.1f", avgBmi)
    }

    private fun setupLineChart(entries: List<BmiEntry>) {
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.md_theme_dark_primary)
        val primaryContainerColor = ContextCompat.getColor(requireContext(), R.color.md_theme_dark_primaryContainer)

        val bmiEntries = entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.bmi.toFloat())
        }

        val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        val labels = entries.map { dateFormat.format(Date(it.timestamp)) }

        val dataSet = LineDataSet(bmiEntries, "BMI").apply {
            color = primaryColor
            setCircleColor(primaryColor)
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(false)
            setDrawValues(false)
            setDrawFilled(true)
            fillColor = primaryColor
            fillAlpha = 40
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
        }

        lineChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(labels)
                labelRotationAngle = -45f
                textColor = ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurface)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(requireContext(), R.color.md_theme_dark_surfaceVariant)
                textColor = ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurface)
                axisMinimum = 10f
                axisMaximum = 50f
                setDrawAxisLine(false)
            }

            axisRight.isEnabled = false
            setExtraOffsets(8f, 8f, 8f, 8f)
            animateX(1200, Easing.EaseInOutCubic)
            animateY(800, Easing.EaseInOutCubic)
            invalidate()
        }
    }

    private fun setupBarChart(entries: List<BmiEntry>) {
        val tertiaryColor = ContextCompat.getColor(requireContext(), R.color.md_theme_dark_tertiary)

        val weightEntries = entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.weight.toFloat())
        }

        val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        val labels = entries.map { dateFormat.format(Date(it.timestamp)) }

        val dataSet = BarDataSet(weightEntries, "Weight (kg)").apply {
            color = tertiaryColor
            setDrawValues(false)
            barShadowColor = Color.TRANSPARENT
        }

        barChart.apply {
            data = BarData(dataSet).apply {
                barWidth = 0.6f
            }
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(labels)
                labelRotationAngle = -45f
                textColor = ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurface)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(requireContext(), R.color.md_theme_dark_surfaceVariant)
                textColor = ContextCompat.getColor(requireContext(), R.color.md_theme_dark_onSurface)
                setDrawAxisLine(false)
            }

            axisRight.isEnabled = false
            setExtraOffsets(8f, 8f, 8f, 8f)
            animateY(1000, Easing.EaseInOutCubic)
            invalidate()
        }
    }
}

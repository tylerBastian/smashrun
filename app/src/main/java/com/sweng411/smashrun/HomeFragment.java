package com.sweng411.smashrun;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.card.MaterialCardView;
import com.sweng411.smashrun.State.DistancePerMonthBarChartState;
import com.sweng411.smashrun.State.RunTimePieChartState;
import com.sweng411.smashrun.State.YearSummaryUiState;
import com.sweng411.smashrun.ViewModel.HomeViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MaterialCardView yearSummaryCard;
    private TextView yearSummaryText;
    private TextView yearSummaryDistance;
    private TextView yearSummaryRunCount;
    private TextView yearAveragePace;
    private TextView yearAverageRunLength;
    private PieChart runTimePieChart;
    private BarChart distancePerMonthBarChart;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.GetPieChartState().observe(this, state -> {
            UpdatePieChart(state);
        });
        viewModel.GetDistancePerMonthState().observe(this, state ->{
            UpdateBarChart(state);
        });
        viewModel.GetYearSummary().observe(this, state -> {
            UpdateYearSummary(state);
        });

    }

    void UpdateYearSummary(YearSummaryUiState state) {
        yearSummaryRunCount.setText(String.format("Total runs: %s", state.TotalRunCount));
        yearSummaryDistance.setText(String.format("Total miles run: %s ", state.TotalDistance));
        yearAveragePace.setText(String.format("Average pace: %s min/mi", state.AveragePace));
        yearAverageRunLength.setText(String.format("Average run length: %s mi", state.AverageRunLength));
    }

    void UpdatePieChart(RunTimePieChartState state) {
        String label = "type";
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        Map<String, Integer> timeOfDayMap = new HashMap<>();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF8D60"));
        colors.add(Color.parseColor("#FF6122"));

        int totalAmRuns = state.AM;
        int totalPmRuns = state.PM;
        timeOfDayMap.put("AM", totalAmRuns);
        timeOfDayMap.put("PM", totalPmRuns);

        for(String type: timeOfDayMap.keySet()){
            pieEntries.add(new PieEntry(timeOfDayMap.get(type), type));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);

        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.parseColor("#EFEFEF"));
        pieDataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter(runTimePieChart));
        runTimePieChart.setData(pieData);
        runTimePieChart.postInvalidate();
    }

    void UpdateBarChart(DistancePerMonthBarChartState state) {
        Map<String, Double> distancePerMonthMap = state.DistancePerMonthMap;
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF8D60"));
        colors.add(Color.parseColor("#FF6122"));
        colors.add(Color.parseColor("#FF8D60"));
        colors.add(Color.parseColor("#FF6122"));
        colors.add(Color.parseColor("#FF8D60"));
        colors.add(Color.parseColor("#FF6122"));
        colors.add(Color.parseColor("#FF8D60"));
        colors.add(Color.parseColor("#FF6122"));
        colors.add(Color.parseColor("#FF8D60"));
        colors.add(Color.parseColor("#FF6122"));
        colors.add(Color.parseColor("#FF8D60"));
        colors.add(Color.parseColor("#FF6122"));

        int i = 0;
        for(String month: distancePerMonthMap.keySet()){
            barEntries.add(new BarEntry(i, distancePerMonthMap.get(month).floatValue()));
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat targetFormat = new SimpleDateFormat("MMM");
            Date date = null;
            try {
                date = originalFormat.parse(month);
                String formattedDate = targetFormat.format(date);
                labels.add(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            i++;
        }
        Log.d("labels", labels.toString());

        BarDataSet barDataSet = new BarDataSet(barEntries, "Distance");
        barDataSet.setColors(colors);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
        barData.setValueTextSize(12f);
        barData.setValueTextColor(Color.parseColor("#EFEFEF"));
        barData.setValueTypeface(Typeface.DEFAULT_BOLD);

        distancePerMonthBarChart.setData(barData);
        XAxis xAxis = distancePerMonthBarChart.getXAxis();
        xAxis.setLabelCount(labels.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setTitle("Overview");

        yearSummaryCard = view.findViewById(R.id.running_report_card);
        yearSummaryText = view.findViewById(R.id.running_report_header);
        yearSummaryDistance = view.findViewById(R.id.running_report_distance);
        yearSummaryRunCount = view.findViewById(R.id.running_report_run_count);
        yearAveragePace = view.findViewById(R.id.running_report_pace);
        yearAverageRunLength = view.findViewById(R.id.running_report_avg_run_length);
        runTimePieChart = view.findViewById(R.id.time_run_pie_chart);
        distancePerMonthBarChart = view.findViewById(R.id.distance_per_month_bar_chart);

        String text = String.format("%d Running Report", MainActivity.getYear());
        yearSummaryText.setText(text);


        InitPieChart();
        InitBarChart();


        yearSummaryRunCount.setText("Total runs:");
        yearSummaryDistance.setText("Total miles run:");
        yearAveragePace.setText("Average pace:");
        yearAverageRunLength.setText("Average run length:");


        return view;
    }





    private void InitPieChart() {


        runTimePieChart.setUsePercentValues(true);
        runTimePieChart.getDescription().setEnabled(false);
        runTimePieChart.setCenterText("Run Time of Day");
        runTimePieChart.setCenterTextSize(12);
        runTimePieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        runTimePieChart.getLegend().setEnabled(false);
        runTimePieChart.setHoleRadius(40f);
        runTimePieChart.setHoleColor(Color.parseColor("#EFEFEF"));
        runTimePieChart.setTransparentCircleRadius(45f);

        runTimePieChart.invalidate();

        runTimePieChart.animateY(1000, Easing.EaseInOutQuad);
    }

    private void InitBarChart() {


        distancePerMonthBarChart.setFitBars(true);
        distancePerMonthBarChart.getDescription().setEnabled(false);
        distancePerMonthBarChart.getLegend().setEnabled(false);
        distancePerMonthBarChart.setDrawGridBackground(false);
        distancePerMonthBarChart.setDrawBorders(false);
        distancePerMonthBarChart.setDrawValueAboveBar(true);
        distancePerMonthBarChart.setDrawBarShadow(false);

        XAxis xAxis = distancePerMonthBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        xAxis.setTextColor(Color.parseColor("#EFEFEF"));
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);
        xAxis.setTextSize(12f);

        YAxis yAxisLeft = distancePerMonthBarChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setDrawLabels(false);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setDrawTopYLabelEntry(false);
        yAxisLeft.setDrawLimitLinesBehindData(false);
        yAxisLeft.setDrawGridLinesBehindData(false);
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setDrawLabels(false);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setDrawTopYLabelEntry(false);
        yAxisLeft.setDrawLimitLinesBehindData(false);
        yAxisLeft.setDrawGridLinesBehindData(false);

        YAxis yAxisRight = distancePerMonthBarChart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawZeroLine(false);
        yAxisRight.setDrawTopYLabelEntry(false);
        yAxisRight.setDrawLimitLinesBehindData(false);
        yAxisRight.setDrawGridLinesBehindData(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawZeroLine(false);
        yAxisRight.setDrawTopYLabelEntry(false);
        yAxisRight.setDrawLimitLinesBehindData(false);
        yAxisRight.setDrawGridLinesBehindData(false);

        distancePerMonthBarChart.invalidate();
        distancePerMonthBarChart.animateY(1000, Easing.EaseInOutQuad);

    }









}
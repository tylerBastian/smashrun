package com.sweng411.smashrun.Activity.Fragment;

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
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.sweng411.smashrun.R;
import com.sweng411.smashrun.Repo.SmashRunRepository;
import com.sweng411.smashrun.State.DistancePerMonthBarChartState;
import com.sweng411.smashrun.State.ProfileUiState;
import com.sweng411.smashrun.State.RunTimePieChartState;
import com.sweng411.smashrun.State.ScatterPlotEntry;
import com.sweng411.smashrun.State.YearSummaryUiState;
import com.sweng411.smashrun.ViewModel.HomeViewModel;
import com.sweng411.smashrun.ViewModel.ProfileViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#NewInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "Home";
    private HomeViewModel viewModel;
    private ProfileViewModel profileViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView yearSummaryText;
    private TextView yearSummaryDistance;
    private TextView yearSummaryRunCount;
    private TextView yearAveragePace;
    private TextView yearAverageRunLength;
    private PieChart runTimePieChart;
    private BarChart distancePerMonthBarChart;
    private ScatterChart paceVsDistanceScatterChart;


    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment NewInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.GetPieChartState(false).observe(this, state -> {
            Log.d(TAG, "Updating PieChart");
            UpdatePieChart(state);
        });
        viewModel.GetDistancePerMonthState(false).observe(this, state ->{
            Log.d(TAG, "Updating BarChart");
            UpdateBarChart(state);
        });
        viewModel.GetYearSummaryState(false).observe(this, state -> {
            Log.d(TAG, "Updating YearSummary");

            UpdateYearSummary(state);
        });
        viewModel.GetScatterPlotEntries(false).observe(this, state ->
        {
            Log.d(TAG, "Updating Scatter");
            UpdateScatterChart(state);
        });

        //update header_navigation_drawer.xml with profile info
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.GetProfileState(false).observe(this, state -> {
            Log.d(TAG, "Updating Profile");
            TextView name = getActivity().findViewById(R.id.sign_in_text);
            name.setText("Signed in as: " + state.fName + " " + state.lName);
        });

    }

    private void UpdateYearSummary(YearSummaryUiState state) {
        yearSummaryRunCount.setText(String.format("Total runs: %s", state.TotalRunCount));
        yearSummaryDistance.setText(String.format("Total miles run: %s ", state.TotalDistance));
        yearAveragePace.setText(String.format("Average pace: %s min/mi", state.AveragePace));
        yearAverageRunLength.setText(String.format("Average run length: %s mi", state.AverageRunLength));
    }

    private void UpdatePieChart(RunTimePieChartState state) {
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

    private void UpdateBarChart(DistancePerMonthBarChartState state) {
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

    private void UpdateScatterChart(ArrayList<ScatterPlotEntry> entries) {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF8D60"));
        colors.add(Color.parseColor("#FF6122"));


        ArrayList scatterEntries = new ArrayList<>();


        for(int i = 0; i < entries.size(); i++){
            scatterEntries.add(new Entry(entries.get(i).Distance, (entries.get(i).Pace)/60));
        }

        ScatterDataSet paceVsDistanceScatterDataSet = new ScatterDataSet(scatterEntries, "Miles");
        ScatterData paceVsDistanceScatterData = new ScatterData(paceVsDistanceScatterDataSet);
        paceVsDistanceScatterDataSet.setColors(colors);
        paceVsDistanceScatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        paceVsDistanceScatterDataSet.setScatterShapeSize(20f);
        paceVsDistanceScatterDataSet.setDrawValues(false);
        paceVsDistanceScatterDataSet.setScatterShapeHoleRadius(0f);
        paceVsDistanceScatterChart.setData(paceVsDistanceScatterData);


    }


    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setTitle("Overview");


        yearSummaryText = view.findViewById(R.id.running_report_header);
        yearSummaryDistance = view.findViewById(R.id.running_report_distance);
        yearSummaryRunCount = view.findViewById(R.id.running_report_run_count);
        yearAveragePace = view.findViewById(R.id.running_report_pace);
        yearAverageRunLength = view.findViewById(R.id.running_report_avg_run_length);
        runTimePieChart = view.findViewById(R.id.time_run_pie_chart);
        distancePerMonthBarChart = view.findViewById(R.id.distance_per_month_bar_chart);
        paceVsDistanceScatterChart = view.findViewById(R.id.pace_vs_distance_scatter_chart);


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String text = String.format("%d Running Report", year);
        yearSummaryText.setText(text);


        InitPieChart();
        InitBarChart();
        InitScatterChart();

        yearSummaryRunCount.setText("Total runs:");
        yearSummaryDistance.setText("Total miles run:");
        yearAveragePace.setText("Average pace:");
        yearAverageRunLength.setText("Average run length:");


        return view;
    }


    public void Refresh() {
        viewModel.GetPieChartState(true).observe(this, state -> {
            Log.d(TAG, "Updating PieChart");
            UpdatePieChart(state);
        });
        viewModel.GetDistancePerMonthState(true).observe(this, state ->{
            Log.d(TAG, "Updating BarChart");
            UpdateBarChart(state);
        });
        viewModel.GetYearSummaryState(true).observe(this, state -> {
            Log.d(TAG, "Updating YearSummary");

            UpdateYearSummary(state);
        });
        viewModel.GetScatterPlotEntries(true).observe(this, state ->
        {
            Log.d(TAG, "Updating Scatter");
            UpdateScatterChart(state);

        });
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

    private void InitScatterChart() {

        XAxis xAxis = paceVsDistanceScatterChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawGridLinesBehindData(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawGridLinesBehindData(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(10);
        xAxis.setTextColor(Color.parseColor("#EFEFEF"));


        YAxis yAxisLeft = paceVsDistanceScatterChart.getAxisLeft();
        yAxisLeft.setDrawLabels(true);
        yAxisLeft.setLabelCount(6);


        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawAxisLine(true);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setInverted(true);
        yAxisLeft.setDrawTopYLabelEntry(false);
        yAxisLeft.setDrawLimitLinesBehindData(false);
        yAxisLeft.setDrawGridLinesBehindData(false);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setTextColor(Color.parseColor("#EFEFEF"));

        YAxis yAxisRight = paceVsDistanceScatterChart.getAxisRight();
        yAxisRight.setEnabled(false);


        paceVsDistanceScatterChart.setDrawGridBackground(false);
        paceVsDistanceScatterChart.setDrawBorders(false);
        paceVsDistanceScatterChart.setDrawMarkers(false);
        paceVsDistanceScatterChart.setDrawGridBackground(false);
        paceVsDistanceScatterChart.setDrawBorders(false);
        paceVsDistanceScatterChart.setDrawMarkers(false);

        paceVsDistanceScatterChart.setTouchEnabled(false);
        paceVsDistanceScatterChart.setDragEnabled(false);
        paceVsDistanceScatterChart.setScaleEnabled(false);
        paceVsDistanceScatterChart.setPinchZoom(false);
        paceVsDistanceScatterChart.setDoubleTapToZoomEnabled(false);
        paceVsDistanceScatterChart.setHighlightPerDragEnabled(false);
        paceVsDistanceScatterChart.setHighlightPerTapEnabled(false);
        paceVsDistanceScatterChart.setHighlightPerDragEnabled(false);
        paceVsDistanceScatterChart.setHighlightPerTapEnabled(false);
        paceVsDistanceScatterChart.setDescription(null);
        paceVsDistanceScatterChart.getLegend().setEnabled(true);
        //get ride of colorbar
        paceVsDistanceScatterChart.getLegend().setForm(Legend.LegendForm.NONE);
        paceVsDistanceScatterChart.getLegend().setTextColor(Color.parseColor("#EFEFEF"));
        paceVsDistanceScatterChart.getLegend().setTextSize(12f);
        paceVsDistanceScatterChart.getLegend().setFormSize(12f);
        paceVsDistanceScatterChart.getLegend().setFormToTextSpace(1f);
        paceVsDistanceScatterChart.getLegend().setWordWrapEnabled(true);
        paceVsDistanceScatterChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        paceVsDistanceScatterChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        paceVsDistanceScatterChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        paceVsDistanceScatterChart.getLegend().setDrawInside(false);

        paceVsDistanceScatterChart.getLegend().setXEntrySpace(15f);
        paceVsDistanceScatterChart.getLegend().setYEntrySpace(100f);




        paceVsDistanceScatterChart.invalidate();
        paceVsDistanceScatterChart.animateXY(1000, 1000);
    }












}
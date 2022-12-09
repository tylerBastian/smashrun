package com.sweng411.smashrun;

import static com.sweng411.smashrun.MainActivity.getAllActivitiesJsonString;
import static com.sweng411.smashrun.MainActivity.getYearlyStatsJsonString;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

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
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

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
    private ScatterChart paceVsDistanceScatterChart;
    private ScatterDataSet paceVsDistanceScatterDataSet;
    private ScatterData paceVsDistanceScatterData;
    private String totalDistance;
    private String totalRunCount;
    private String averagePace;
    private String averageRunLength;
    private int AmRuns;
    private int PmRuns;
    private String stats;
    private String activities;
    private List<String> last12Months;

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
        paceVsDistanceScatterChart = view.findViewById(R.id.pace_vs_distance_scatter_chart);

        String text = String.format("%d Running Report", MainActivity.getYear());
        yearSummaryText.setText(text);

        stats = getYearlyStatsJsonString();

        try {
            JSONObject jsonObject = new JSONObject(stats);

            totalDistance = jsonObject.getString("totalDistance");
            totalDistance = String.format("%.2f", Double.parseDouble(totalDistance)*0.621371);

            totalRunCount = jsonObject.getString("runCount");

            averagePace = jsonObject.getString("averagePace");
            averagePace = minPerKmtoMinPerMile(averagePace);

            averageRunLength = jsonObject.getString("averageRunLength");
            averageRunLength = String.format("%.2f", Double.parseDouble(averageRunLength)*0.621371);

            AmRuns = jsonObject.getInt("daysRunAM");
            PmRuns = jsonObject.getInt("daysRunPM");
            showPieChart(AmRuns, PmRuns);
            showDistancePerMonthBarChart();
            showScatterChart();

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        yearSummaryRunCount.setText(String.format("Total runs: %s", totalRunCount));
        yearSummaryDistance.setText(String.format("Total miles run: %s ", totalDistance));
        yearAveragePace.setText(String.format("Average pace: %s min/mi", averagePace));
        yearAverageRunLength.setText(String.format("Average run length: %s mi", averageRunLength));

        getDistancePerMonth(MainActivity.getAllActivitiesJsonString());

        return view;
    }



    private String minPerKmtoMinPerMile(String minKm) {
        String[] minKmArray = minKm.split(":");
        int min = Integer.parseInt(minKmArray[0]);
        int sec = Integer.parseInt(minKmArray[1]);
        double minMile = min + (sec/60.0);
        minMile = minMile*1.60934;
        int minMileInt = (int) minMile;
        int secMile = (int) ((minMile - minMileInt)*60);
        return String.format("%d:%d", minMileInt, secMile);
    }

    private void showPieChart(int amRuns, int pmRuns) {
        String label = "type";
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        Map<String, Integer> timeOfDayMap = new HashMap<>();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF8D60"));
        colors.add(Color.parseColor("#FF6122"));

        int totalAmRuns = amRuns;
        int totalPmRuns = pmRuns;
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

        runTimePieChart.setUsePercentValues(true);
        runTimePieChart.getDescription().setEnabled(false);
        runTimePieChart.setCenterText("Run Time of Day");
        runTimePieChart.setCenterTextSize(12);
        runTimePieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        runTimePieChart.getLegend().setEnabled(false);
        runTimePieChart.setHoleRadius(40f);
        runTimePieChart.setHoleColor(Color.parseColor("#EFEFEF"));
        runTimePieChart.setTransparentCircleRadius(45f);

        runTimePieChart.setData(pieData);
        runTimePieChart.invalidate();

        runTimePieChart.animateY(1000, Easing.EaseInOutQuad);
    }

    private void showDistancePerMonthBarChart() throws ParseException {
        Map<String, Double> distancePerMonthMap = getDistancePerMonth(MainActivity.getAllActivitiesJsonString());
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
            Date date = originalFormat.parse(month);
            String formattedDate = targetFormat.format(date);
            labels.add(formattedDate);
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
        xAxis.setLabelCount(labels.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
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

    private void showScatterChart() throws JSONException, ParseException {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF8D60"));
        colors.add(Color.parseColor("#FF6122"));


        ArrayList scatterEntries = new ArrayList<>();

        activities = MainActivity.getAllActivitiesJsonString();
        JSONArray activitiesJsonArray = new JSONArray(activities);
        for(int i = 0; i < activitiesJsonArray.length(); i++){
            JSONObject itemObj = activitiesJsonArray.getJSONObject(i);

            String date = itemObj.getString("startDateTimeLocal");
            Date dateObj = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);
            String dateStr = new SimpleDateFormat("MM/dd/yy").format(dateObj);
            date = dateStr;
            Log.d("date", date);
            //if date is not in last 12 months, break
            if(!isDateInLast12Months(date)){
                break;
            }

            String distance = itemObj.getString("distance");
            distance = String.format("%.2f", Double.parseDouble(distance)*0.621371);
            Log.d("distance", distance);

            String duration = itemObj.getString("duration");
            String durationStr = DateUtils.formatElapsedTime((long) Double.parseDouble(duration));
            Log.d("duration", durationStr);

            String pace = String.valueOf(Double.parseDouble(duration) / Double.parseDouble(distance));
            String paceStr = DateUtils.formatElapsedTime((long) Double.parseDouble(pace));
            Log.d("pace", pace);


            scatterEntries.add(new Entry(Float.parseFloat(distance), (Float.parseFloat(pace))/60));
        }

        paceVsDistanceScatterDataSet = new ScatterDataSet(scatterEntries, "Miles");
        paceVsDistanceScatterData = new ScatterData(paceVsDistanceScatterDataSet);
        paceVsDistanceScatterChart.setData(paceVsDistanceScatterData);
        paceVsDistanceScatterDataSet.setColors(colors);
        paceVsDistanceScatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        paceVsDistanceScatterDataSet.setScatterShapeSize(20f);
        paceVsDistanceScatterDataSet.setDrawValues(false);
        paceVsDistanceScatterDataSet.setScatterShapeHoleRadius(0f);

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
//        ArrayList labels = new ArrayList();
//        labels.add("7:00");
//        labels.add("7:20");
//        labels.add("7:40");
//        labels.add("8:00");
//        labels.add("8:20");
//        labels.add("8:40");
//
//        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(labels);
//        yAxisLeft.setValueFormatter(formatter);

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

    private boolean isDateInLast12Months(String date) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -12);
        Date twelveMonthsAgo = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String twelveMonthsAgoStr = sdf.format(twelveMonthsAgo);
        Log.d("twelveMonthsAgo", twelveMonthsAgoStr);
        try {
            Date dateObj = sdf.parse(date);
            Date twelveMonthsAgoObj = sdf.parse(twelveMonthsAgoStr);
            if (dateObj.after(twelveMonthsAgoObj)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    private List<String> getLast12Months() {
        final int monthsInYear = 12;
        YearMonth currentMonth = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentMonth = YearMonth.now(ZoneId.of("America/New_York"));
        }
        YearMonth sameMonthLastYear = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sameMonthLastYear = currentMonth.minusYears(1);
        }
        List<YearMonth> months = new ArrayList<>(monthsInYear);
        for (int i = 1; i <= monthsInYear; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                months.add(sameMonthLastYear.plusMonths(i));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("months", months.toString());
            return months.stream().map(YearMonth::toString).collect(Collectors.toList());
        }
        return null;
    }

    private Map<String, Double> getDistancePerMonth(String json) {
        Map<String, Double> distancePerMonthMap = null;
        try {
            last12Months = getLast12Months();
            distancePerMonthMap = new HashMap<>();
            boolean withinLast12Months = true;
            String jsonDataString = json;
            JSONArray jsonArray = new JSONArray(jsonDataString);
            int index = 0;

            while (withinLast12Months) {

                JSONObject itemObj = jsonArray.getJSONObject(index);

                String date = itemObj.getString("startDateTimeLocal");
                String[] dateArray = date.split("-");
                String yearMonth = dateArray[0] + "-" + dateArray[1];

                if (last12Months.contains(yearMonth)) {
                    String distanceStr = itemObj.getString("distance");
                    double distance = Double.parseDouble(distanceStr) * 0.621371;
                    if (distancePerMonthMap.containsKey(yearMonth)) {
                        distancePerMonthMap.put(yearMonth, distancePerMonthMap.get(yearMonth) + distance);
                    } else {
                        distancePerMonthMap.put(yearMonth, distance);
                    }
                } else {
                    withinLast12Months = false;
                }

                index++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        distancePerMonthMap = sortByKey(distancePerMonthMap);
        Log.d("distancePerMonthMap", distancePerMonthMap.toString());
        return distancePerMonthMap;
    }

    private Map<String, Double> sortByKey(Map<String, Double> distancePerMonthMap) {
        Map<String, Double> sortedMap = new LinkedHashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            distancePerMonthMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        }
        return sortedMap;
    }


}
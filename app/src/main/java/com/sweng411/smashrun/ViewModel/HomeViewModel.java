package com.sweng411.smashrun.ViewModel;

import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sweng411.smashrun.Model.Run;
import com.sweng411.smashrun.Repo.SmashRunRepository;
import com.sweng411.smashrun.State.DistancePerMonthBarChartState;
import com.sweng411.smashrun.State.RunTimePieChartState;
import com.sweng411.smashrun.State.ScatterPlotEntry;
import com.sweng411.smashrun.State.YearSummaryUiState;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeViewModel extends ViewModel {
    private static DistancePerMonthBarChartState storedBarChartState;
    private static RunTimePieChartState storedPieChartState;
    private static YearSummaryUiState storedYearSummaryState;
    private static ArrayList<ScatterPlotEntry> storedScatterState;

    private SmashRunRepository repository = SmashRunRepository.GetInstance();
    private final MutableLiveData<DistancePerMonthBarChartState> distancePerMonthBarChartStateMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<RunTimePieChartState> runTimePieChartStateMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<YearSummaryUiState> yearSummaryLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<ScatterPlotEntry>> scatterPlotLiveData = new MutableLiveData<>();

    public LiveData<YearSummaryUiState> GetYearSummaryState(boolean refresh) {
        if(!refresh && storedYearSummaryState != null) {
            yearSummaryLiveData.setValue(storedYearSummaryState);
            return  yearSummaryLiveData;
        }
        repository.GetYearlyStats(yearSummary -> {
            YearSummaryUiState yearSummaryUiState = new YearSummaryUiState();

            yearSummaryUiState.TotalDistance = String.format("%.2f", yearSummary.Distance * 0.621371);
            yearSummaryUiState.TotalRunCount = String.valueOf(yearSummary.RunCount);
            yearSummaryUiState.AveragePace = minPerKmtoMinPerMile(yearSummary.AveragePace);
            //Why is this multiplied by 0.621
            yearSummaryUiState.AverageRunLength = String.format("%.2f", yearSummary.AverageRunLength * 0.621371);

            yearSummaryLiveData.postValue(yearSummaryUiState);
        });

        return yearSummaryLiveData;
    }

    public LiveData<RunTimePieChartState> GetPieChartState(boolean refresh) {
        if(!refresh && storedPieChartState != null) {
            runTimePieChartStateMutableLiveData.setValue(storedPieChartState);
            return runTimePieChartStateMutableLiveData;
        }

        repository.GetYearlyStats(stats ->{
            RunTimePieChartState state = new RunTimePieChartState();
            state.AM = stats.AmRuns;
            state.PM = stats.PmRuns;
            runTimePieChartStateMutableLiveData.postValue(state);
        });
        return runTimePieChartStateMutableLiveData;
    }

    public LiveData<DistancePerMonthBarChartState> GetDistancePerMonthState(boolean refresh) {
        if(!refresh && storedBarChartState != null) {
            distancePerMonthBarChartStateMutableLiveData.setValue(storedBarChartState);
            return  distancePerMonthBarChartStateMutableLiveData;
        }
        repository.GetRuns(runs -> {
            DistancePerMonthBarChartState state = new DistancePerMonthBarChartState();


            state.DistancePerMonthMap = getDistancePerMonth(runs);
            distancePerMonthBarChartStateMutableLiveData.postValue(state);
        });

        return distancePerMonthBarChartStateMutableLiveData;
    }

    public LiveData<ArrayList<ScatterPlotEntry>> GetScatterPlotEntries(boolean refresh) {
        if(!refresh && storedScatterState != null) {
            scatterPlotLiveData.setValue(storedScatterState);
            return scatterPlotLiveData;
        }
        repository.GetRuns(runs -> {
            ArrayList<ScatterPlotEntry> entries = new ArrayList<>();

            for (Run run: runs) {
                ScatterPlotEntry entry = new ScatterPlotEntry();

                //Checks if date is within last 12 Months, if not it skips the run
                try {
                    Date dateObj = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(run.Date);
                    String date = new SimpleDateFormat("MM/dd/yy").format(dateObj);
                    if(!isDateInLast12Months(date)) {
                        continue;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }



                entry.Distance = run.Distance * Float.parseFloat("0.621371");
                entry.Pace = run.Duration / (run.Distance * Float.parseFloat("0.621371"));
                entries.add(entry);
            }

            scatterPlotLiveData.postValue(entries);

        });

        return scatterPlotLiveData;
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

    private Map<String, Double> getDistancePerMonth(ArrayList<Run> runs) {
        Map<String, Double> distancePerMonthMap = null;
        List<String> last12Months = getLast12Months();
        distancePerMonthMap = new HashMap<>();
        boolean withinLast12Months = true;

        int index = 0;

        while (withinLast12Months) {
            if(index >= runs.size()) break;

            Run run = runs.get(index);

            String date = run.Date;
            String[] dateArray = date.split("-");
            if(dateArray.length < 2) break;
            String yearMonth = dateArray[0] + "-" + dateArray[1];

            if (last12Months.contains(yearMonth)) {

                double distance = run.Distance * 0.621371;
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


        distancePerMonthMap = sortByKey(distancePerMonthMap);
        Log.d("distancePerMonthMap", distancePerMonthMap.toString());
        return distancePerMonthMap;
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

}

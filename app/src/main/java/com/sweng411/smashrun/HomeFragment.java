package com.sweng411.smashrun;

import static com.sweng411.smashrun.MainActivity.getAllActivitiesJsonString;
import static com.sweng411.smashrun.MainActivity.getYearlyStatsJsonString;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

        String text = String.format("%d Running Report", MainActivity.getYear());
        yearSummaryText.setText(text);

        String stats = getYearlyStatsJsonString();
        String totalDistance = null;
        String totalRunCount = null;
        String averagePace = null;
        String averageRunLength = null;
        try {
            JSONObject jsonObject = new JSONObject(stats);

            totalDistance = jsonObject.getString("totalDistance");
            totalDistance = String.format("%.2f", Double.parseDouble(totalDistance)*0.621371);

            totalRunCount = jsonObject.getString("runCount");

            averagePace = jsonObject.getString("averagePace");
            averagePace = minPerKmtoMinPerMile(averagePace);

            averageRunLength = jsonObject.getString("averageRunLength");
            averageRunLength = String.format("%.2f", Double.parseDouble(averageRunLength)*0.621371);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        yearSummaryRunCount.setText(String.format("Total runs: %s", totalRunCount));
        yearSummaryDistance.setText(String.format("Total miles run: %s ", totalDistance));
        yearAveragePace.setText(String.format("Average pace: %s min/mi", averagePace));
        yearAverageRunLength.setText(String.format("Average run length: %s mi", averageRunLength));

        return view;
    }

    //SimpleDateFormat sdf = new SimpleDateFormat("min:sec");

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




}
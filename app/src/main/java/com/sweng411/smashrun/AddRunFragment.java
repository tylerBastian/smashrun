package com.sweng411.smashrun;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRunFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText date;
    private EditText distance;
    private EditText time;
    private EditText duration;
    private Button addRunButton;
    private Button resetButton;

    public AddRunFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RunsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddRunFragment newInstance(String param1, String param2) {
        AddRunFragment fragment = new AddRunFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_run, container, false);

        getActivity().setTitle("Add New Run");

        date = view.findViewById(R.id.date_edit);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        addRunButton = view.findViewById(R.id.button_submit);
        addRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = view.findViewById(R.id.date_edit);
                distance = view.findViewById(R.id.distance_edit);
                time = view.findViewById(R.id.time_edit);
                duration = view.findViewById(R.id.duration_edit);

                if (date.getText().toString().isEmpty() || distance.getText().toString().isEmpty() || time.getText().toString().isEmpty() || duration.getText().toString().isEmpty()) {
                    return;
                }

                String dateString = date.getText().toString();
                Double distanceDouble = Double.parseDouble(distance.getText().toString());
                String timeString = time.getText().toString();
                String durationString = duration.getText().toString();
                //convert duration hh:mm:ss to seconds
                String[] durationArray = durationString.split(":");
                int durationSeconds = Integer.parseInt(durationArray[0]) * 3600 + Integer.parseInt(durationArray[1]) * 60 + Integer.parseInt(durationArray[2]);

                //convert to double
                double durationDouble = (double) durationSeconds;


//                String[] durationArray = durationString.split(":");
//                int durationSeconds = Integer.parseInt(durationArray[0]) * 60 + Integer.parseInt(durationArray[1]);
//                //convert to double
//                double durationDouble = (double) durationSeconds;

                //convert date string to yyyy-mm-dd format
                String[] dateArray = dateString.split("/");
                String year = dateArray[2];
                String month = dateArray[1];
                String day = dateArray[0];
                String dateFormatted = year + "-" + month + "-" + day;

                //startDateTimeLocal in ISO 8601 format
                String startDateTimeLocal = dateFormatted + "T" + timeString + ":00-04:00";

                //convert to json
                String json = "{\"startDateTimeLocal\":\"" + startDateTimeLocal + "\",\"distance\":" + distanceDouble + ",\"duration\":" + durationDouble + "}";
                Log.d("json", json);

                //post to api
                postRunJson(json);

                //clear fields
                date.setText("");
                distance.setText("");
                time.setText("");
                duration.setText("");
            }
        });

        resetButton = view.findViewById(R.id.button_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date.setText("");
                distance = view.findViewById(R.id.distance_edit);
                distance.setText("");
                time = view.findViewById(R.id.time_edit);
                time.setText("");
                duration = view.findViewById(R.id.duration_edit);
                duration.setText("");
            }
        });

        return view;
    }


    private void postRunJson(String json) {
        String url = "https://api.smashrun.com/v1/my/activities/";
        String token = MainActivity.getSharedPref().getString("token", "");
        String auth = MainActivity.getSharedPref().getString("auth", "");

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", auth + " " + token)
                .post(okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json))
                .build();

        MainActivity.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AddRunFragment", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("AddRunFragment", "onResponse: " + response.body().string());
            }
        });
        }



}
package com.sweng411.smashrun.Activity.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sweng411.smashrun.Model.Run;
import com.sweng411.smashrun.R;
import com.sweng411.smashrun.ViewModel.RunEditorViewModel;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRunFragment extends Fragment {

    private EditText date;
    private EditText distance;
    private EditText time;
    private EditText duration;
    private Button addRunButton;
    private Button resetButton;
    private RelativeLayout addRunLayout;

    private RunEditorViewModel viewModel;
    public AddRunFragment() {
        // Required empty public constructor
    }

    public static AddRunFragment newInstance(String param1, String param2) {
        AddRunFragment fragment = new AddRunFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RunEditorViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_run, container, false);

        getActivity().setTitle("Add New Run");
        addRunLayout = view.findViewById(R.id.add_run_parent_layout);
        addRunLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide current fragment
                getActivity().getSupportFragmentManager().beginTransaction().remove(AddRunFragment.this).commit();
            }
        });

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

                if (date.getText().toString().isEmpty() ||
                    distance.getText().toString().isEmpty() ||
                    time.getText().toString().isEmpty() ||
                    duration.getText().toString().isEmpty())
                {
                    return;
                }

                String dateString = date.getText().toString();
                float distanceDouble = Float.parseFloat(distance.getText().toString());
                String timeString = time.getText().toString();
                String durationString = duration.getText().toString();
                //convert duration hh:mm:ss to seconds
                String[] durationArray = durationString.split(":");
                int durationSeconds = Integer.parseInt(durationArray[0]) * 3600 + Integer.parseInt(durationArray[1]) * 60 + Integer.parseInt(durationArray[2]);

                //convert date string to yyyy-mm-dd format
                String[] dateArray = dateString.split("/");
                String year = dateArray[2];
                String month = dateArray[1];
                String day = dateArray[0];
                String dateFormatted = year + "-" + month + "-" + day;

                //startDateTimeLocal in ISO 8601 format
                String startDateTimeLocal = dateFormatted + "T" + timeString + ":00-04:00";

                //convert to json
                Run run = new Run();
                run.Duration = durationSeconds;
                run.Distance = distanceDouble;
                run.Date = startDateTimeLocal;

                viewModel.PostRun(run);

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
}
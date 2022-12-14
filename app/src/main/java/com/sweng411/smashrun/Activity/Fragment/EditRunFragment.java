package com.sweng411.smashrun.Activity.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.sweng411.smashrun.Model.Run;
import com.sweng411.smashrun.R;
import com.sweng411.smashrun.State.UserRunUiState;
import com.sweng411.smashrun.ViewModel.HomeViewModel;
import com.sweng411.smashrun.ViewModel.RunEditorViewModel;

import java.util.Calendar;


public class EditRunFragment extends DialogFragment {



    private EditText dateText;
    private EditText distanceText;
    private EditText timeText;
    private EditText durationText;
    private Button applyButton;
    private Button deleteButton;

    private RunEditorViewModel viewModel;


    public EditRunFragment() {

    }

    public static EditRunFragment newInstance(UserRunUiState runToEdit) {
        EditRunFragment fragment = new EditRunFragment();
        Bundle args = new Bundle();
        args.putInt("id", runToEdit.runId);
        args.putString("date", runToEdit.date);
        args.putString("time", runToEdit.time);
        args.putString("distance", runToEdit.distance);
        args.putString("duration", runToEdit.duration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RunEditorViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_add_run, container);

        Bundle args = getArguments();



        dateText = view.findViewById(R.id.date_edit);
        durationText = view.findViewById(R.id.duration_edit);
        distanceText = view.findViewById(R.id.distance_edit);
        timeText = view.findViewById(R.id.time_edit);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                String[] dateArray = args.getString("date").split("/");

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, 2000+ Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[1]) - 1, Integer.parseInt(dateArray[0]));
                datePickerDialog.show();
            }
        });

        applyButton = view.findViewById(R.id.button_reset);
        applyButton.setText("Apply");
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Run run = RetrieveDataFromEditor();
                if(run != null) {
                    viewModel.EditRun(run);
                    dismiss();
                }

            }
        });

        deleteButton = view.findViewById(R.id.button_submit);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewModel.DeleteRun(getArguments().getInt("id"));
                dismiss();
            }
        });
        FillRunEditor(args.getString("date"), args.getString("time"), args.getString("duration"), args.getString("distance"));

        return view;

    }

    private Run RetrieveDataFromEditor() {
        if (dateText.getText().toString().isEmpty() ||
                distanceText.getText().toString().isEmpty() ||
                timeText.getText().toString().isEmpty() ||
                distanceText.getText().toString().isEmpty())
        {
            return null;
        }

        String dateString = dateText.getText().toString();
        float distanceDouble = Float.parseFloat(distanceText.getText().toString());
        String timeString = timeText.getText().toString();
        String durationString = durationText.getText().toString();
        //convert duration hh:mm:ss to seconds
        String[] durationArray = durationString.split(":");
        int durationSeconds = Integer.parseInt(durationArray[0]) * 3600 + Integer.parseInt(durationArray[1]) * 60;

        //convert date string to yyyy-mm-dd format
        String[] dateArray = dateString.split("/");
        String year = dateArray[2];
        String month = dateArray[1];
        String day = dateArray[0];
        String dateFormatted = year + "-" + day + "-" + month;

        //startDateTimeLocal in ISO 8601 format
        String startDateTimeLocal = dateFormatted + "T" + timeString + ":00-04:00";

        //convert to json
        Run run = new Run();
        run.Duration = durationSeconds;
        run.Distance = distanceDouble;
        run.Date = startDateTimeLocal;
        run.ActivityId = getArguments().getInt("id");
        return run;
    }

    private void FillRunEditor(String date, String time, String duration, String distance) {
        String[] dateArray = date.split("/");
        Log.d("ERF", date);
        Log.d("ERF", dateArray[0]);
        String year = dateArray[2];
        String month = dateArray[1];
        String day = dateArray[0];

        String dateString = day + "/" + month + "/" + year;
        dateText.setText(dateString);
        durationText.setText(duration);
        distanceText.setText(distance);
        timeText.setText(time);

    }
}

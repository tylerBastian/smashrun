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
import com.sweng411.smashrun.R;
import com.sweng411.smashrun.State.UserRunUiState;

import java.util.Calendar;


public class EditRunFragment extends DialogFragment {



    private EditText dateText;
    private EditText distanceText;
    private EditText timeText;
    private EditText durationText;
    private Button applyButton;
    private Button deleteButton;
    private RelativeLayout addRunLayout;


    UserRunUiState runToEdit;

    public EditRunFragment() {

    }

    public static EditRunFragment newInstance(UserRunUiState runToEdit) {
        EditRunFragment fragment = new EditRunFragment();
        Bundle args = new Bundle();
        args.putString("date", runToEdit.date);
        args.putString("time", runToEdit.time);
        args.putString("distance", runToEdit.distance);
        args.putString("duration", runToEdit.duration);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_add_run, container);

        Bundle args =getArguments();

        addRunLayout = view.findViewById(R.id.add_run_parent_layout);

        Log.d("ERF", "Creating ERF");

        addRunLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide current fragment
               // getActivity().getSupportFragmentManager().beginTransaction().remove(EditRunFragment.this).commit();
            }
        });

        dateText = view.findViewById(R.id.date_edit);
        durationText = view.findViewById(R.id.duration_edit);
        distanceText = view.findViewById(R.id.distance_edit);
        timeText = view.findViewById(R.id.time_edit);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        applyButton = view.findViewById(R.id.button_reset);
        applyButton.setText("Apply");
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        deleteButton = view.findViewById(R.id.button_submit);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateText.setText("");
                distanceText = view.findViewById(R.id.distance_edit);
                distanceText.setText("");
                timeText.findViewById(R.id.time_edit);
                timeText.setText("");
                durationText = view.findViewById(R.id.duration_edit);
                durationText.setText("");
            }
        });
        FillRunEditor(args.getString("date"), args.getString("time"), args.getString("duration"), args.getString("distance"));

        return view;

    }

    void FillRunEditor(String date, String time, String duration, String distance) {
        String[] dateArray = date.split("/");
        Log.d("ERF", date);
        Log.d("ERF", dateArray[0]);
        String year = dateArray[2];
        String month = dateArray[1];
        String day = dateArray[0];

        String dateString = day + "/" + (month + 1) + "/" + year;
        dateText.setText(dateString);
        durationText.setText(duration);
        distanceText.setText(distance);
        timeText.setText(time);

    }
}

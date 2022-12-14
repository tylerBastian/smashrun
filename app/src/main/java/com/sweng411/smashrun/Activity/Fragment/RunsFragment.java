package com.sweng411.smashrun.Activity.Fragment;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sweng411.smashrun.R;
import com.sweng411.smashrun.RunListAdapter;
import com.sweng411.smashrun.State.UserRunUiState;
import com.sweng411.smashrun.ViewModel.RunViewModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunsFragment extends Fragment implements RunListAdapter.ListItemClickListener {


    private RecyclerView mRecyclerView;

    private  RunListAdapter runsListAdapter;
    private RunViewModel viewModel;

    private static final String TAG = "ListFragment";

    List<UserRunUiState> runs;

    public RunsFragment() {
        // Required empty public constructor
    }

    public static RunsFragment newInstance(String param1, String param2) {
        RunsFragment fragment = new RunsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RunViewModel.class);


        //Pulls LiveDataRuns from View Model and observes changes to variable, when changed it updates UI
        //That way you dont need to pull the data before you attempt to display
        viewModel.GetUserRunsState(false).observe(this, (List<UserRunUiState> userRuns) -> {
            InitRecyclerView(userRuns);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_runs, container, false);
        mRecyclerView = view.findViewById(R.id.recentRunsList);
        getActivity().setTitle("Recent Runs");
        return view;
    }


    private void InitRecyclerView(List<UserRunUiState> runs) {
        //Sets up the data when it is received from the ViewModel
        this.runs = runs;
        Log.d(TAG, "Init Recycler View");

        runsListAdapter = new RunListAdapter(getContext(), runs, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(runsListAdapter);

    }


    @Override
    public void onListItemClick(int position) {

        Log.d("Test", runs.get(position).date);
        EditRunFragment.newInstance(runs.get(position)).show(getChildFragmentManager(), null);
    }

    public void Refresh() {
        viewModel.GetUserRunsState(true).observe(this, (List<UserRunUiState> userRuns) -> {
            InitRecyclerView(userRuns);
        });
    }
}


package com.sweng411.smashrun;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sweng411.smashrun.State.UserRunUiState;
import com.sweng411.smashrun.ViewModel.RunViewModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView mRecyclerView;


    private RunViewModel viewModel;

    private static final String TAG = "ListFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RunsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunsFragment newInstance(String param1, String param2) {
        RunsFragment fragment = new RunsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RunViewModel.class);


        //Pulls LiveDataRuns from View Model and observes changes to variable, when changed it updates UI
        //That way you dont need to pull the data before you attempt to display
        viewModel.GetUserRuns().observe(this, (List<UserRunUiState> userRuns) -> {
            InitRecyclerView(userRuns);
        });

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


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
        Log.d(TAG, "Init Recycler View");

        RunListAdapter mAdapter = new RunListAdapter(getContext(), runs);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }



}


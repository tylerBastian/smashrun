package com.sweng411.smashrun.Activity.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sweng411.smashrun.R;
import com.sweng411.smashrun.State.ProfileUiState;
import com.sweng411.smashrun.ViewModel.ProfileViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "Profile";
    private ProfileViewModel viewModel;
    private RecyclerView mRecyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView userName;
    private TextView dateJoined;
    private TextView lastRunDate;
    private ImageView profilePicture;

    public ProfileFragment() {
        // Required empty public constructor
    }
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel =  new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.GetProfileState(false).observe(this, state -> {
            Log.d(TAG, "Updating Profile");
            UpdateProfile(state);
        });

    }
    void UpdateProfile(ProfileUiState state){
        userName.setText(String.format("Name:\n%s %s", state.fName, state.lName));
        dateJoined.setText(String.format("Date Joined:\n %s", state.dateJoinedUTC));
        lastRunDate.setText(String.format("Last Run Date:\n %s", state.dateLastRunUTC));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        getActivity().setTitle("Profile");

        userName = view.findViewById(R.id.FullName);
        dateJoined = view.findViewById(R.id.JoinDate);
        lastRunDate = view.findViewById(R.id.LastRunDate);
        profilePicture = view.findViewById(R.id.ProfilePicture);

        return view;
    }
}
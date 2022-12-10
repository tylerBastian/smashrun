package com.sweng411.smashrun;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sweng411.smashrun.State.UserBadgeUiState;
import com.sweng411.smashrun.ViewModel.BadgeViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BadgesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BadgesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BadgeViewModel viewModel;
    private RecyclerView mRecyclerView;

    public BadgesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BadgesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BadgesFragment newInstance(String param1, String param2) {
        BadgesFragment fragment = new BadgesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BadgeViewModel.class);

        viewModel.GetBadgesState(false).observe(this, (List<UserBadgeUiState> userBadges) -> {
            InitRecyclerView(userBadges);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_badges, container, false);
        mRecyclerView = view.findViewById(R.id.badgesList);

        getActivity().setTitle("Badges Earned");

        return view;
    }

    private void InitRecyclerView(List<UserBadgeUiState> badges) {
        BadgeListAdapter adapter = new BadgeListAdapter(getContext(), badges);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(true);
    }
}
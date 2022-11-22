package com.sweng411.smashrun;

import static com.sweng411.smashrun.MainActivity.clearCookies;
import static com.sweng411.smashrun.MainActivity.getOkHttpClient;
import static com.sweng411.smashrun.MainActivity.getSharedPref;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button get_test = (Button) view.findViewById(R.id.get_test);
        Button logout = (Button) view.findViewById(R.id.logout);

        getActivity().setTitle("Overview");

        get_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllActivities();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPref().edit();
                editor.putString("token", "");
                editor.putString("auth", "");
                editor.apply();
                //This is to make it so you have to re input your login stuff on their webpage, mostly for testing purposes
                clearCookies(view.getContext());
                sendToLoginActivity();
            }
        });

        return view;
    }

    private void sendToLoginActivity() {
        //To send user to Login Activity
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
    }


    public void getAllActivities(){
        String url = "https://api.smashrun.com/v1/my/activities";
        String token = getSharedPref().getString("token", "");
        String auth = getSharedPref().getString("auth", "");
        String response = "";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Log.d("built request", "success");
        Log.d("request", request.toString());
        getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.d("onFailure", "failure");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("onResponse", "entered");
                if (response.isSuccessful()) {
                    Log.d("onResponse", "success");
                    final String myResponse = response.body().string();
                    Log.d("Response", myResponse);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Response", myResponse);
                        }
                    });
                }
            }
        });

    }


}
package com.sweng411.smashrun;

import static com.sweng411.smashrun.MainActivity.getJsonString;
import static com.sweng411.smashrun.MainActivity.getOkHttpClient;
import static com.sweng411.smashrun.MainActivity.getSharedPref;
import static com.sweng411.smashrun.MainActivity.isListLoaded;
import static com.sweng411.smashrun.MainActivity.setJsonString;
import static com.sweng411.smashrun.MainActivity.setListLoaded;

import static java.lang.Thread.sleep;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView mRecyclerView;
    private List<Object> viewItems = new ArrayList<>();

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final String TAG = "ListFragment";
    //private String jsonString;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListFragment() {
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
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
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
        View view = inflater.inflate(R.layout.fragment_list, container, false);
//        if (!isListLoaded()){
//            getRecentActivities();
//            Log.d(TAG, "onCreateView: " + "made api call");
//            setListLoaded(true);
//            try {
//                sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        Log.d(TAG, "onCreateView: " + getJsonString());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recentRunsList);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RunListAdapter(getContext(), viewItems);
        mRecyclerView.setAdapter(mAdapter);

        addItemsFromJSON(getJsonString());

        return view;
    }


    //get 10 most recent activities
//    public void getRecentActivities(){
//        String url = "https://api.smashrun.com/v1/my/activities/search?page=0&count=10";
//        String token = getSharedPref().getString("token", "");
//        String auth = getSharedPref().getString("auth", "");
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url(url)
//                .addHeader("Authorization", "Bearer " + token)
//                .build();
//        Log.d("request", request.toString());
//        getOkHttpClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                e.printStackTrace();
//                Log.d("onFailure", "failure");
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                Log.d("onResponse", "entered");
//                if (response.isSuccessful()) {
//                    Log.d("onResponse", "success");
//                    setJsonString(response.body().string());
//                    Log.d("Response", getJsonString());
//                    Log.d("returnString", getJsonString());
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d("Response", getJsonString() + " in run");
//                        }
//                    });
//                }
//            }
//        });
//
//    }


    private void addItemsFromJSON(String json) {
        try {
            String jsonDataString = json;
            JSONArray jsonArray = new JSONArray(jsonDataString);

            for (int i=0; i<20; ++i) {

                JSONObject itemObj = jsonArray.getJSONObject(i);

                String date = itemObj.getString("startDateTimeLocal");
                Date dateObj = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);
                String dateStr = new SimpleDateFormat("MM/dd/yyyy").format(dateObj);
                date = dateStr;
                Log.d("date", date);

                String distance = itemObj.getString("distance");
                distance = String.format("%.2f", Double.parseDouble(distance)*0.621371);
                Log.d("distance", distance);

                String duration = itemObj.getString("duration");
                String durationStr = DateUtils.formatElapsedTime((long) Double.parseDouble(duration));
                Log.d("duration", durationStr);

                String pace = String.valueOf(Double.parseDouble(duration) / Double.parseDouble(distance));
                String paceStr = DateUtils.formatElapsedTime((long) Double.parseDouble(pace));
                Log.d("pace", pace);

                String calories = itemObj.getString("calories");
                Log.d("calories", calories);

                Runs runs = new Runs(date, distance, durationStr, paceStr, calories);
                viewItems.add(runs);
            }

        } catch (JSONException e) {
            Log.d(TAG, "addItemsFromJSON: ", e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
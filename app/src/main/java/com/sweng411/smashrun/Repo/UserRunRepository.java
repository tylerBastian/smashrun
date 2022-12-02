package com.sweng411.smashrun.Repo;


import static com.sweng411.smashrun.MainActivity.getSharedPref;

import android.util.Log;

import androidx.annotation.NonNull;

import com.sweng411.smashrun.Model.Run;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import androidx.lifecycle.LiveData;


//Uses Singleton pattern, only needs to be one and is easy way to access it
public class UserRunRepository {
    private static final UserRunRepository instance = new UserRunRepository();

    private OkHttpClient httpClient;

    private UserRunRepository() {
        httpClient = new OkHttpClient();
    }


    public static UserRunRepository GetInstance() {
        return instance;
    }


    //Gets All Runs of a user, can be changed to take a parameter for only a certain amount
    public void GetRuns(com.sweng411.smashrun.Callback<ArrayList<Run>> callback) {
        String url = "https://api.smashrun.com/v1/my/activities";
        String token = getSharedPref().getString("token", "");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();


        ArrayList<Run> runList = new ArrayList<>();

        httpClient.newCall(request).enqueue(new Callback() {
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
                    JSONArray responseJSON;
                    try {
                        responseJSON = new JSONArray(response.body().string());
                        for (int i = 0; i < responseJSON.length(); i++) {

                            runList.add(CreateUserRunFromJson(responseJSON.getJSONObject(i)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                //This calls the ViewModel back to let it know it can use the data
                callback.HandleRepoData(runList);

            }
        });


    }


    Run CreateUserRunFromJson(JSONObject userRunJson) {
        Run userRun = new Run();

        userRun.ActivityId = userRunJson.optInt("activityId", 0);
        userRun.Calories = userRunJson.optInt("calories", 0);
        userRun.Distance = (float) userRunJson.optDouble("distance", 0);
        userRun.Duration = (float) userRunJson.optDouble("duration", 0);
        String date = userRunJson.optString("startDateTimeLocal");
        Date dateObj = null;
        try {
            dateObj = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);
        } catch (ParseException e) {
            new SimpleDateFormat("");
        }
        String dateStr = new SimpleDateFormat("MM/dd/yy").format(dateObj);
        userRun.Date = dateStr;
        return userRun;
    }
}



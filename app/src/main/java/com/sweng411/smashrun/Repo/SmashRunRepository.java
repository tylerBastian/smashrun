

package com.sweng411.smashrun.Repo;


import static com.sweng411.smashrun.MainActivity.getSharedPref;

import android.app.Application;
import android.util.Log;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.sweng411.smashrun.App;
import com.sweng411.smashrun.Model.Run;
import com.sweng411.smashrun.Model.YearSummary;
import com.sweng411.smashrun.RepoCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

//Uses Singleton pattern, only needs to be one and is easy way to access it



public class SmashRunRepository {
    private static final String TAG = "Repo";
    private static SmashRunRepository instance;

    private OkHttpClient httpClient;

    private SmashRunRepository() {
        instance = this;

        httpClient = new OkHttpClient();
    }




    public static SmashRunRepository GetInstance() {
        if(instance == null) {
            new SmashRunRepository();
        }
        return instance;
    }

    //Gets All Runs of a user, can be changed to take a parameter for only a certain amount
    public void GetRuns(RepoCallback<ArrayList<Run>> callback) {

        ArrayList<Run> runs = new ArrayList<>();

        Log.d(TAG, "Fetching Runs");
        Log.d(TAG, Log.getStackTraceString(new Throwable("message")));
        String url = "https://api.smashrun.com/v1/my/activities";
        String token = getSharedPref().getString("token", "");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();


        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.d("onFailure", "failure");
                //Might as well send something back
                callback.HandleRepoData(runs);
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

                            runs.add(CreateUserRunFromJson(responseJSON.getJSONObject(i)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                //This calls the ViewModel back to let it know it can use the data
                callback.HandleRepoData(runs);

            }
        });


    }


    //Get yearly stats
    public void GetYearlyStats(RepoCallback<YearSummary> callback){
        YearSummary yearSummary = new YearSummary();
        String url = "https://api.smashrun.com/v1/my/stats/" + Calendar.getInstance().get(Calendar.YEAR);
        String token = getSharedPref().getString("token", "");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();




        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.d("onFailure", "failure");
                callback.HandleRepoData(yearSummary);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("onResponse", "entered");
                if (response.isSuccessful()) {
                    Log.d("onResponse", "success");

                    try {
                        JSONObject responseJSON = new JSONObject((response.body().string()));
                        yearSummary.Distance = responseJSON.optInt("totalDistance");
                        yearSummary.RunCount = responseJSON.optInt("runCount");
                        yearSummary.AveragePace = responseJSON.getString("averagePace");
                        yearSummary.AverageRunLength = responseJSON.optDouble("averageRunLength");
                        yearSummary.AmRuns = responseJSON.optInt("daysRunAM");
                        yearSummary.PmRuns = responseJSON.optInt("daysRunPM");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    callback.HandleRepoData(yearSummary);
                }
            }
        });
    }


    Run CreateUserRunFromJson(JSONObject userRunJson) {
        Run userRun = new Run();

        userRun.ActivityId = userRunJson.optInt("activityId", 0);
        userRun.Calories = userRunJson.optInt("calories", 0);
        userRun.Distance = (float) userRunJson.optDouble("distance", 0);
        userRun.Duration = (float) userRunJson.optDouble("duration", 0);
        userRun.Date= userRunJson.optString("startDateTimeLocal");
        return userRun;
    }


}



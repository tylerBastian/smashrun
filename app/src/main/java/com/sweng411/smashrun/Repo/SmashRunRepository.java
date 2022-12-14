

package com.sweng411.smashrun.Repo;


import static com.sweng411.smashrun.Activity.MainActivity.getSharedPref;

import android.util.Log;

import androidx.annotation.NonNull;

import com.sweng411.smashrun.Model.Badge;
import com.sweng411.smashrun.Model.Profile;
import com.sweng411.smashrun.Model.Run;
import com.sweng411.smashrun.Model.YearSummary;
import com.sweng411.smashrun.RepoCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

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
    public void GetProfile(RepoCallback<Profile> callback){
        Profile Profile = new Profile();
        String url = "https://api.smashrun.com/v1/my/userinfo";
        String token = getSharedPref().getString("token","");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();



        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.d("onFailure", "failure");
                callback.HandleRepoData(Profile);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("onResponse", "entered");
                if (response.isSuccessful()) {
                    Log.d("onResponse", "success");

                    try {
                        JSONObject responseJSON = new JSONObject((response.body().string()));
                        Profile.fName = responseJSON.optString("firstName");
                        Profile.lName = responseJSON.optString("lastName");
                        Profile.dateLastRunUTC = responseJSON.optString("dateTimeUTCOfLastRun");
                        Profile.dateJoinedUTC = responseJSON.optString("registrationDateUTC");
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                    callback.HandleRepoData(Profile);
                }
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

    public void PostRun(Run run) {

        String json = "{\"startDateTimeLocal\":\"" + run.Date + "\",\"distance\":" + run.Distance + ",\"duration\":" + run.Duration + "}";

        String url = "https://api.smashrun.com/v1/my/activities/";
        String token = getSharedPref().getString("token", "");
        String auth = getSharedPref().getString("auth", "");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", auth + " " + token)
                .post(okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json))
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AddRunFragment", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("AddRunFragment", "onResponse: " + response.body().string());
            }
        });
    }

    public void GetBadges(RepoCallback<ArrayList<Badge>> callback){
        ArrayList<Badge> badges = new ArrayList<>();

        Log.d(TAG, "Fetching Badges");
        Log.d(TAG, Log.getStackTraceString(new Throwable("message")));
        String url = "https://api.smashrun.com/v1/my/badges";
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
                callback.HandleRepoData(badges);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("onResponse", "entered");
                if (response.isSuccessful()) {
                    Log.d("onResponse", "success");
                    JSONArray responseJSON;
                    try {
                        responseJSON = new JSONArray(response.body().string());
                        Log.d("json", responseJSON.toString());
                        for (int i = 0; i < responseJSON.length(); i++) {

                            badges.add(CreateBadgeFromJson(responseJSON.getJSONObject(i)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                //This calls the ViewModel back to let it know it can use the data
                callback.HandleRepoData(badges);

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

    Badge CreateBadgeFromJson(JSONObject badgeJson) {
        Badge badge = new Badge();

        badge.id = badgeJson.optInt("badgeId", 0);
        badge.name = badgeJson.optString("name");
        badge.image = badgeJson.optString("image");
        badge.imageSmall = badgeJson.optString("imageSmall");
        badge.requirement = badgeJson.optString("requirement");
        badge.dateEarnedUTC = badgeJson.optString("dateEarnedUTC");
        badge.badgeOrder = badgeJson.optInt("badgeOrder", 0);

        return badge;
    }





    public void DeleteRun(int runID) {
        String url = "https://api.smashrun.com/v1/my/activities/" + runID;
        String token = getSharedPref().getString("token", "");
        String auth = getSharedPref().getString("auth", "");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", auth + " " + token)
                .delete()
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Repo", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Repo", "onResponse: " + response.body().string());
            }
        });
    }

    public void EditRun(Run run) {
        String json = "{\"startDateTimeLocal\":\"" + run.Date + "\",\"distance\":" + run.Distance + ",\"duration\":" + run.Duration + "}";

        String url = "https://api.smashrun.com/v1/my/activities/" + run.ActivityId;
        String token = getSharedPref().getString("token", "");
        String auth = getSharedPref().getString("auth", "");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", auth + " " + token)
                .patch(okhttp3.RequestBody.create(json, okhttp3.MediaType.parse("application/json; charset=utf-8")))
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Repo", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Repo", "onResponse: " + response.body().string());
            }
        });
    }
}



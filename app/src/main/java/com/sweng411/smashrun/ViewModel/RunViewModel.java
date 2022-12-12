package com.sweng411.smashrun.ViewModel;


import android.os.Looper;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sweng411.smashrun.Model.Run;
import com.sweng411.smashrun.Repo.SmashRunRepository;
import com.sweng411.smashrun.State.UserRunUiState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RunViewModel extends ViewModel {
    private static final String TAG = "RVM";
    private static List<UserRunUiState> storedRuns = null;


    private SmashRunRepository repository = SmashRunRepository.GetInstance();
    private final MutableLiveData<List<UserRunUiState>> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();


    public LiveData<List<UserRunUiState>> GetUserRunsState(boolean refresh) {

        //Uses cached data
        if(!refresh && storedRuns != null) {
            userLiveData.setValue(storedRuns);
            return userLiveData;
        }

        //Grabs data from repo using a callback
        repository.GetRuns(runs -> {
            ArrayList<UserRunUiState> states = new ArrayList<>();

            //Takes data from Repo and transforms it into what the UI consumes
            for (Run run :
                    runs) {
                UserRunUiState state = new UserRunUiState();
                state.calories = String.valueOf(run.Calories);
                state.distance = String.format("%.2f", (run.Distance * 0.621371));
                state.duration =  DateUtils.formatElapsedTime((long) run.Duration);
                String pace =  String.valueOf(run.Duration/(run.Distance * 0.621371));
                state.pace = DateUtils.formatElapsedTime((long) Double.parseDouble(pace));
                Log.d("RVM", "Model Calories: " + run.Calories);
                Log.d("RVM","State Calories: " + state.calories);

                String date = run.Date;
                Date dateObj = null;
                try {
                    dateObj = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);
                } catch (ParseException e) {
                    new SimpleDateFormat("");
                }
                String dateStr = new SimpleDateFormat("MM/dd/yy").format(dateObj);

                state.date = dateStr;

                state.time   = new SimpleDateFormat("HH:mm").format(dateObj);
                states.add(state);
            }

            //Live data is a way to update UI after it received the initial variable
            //This checks if on main thread and sets the live data accordingly
            storedRuns = states;
            userLiveData.postValue(states);
        });

        //Returns initial live data variable for UI to consume
        return userLiveData;
    }

    public LiveData<Boolean> GetIsLoading() {
        return isLoading;
    }


}
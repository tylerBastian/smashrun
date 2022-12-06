package com.sweng411.smashrun.ViewModel;


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
    private SmashRunRepository repository = SmashRunRepository.GetInstance();
    private final MutableLiveData<List<UserRunUiState>> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<List<UserRunUiState>> GetUserRunsState() {

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
                String pace =  String.valueOf(run.Duration/run.Distance);
                Log.d("RVM", pace);
                state.pace =  String.format("%.2f", Float.parseFloat(pace));
                Log.d("RVM", state.pace);

                String date = run.Date;
                Date dateObj = null;
                try {
                    dateObj = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);
                } catch (ParseException e) {
                    new SimpleDateFormat("");
                }
                String dateStr = new SimpleDateFormat("MM/dd/yy").format(dateObj);

                state.date = dateStr;
                states.add(state);
            }

            //Live data is a way to update UI after it received the initial variable
            userLiveData.postValue(states);
        });

        //Returns initial live data variable for UI to consume
        return userLiveData;
    }

    public LiveData<Boolean> GetIsLoading() {
        return isLoading;
    }


}
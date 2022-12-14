package com.sweng411.smashrun.ViewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sweng411.smashrun.Repo.SmashRunRepository;
import com.sweng411.smashrun.State.ProfileUiState;

import java.util.ArrayList;


public class ProfileViewModel extends ViewModel {
    private static final String TAG = "Profile";
    private static ProfileUiState storedProfile = null;

    private SmashRunRepository repository =  SmashRunRepository.GetInstance();
    private final MutableLiveData<ProfileUiState> profileLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<ProfileUiState> GetProfileState(boolean refresh){

        if(!refresh && storedProfile != null) {
            profileLiveData.setValue(storedProfile);
            return profileLiveData;
        }
        repository.GetProfile(profile -> {
            ArrayList<ProfileUiState> states = new ArrayList<>();
            ProfileUiState state = new ProfileUiState();
            String jDate = profile.dateJoinedUTC.substring(0,10);
            String lrDate = profile.dateLastRunUTC.substring(0,10);
            state.fName = profile.fName;
            state.lName = profile.lName;
            state.dateJoinedUTC = jDate;
            state.dateLastRunUTC = lrDate;

            storedProfile = state;
            profileLiveData.postValue(state);
        });

        return profileLiveData;
    }

}

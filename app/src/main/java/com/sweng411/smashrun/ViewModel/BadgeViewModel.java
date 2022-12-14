package com.sweng411.smashrun.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sweng411.smashrun.Model.Badge;
import com.sweng411.smashrun.Repo.SmashRunRepository;
import com.sweng411.smashrun.State.UserBadgeUiState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BadgeViewModel extends ViewModel {
    private static final String TAG = "BVM";
    private static List<UserBadgeUiState> storedBadges = null;


    private SmashRunRepository repository = SmashRunRepository.GetInstance();
    private final MutableLiveData<List<UserBadgeUiState>> badgesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();


    public LiveData<List<UserBadgeUiState>> GetBadgesState(boolean refresh) {

        //Uses cached data
        if(!refresh && storedBadges != null) {
            badgesLiveData.setValue(storedBadges);
            return badgesLiveData;
        }

        //Grabs data from repo using a callback
        repository.GetBadges(badges -> {
            ArrayList<UserBadgeUiState> states = new ArrayList<>();

            //Takes data from Repo and transforms it into what the UI consumes
            for (Badge badge :
                    badges) {
                UserBadgeUiState state = new UserBadgeUiState();
                state.id = badge.id;
                state.name = badge.name;
                state.badgeSet = badge.badgeSet;
                state.image = badge.image;
                state.imageSmall = badge.imageSmall;
                state.requirement = badge.requirement;
                state.dateEarnedUTC = badge.dateEarnedUTC;
                state.badgeOrder = badge.badgeOrder;

                Date dateObj = null;
                try {
                    dateObj = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(badge.dateEarnedUTC);
                } catch (ParseException e) {
                    new SimpleDateFormat("");
                }
                String dateStr = new SimpleDateFormat("MM/dd/yy").format(dateObj);

                state.dateEarnedUTC = dateStr;
                states.add(state);
            }

            //Live data is a way to update UI after it received the initial variable
            //This checks if on main thread and sets the live data accordingly
            //sort states by date descending
            states.sort((o1, o2) -> {
                try {
                    Date date1 = new SimpleDateFormat("MM/dd/yy").parse(o1.dateEarnedUTC);
                    Date date2 = new SimpleDateFormat("MM/dd/yy").parse(o2.dateEarnedUTC);
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            });

            storedBadges = states;
            badgesLiveData.postValue(states);
        });

        //Returns initial live data variable for UI to consume
        return badgesLiveData;
    }

    public LiveData<Boolean> GetIsLoading() {
        return isLoading;
    }


}

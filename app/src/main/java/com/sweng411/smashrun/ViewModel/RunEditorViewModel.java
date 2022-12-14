package com.sweng411.smashrun.ViewModel;

import androidx.lifecycle.ViewModel;

import com.sweng411.smashrun.Model.Run;
import com.sweng411.smashrun.Repo.SmashRunRepository;

public class RunEditorViewModel extends ViewModel {

    SmashRunRepository repository = SmashRunRepository.GetInstance();

    public void PostRun(Run run ) {
        repository.PostRuns(run);
    }
    public void DeleteRun(int runID) {
        repository.DeleteRun(runID);
    }

    public void EditRun(Run run) {
        repository.EditRun(run);
    }

}

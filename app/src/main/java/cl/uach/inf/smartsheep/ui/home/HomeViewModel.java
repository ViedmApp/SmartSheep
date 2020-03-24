package cl.uach.inf.smartsheep.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import cl.uach.inf.smartsheep.data.model.Sheep;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Sheep>> mySheeps = new MutableLiveData<>();

    public HomeViewModel() {
        mySheeps = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Sheep>> getArraySheep() {
        if (mySheeps.getValue() == null){
            loadSheeps();
        }
        return mySheeps;
    }

    private void loadSheeps() {

        //mySheeps.setValue();
    }

    public void loadSheeps(ArrayList<Sheep> sheep){
        mySheeps.setValue(sheep);
    }
}
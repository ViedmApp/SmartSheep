package cl.uach.inf.smartsheep.ui.property;

import android.widget.Spinner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import cl.uach.inf.smartsheep.data.model.Predio;

public class PropertyViewModel extends ViewModel{

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Predio>> myPredios = new MutableLiveData<>();
    private MutableLiveData<Integer> idPredio;


    public PropertyViewModel() {
        mText = new MutableLiveData<>();
        myPredios = new MutableLiveData<>();
        idPredio = new MutableLiveData<>();

        mText.setValue("Seleccione un predio");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ArrayList<Predio>> getArrayPredio() {
        if (myPredios.getValue() == null){
            loadPredios();
        }
        return myPredios;
    }

    public LiveData<Integer> getIdPredio(){
        return idPredio;
    }

    private void loadPredios() {

        //mySheeps.setValue();
    }

    public void loadPredios(ArrayList<Predio> predio){
        myPredios.setValue(predio);
    }


}
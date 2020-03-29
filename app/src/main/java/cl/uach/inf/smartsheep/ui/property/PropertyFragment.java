package cl.uach.inf.smartsheep.ui.property;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

import cl.uach.inf.smartsheep.R;
import cl.uach.inf.smartsheep.data.model.Predio;
import cl.uach.inf.smartsheep.ui.home.HomeViewModel;

public class PropertyFragment extends Fragment {

    private PropertyViewModel propertyViewModel;
    private String nombrePredio;
    private int nPredios;
    private int idPredio;
    ArrayList<String> nombresPredios = new ArrayList<>();
    ArrayList<Predio> predios = new ArrayList<>();
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        propertyViewModel =
                ViewModelProviders.of(getActivity()).get(PropertyViewModel.class);
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_property, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        final Spinner spinnerPredios = root.findViewById(R.id.idSpinnerPredios);


        propertyViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        propertyViewModel.getArrayPredio().observe(getViewLifecycleOwner(), new Observer<ArrayList<Predio>>() {
            @Override
            public void onChanged(ArrayList<Predio> predios) {


            }
        });

        propertyViewModel.getIdPredio().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

            }
        });

        predios = propertyViewModel.getArrayPredio().getValue();
        nPredios = predios.size();

        for (int i=0; i<nPredios; i++){
            nombrePredio = predios.get(i).getName();
            nombresPredios.add(nombrePredio);
        }



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, nombresPredios);

        spinnerPredios.setAdapter(adapter);

        spinnerPredios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idPredio = predios.get(position).getId();

                SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                prefs.edit().putInt("IDPREDIO", idPredio).apply();
                homeViewModel.setPredio(idPredio);
                Toast.makeText(parent.getContext(), "ID Predio: " + idPredio, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }



}

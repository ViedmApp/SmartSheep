package cl.uach.inf.smartsheep.ui.reproduction;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.uach.inf.smartsheep.R;

public class ReproductionFragment extends Fragment {

    private ReproductionViewModel mViewModel;

    public static ReproductionFragment newInstance() {
        return new ReproductionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reproduction, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ReproductionViewModel.class);
        // TODO: Use the ViewModel
    }

}

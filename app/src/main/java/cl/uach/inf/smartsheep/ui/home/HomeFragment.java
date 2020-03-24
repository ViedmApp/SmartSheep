package cl.uach.inf.smartsheep.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import cl.uach.inf.smartsheep.R;
import cl.uach.inf.smartsheep.SheepActivity;
import cl.uach.inf.smartsheep.ui.sheep.SheepFragment;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

//        Button searchSheep = (Button) root.findViewById(R.id.search_sheep);
  //      searchSheep.setOnClickListener(this);

        return root;
    }


    /*
    public void onClick(View view) {
        Fragment fragment = null;

        switch(view.getId()) {
            case R.id.search_sheep:
                Intent intent = new Intent(getActivity(), SheepActivity.class);
                startActivity(intent);
                break;
        }
    }*/


}

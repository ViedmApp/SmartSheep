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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cl.uach.inf.smartsheep.MainActivity;
import cl.uach.inf.smartsheep.R;
import cl.uach.inf.smartsheep.SheepActivity;
import cl.uach.inf.smartsheep.data.model.Sheep;
import cl.uach.inf.smartsheep.data.utils.RecyclerAdapter;
import cl.uach.inf.smartsheep.ui.sheep.SheepFragment;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerAdapter sheepAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(getActivity()).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.sheepRecyclerView);

        //Recycler Layout Manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //Divider Item Decoration
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        recyclerView.addItemDecoration(dividerItemDecoration);

        //Set Observer for Sheep Arraylist
        homeViewModel.getArraySheep().observe(getViewLifecycleOwner(), new Observer<ArrayList<Sheep>>() {
            @Override
            public void onChanged(ArrayList<Sheep> sheep) {
                updateRecycler();
            }
        });

        //Recycler Adapter
        sheepAdapter = new RecyclerAdapter(homeViewModel.getArraySheep().getValue(), R.layout.sheep_layout);
        recyclerView.setAdapter(sheepAdapter);

        return root;
    }

    public void updateRecycler(){
        sheepAdapter.notifyDataSetChanged();
    }

}

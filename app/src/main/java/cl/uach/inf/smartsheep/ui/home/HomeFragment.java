package cl.uach.inf.smartsheep.ui.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import cl.uach.inf.smartsheep.FormActivity;
import cl.uach.inf.smartsheep.R;
import cl.uach.inf.smartsheep.data.model.Sheep;
import cl.uach.inf.smartsheep.data.utils.SheepAdapter;

public class HomeFragment extends Fragment implements SheepAdapter.SheepAdapterListener {

    private HomeViewModel homeViewModel;
    private SheepAdapter sheepAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Recycler Adapter
        sheepAdapter = new SheepAdapter(getContext(),
                homeViewModel.getArraySheep().getValue(),
                R.layout.sheep_layout,
        this);
        recyclerView.setAdapter(sheepAdapter);

        homeViewModel.getArraySheep().observe(getViewLifecycleOwner(), new Observer<ArrayList<Sheep>>() {
            @Override
            public void onChanged(ArrayList<Sheep> sheep) {
                updateRecycler();
            }
        });


        final SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeViewModel.loadSheeps();
                updateRecycler();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });


        return root;
    }

    private void updateRecycler(){
        sheepAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);


        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sheepAdapter.getFilter().filter(query);
                updateRecycler();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                sheepAdapter.getFilter().filter(newText);
                updateRecycler();
                return false;
            }
        });

    }

    @Override
    public void onSheepSelected(final Sheep sheep) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.sheep_dialog, null);

        TextView textView = view.findViewById(R.id.sheepDialog_earring);
        textView.setText(sheep.getEarring());
        textView = view.findViewById(R.id.sheepDialog_earringColor);
        textView.setText(sheep.getEarringColor());
        textView = view.findViewById(R.id.sheepDialog_gender);
        textView.setText(sheep.getGender());
        textView = view.findViewById(R.id.sheepDialog_breed);
        textView.setText(sheep.getBreed());
        textView = view.findViewById(R.id.sheepDialog_birthWeight);
        textView.setText(String.valueOf(sheep.getBirth_weight()));
        textView = view.findViewById(R.id.sheepDialog_purpose);
        textView.setText(sheep.getPurpose());
        textView = view.findViewById(R.id.sheepDialog_category);
        textView.setText(sheep.getCategory());
        textView = view.findViewById(R.id.sheepDialog_merit);
        textView.setText(String.valueOf(sheep.getMerit()));
        textView = view.findViewById(R.id.sheepDialog_isDead);
        textView.setText(sheep.getIs_dead().equalsIgnoreCase("T")
         || sheep.getIs_dead().equalsIgnoreCase("1")? "Muerta": "Viva");



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Información de la Oveja");
        builder.setView(view);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNeutralButton("Editar oveja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getParentFragment().getActivity(), FormActivity.class);
                intent.putExtra("SHEEP", sheep);

                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}

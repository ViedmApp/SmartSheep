package cl.uach.inf.smartsheep.ui.home;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cl.uach.inf.smartsheep.R;
import cl.uach.inf.smartsheep.data.model.Sheep;
import cl.uach.inf.smartsheep.data.utils.RecyclerAdapter;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerAdapter sheepAdapter;
    private SearchManager searchManager;

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

        //Set Observer for Sheep Arraylist
        homeViewModel.getArraySheep().observe(getViewLifecycleOwner(), new Observer<ArrayList<Sheep>>() {
            @Override
            public void onChanged(ArrayList<Sheep> sheep) {
                updateRecycler();
            }
        });

        //Recycler Adapter
        sheepAdapter = new RecyclerAdapter(getContext(), homeViewModel.getArraySheep().getValue(), R.layout.sheep_layout);
        recyclerView.setAdapter(sheepAdapter);

        //Search manager
        //



        return root;
    }

    public void updateRecycler(){
        sheepAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);


        searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
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

        updateRecycler();
    }
}

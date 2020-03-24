package cl.uach.inf.smartsheep.data.utils;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

import cl.uach.inf.smartsheep.R;
import cl.uach.inf.smartsheep.data.model.Sheep;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<Sheep> dataSet;
    private int resource;

    public RecyclerAdapter(ArrayList<Sheep> dataSet, int resource) {
        this.dataSet = dataSet;
        this.resource = resource;
    }


    //Create new CardView
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(resource,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder viewHolder, int i) {
        viewHolder.earring.setText(dataSet.get(i).getEarring());
        viewHolder.category.setText(dataSet.get(i).getCategory());
        viewHolder.date.setText(dataSet.get(i).getBirthDate());
        viewHolder.gender.setImageResource(
                dataSet.get(i).getGender().equalsIgnoreCase("macho")?
                R.drawable.ic_mars_symbol:
                        R.drawable.ic_venus_symbol
        );

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView earring;
        TextView category;
        TextView date;
        ImageView gender;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            earring = itemView.findViewById(R.id.sheep_earring);
            category = itemView.findViewById(R.id.sheep_category);
            date = itemView.findViewById(R.id.sheep_date);
            gender = itemView.findViewById(R.id.sheep_gender);

        }
    }
}
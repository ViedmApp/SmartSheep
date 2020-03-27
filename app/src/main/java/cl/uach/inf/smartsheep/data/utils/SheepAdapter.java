package cl.uach.inf.smartsheep.data.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cl.uach.inf.smartsheep.R;
import cl.uach.inf.smartsheep.data.model.Sheep;


public class SheepAdapter extends RecyclerView.Adapter<SheepAdapter.ViewHolder> implements Filterable {
    private ArrayList<Sheep> dataSet;
    private ArrayList<Sheep> dataSetFiltered;
    private Context context;
    private int resource;
    private SheepAdapterListener listener;

    public SheepAdapter(Context context , ArrayList<Sheep> dataSet, int resource, SheepAdapterListener listener) {
        this.dataSet = dataSet;
        this.resource = resource;
        this.context = context;
        this.dataSetFiltered = dataSet;
        this.listener = listener;
    }


    //Create new CardView
    @NonNull
    @Override
    public SheepAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(resource,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SheepAdapter.ViewHolder viewHolder, int i) {
        final Sheep sheep = dataSetFiltered.get(i);
        viewHolder.itemView.setLongClickable(true);

        viewHolder.earring.setText(sheep.getEarring());
        viewHolder.category.setText(sheep.getCategory());
        viewHolder.isDead.setText(sheep.getIs_dead().equalsIgnoreCase("1")? "Muerta":"Viva");
        viewHolder.gender.setImageResource(
                sheep.getGender().equalsIgnoreCase("macho")?
                R.drawable.ic_mars_symbol:
                        R.drawable.ic_venus_symbol
        );

        String earringColor = sheep.getEarringColor();
        int color;

        switch(earringColor.toLowerCase()){
            case "orange":
            case "naranjo":
            case "naranja":
                color = R.color.secondary1_2;
                break;
            case "blue":
            case "azul":
                color = R.color.secondary2_2;
                break;
            case "green":
            case "verde":
                color = R.color.primary_3;
                break;
            default:
                color = R.color.white;
        }

        viewHolder.linearLayout.setBackgroundResource(color);

    }

    @Override
    public int getItemCount() {
        return dataSetFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString().toLowerCase();
                if (charString.isEmpty()){
                    dataSetFiltered = dataSet;
                } else {
                    ArrayList<Sheep> filteredList = new ArrayList<>();
                    for (Sheep row: dataSet){
                        String isdead = row.getIs_dead()
                                .equalsIgnoreCase("1") ? "muerta": "viva";
                        if (row.getEarring().contains(charString)
                        || row.getCategory().toLowerCase().contains(charString)
                        || row.getEarringColor().toLowerCase().contains(charString)
                        || row.getPurpose().toLowerCase().contains(charString)
                        || row.getIs_dead().toLowerCase().contains(charString)
                        || isdead.contains(charString)
                        || row.getGender().toLowerCase().contains(charString)){
                            filteredList.add(row);
                        }
                    }
                    dataSetFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataSetFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                dataSetFiltered = (ArrayList<Sheep>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface SheepAdapterListener {
        void onSheepSelected(Sheep sheep);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView earring;
        TextView category;
        TextView isDead;
        ImageView gender;
        LinearLayout linearLayout;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            earring = itemView.findViewById(R.id.sheep_earring);
            category = itemView.findViewById(R.id.sheep_category);
            isDead = itemView.findViewById(R.id.sheep_isDead);
            gender = itemView.findViewById(R.id.sheep_gender);
            linearLayout = itemView.findViewById(R.id.sheep_layout);

            itemView.setOnClickListener(
                    new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            listener.onSheepSelected(dataSetFiltered.get(getAdapterPosition()));
                        }
                    }
            );

        }

    }

}


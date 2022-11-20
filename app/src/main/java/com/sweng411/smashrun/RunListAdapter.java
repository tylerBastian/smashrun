package com.sweng411.smashrun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class RunListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE = 1;
    private final Context context;
    private final List<Object> listRecyclerItem;

    public RunListAdapter(Context context, List<Object> listRecyclerItem) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView date;
        private TextView distance;
        private TextView duration;
        private TextView pace;
        private TextView calories;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.run_date);
            distance = (TextView) itemView.findViewById(R.id.run_distance);
            pace = (TextView) itemView.findViewById(R.id.run_pace);
            calories = (TextView) itemView.findViewById(R.id.run_calories);
            duration = (TextView) itemView.findViewById(R.id.run_duration);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE:

            default:

                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.run_list_item, viewGroup, false);

                return new ItemViewHolder((layoutView));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int viewType = getItemViewType(i);

        switch (viewType) {
            case TYPE:
            default:
                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                Runs runs = (Runs) listRecyclerItem.get(i);

                itemViewHolder.date.setText(runs.getDate());
                itemViewHolder.distance.setText(runs.getDistance());
                itemViewHolder.pace.setText(runs.getPace());
                itemViewHolder.calories.setText(runs.getCalories());
                itemViewHolder.duration.setText(runs.getDuration());
        }

    }

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }
}

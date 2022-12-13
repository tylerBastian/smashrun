package com.sweng411.smashrun;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sweng411.smashrun.State.UserBadgeUiState;

import java.io.IOException;
import java.net.URL;
import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class BadgeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE = 1;
    private final Context context;
    private final List<UserBadgeUiState> listRecyclerItems;

    public BadgeListAdapter(Context context, List<UserBadgeUiState> listRecyclerItems) {
        this.context = context;
        this.listRecyclerItems = listRecyclerItems;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView requirement;
        private TextView date;
        private ImageView image;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.badge_date);
            name = itemView.findViewById(R.id.badge_name);
            requirement = itemView.findViewById(R.id.badge_requirement);
            image = itemView.findViewById(R.id.badge_image);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE:

            default:

                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.badge_list_item, viewGroup, false);

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
                UserBadgeUiState badge = listRecyclerItems.get(i);

                itemViewHolder.name.setText(badge.name);
                itemViewHolder.requirement.setText(badge.requirement);
                itemViewHolder.date.setText("Earned on: " + badge.dateEarnedUTC);
                try {
                    URL url = new URL(badge.image);
                    itemViewHolder.image.setImageBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }

    }

    @Override
    public int getItemCount() {
        return listRecyclerItems.size();
    }
}

package com.yikes.park.menu.profile.data;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yikes.park.R;
import com.yikes.park.menu.map.Objects.YikeSpot;

import java.util.ArrayList;

public class YikeSpotRecyclerView extends RecyclerView.Adapter<YikeSpotRecyclerView.ViewHolder> {

    private ArrayList<YikeSpot> ypList;
    private Context context;

    public YikeSpotRecyclerView(Context con, ArrayList<YikeSpot> arr) {
        ypList = arr;
        context = con;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ypItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ypItem = itemView.findViewById(R.id.yikeSpotItem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_yikespot_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (ypList != null) {
            holder.ypItem.setText(ypList.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return ypList.size();
    }

}




package com.example.museum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ExhibitItemAdapter extends RecyclerView.Adapter<ExhibitItemAdapter.ViewHolder> implements Filterable {

    private ArrayList<ExhibitItem> mExhibitItemData = new ArrayList<>();
    private ArrayList<ExhibitItem> mExhibitItemDataAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    ExhibitItemAdapter(Context context, ArrayList<ExhibitItem> itemsData) {
        this.mExhibitItemData = itemsData;
        this.mExhibitItemDataAll = itemsData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ExhibitItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExhibitItemAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.exhibit_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ExhibitItemAdapter.ViewHolder holder, int position) {
        ExhibitItem currentItem = mExhibitItemData.get(position);

        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mExhibitItemData.size();
    }

    @Override
    public Filter getFilter() {
        return exhibitFilter;
    }
    private Filter exhibitFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ExhibitItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = mExhibitItemDataAll.size();
                results.values = mExhibitItemDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(ExhibitItem item : mExhibitItemDataAll) {
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mExhibitItemData = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;

        ViewHolder(View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mPriceText = itemView.findViewById(R.id.price);
        }

        void bindTo(ExhibitItem currentItem){
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());
        }
    }
}

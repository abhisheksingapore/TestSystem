package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.FoodWisdom;


/**
 * Created by abhishek on 12/11/17.
 */

public class FoodWisdomRecyclerViewAdapter
        extends RecyclerView.Adapter<FoodWisdomRecyclerViewAdapter.FoodWisdomViewHolder> {
    List<FoodWisdom> foodWisdomList;
    Context contextFWRVA;

    FoodWisdomRecyclerViewAdapter(List<FoodWisdom> list) {
        if (list != null && list.size() > 0) foodWisdomList = list;
    }

    @Override
    public FoodWisdomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        contextFWRVA = parent.getContext();
        View view = LayoutInflater.from(contextFWRVA)
                .inflate(R.layout.item_foodwisdom, parent, false);
        return new FoodWisdomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodWisdomViewHolder holder, int position) {
        if (foodWisdomList == null) return;

        int reversePosition = foodWisdomList.size() - position - 1;
        FoodWisdom foodWisdomThis = foodWisdomList.get(reversePosition);

        String url = getUrl(foodWisdomThis);

        if (url != null && url.length() > 1) Picasso.with(contextFWRVA).load(url).fit()
                .placeholder(R.drawable.veganbuddy_logo).error(R.drawable.ic_info_black_24dp)
                .into(holder.imageView);

    }

    private String getUrl(FoodWisdom foodWisdomThis) {
        String url = "";

        if (foodWisdomThis.getGeneral() != null) url = foodWisdomThis.getGeneral();
        if (foodWisdomThis.getHealth() != null) url = foodWisdomThis.getHealth();
        if (foodWisdomThis.getHumour() != null) url = foodWisdomThis.getHumour();
        if (foodWisdomThis.getQuotes() != null) url = foodWisdomThis.getQuotes();
        if (foodWisdomThis.getRecipes() != null) url = foodWisdomThis.getRecipes();
        if (foodWisdomThis.getStats() != null) url = foodWisdomThis.getStats();
        if (foodWisdomThis.getUser_contributions() != null)
            url = foodWisdomThis.getUser_contributions();

        return url;
    }


    @Override
    public int getItemCount() {
        if (foodWisdomList == null || foodWisdomList.size() == 0) return 0;
        else return foodWisdomList.size();
    }

    public void setDataList(List<FoodWisdom> fw) {
        foodWisdomList = fw;
        notifyDataSetChanged();
    }

    class FoodWisdomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        FoodWisdomViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ifw_iv);
        }

    }
}

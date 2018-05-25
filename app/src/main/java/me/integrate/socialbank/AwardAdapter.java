package me.integrate.socialbank;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwardAdapter extends RecyclerView.Adapter<AwardAdapter.AwardViewHolder>{

    private List<String> items;

    private Map<String, Integer> awardsMap;

    private CustomItemClickListener listener;

    static class AwardViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        private AwardViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.award);
        }

    }

    AwardAdapter(List<String> items, CustomItemClickListener listener) {
        this.items = items;
        this.listener = listener;

        awardsMap = new HashMap<>();
        awardsMap.put(Award.ACTIVE_USER.name(), R.drawable.volunteer);
        awardsMap.put(Award.DEVELOPER.name(), R.drawable.developer);
        awardsMap.put(Award.TOP_ORGANIZER.name(),R.drawable.award);
        awardsMap.put(Award.VERIFIED_USER.name(), R.drawable.verified);
    }


    @Override
    public AwardAdapter.AwardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.awards_card, viewGroup, false);
        AwardAdapter.AwardViewHolder awardViewHolder = new AwardAdapter.AwardViewHolder(v);
        v.setOnClickListener(view -> listener.onItemClick(view, awardViewHolder.getLayoutPosition()));
        return awardViewHolder;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(AwardViewHolder viewHolder, int i) {
        viewHolder.imageView.setImageResource(awardsMap.get(items.get(i)));
    }
}

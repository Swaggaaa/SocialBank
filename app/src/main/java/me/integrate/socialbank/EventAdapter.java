package me.integrate.socialbank;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> items;

    private Context context;
    private CustomItemClickListener listener;

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagen;
        private TextView title;
        private TextView initDate;
        private TextView finishDate;
        private TextView location;
        private TextView demand;

            public EventViewHolder(View v) {
                super(v);
                imagen = (ImageView) v.findViewById(R.id.event_photo);
                title = (TextView) v.findViewById(R.id.title_event);
                initDate = (TextView) v.findViewById(R.id.init_date_event);
                finishDate = (TextView) v.findViewById(R.id.finish_date_event);
                location = (TextView) v.findViewById(R.id.place_event);
                demand = (TextView) v.findViewById(R.id.demand);

            }
    }

    EventAdapter(List<Event> items, Context context, CustomItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.events_card, viewGroup, false);
        EventViewHolder eventViewHolder = new EventViewHolder(v);
        v.setOnClickListener(view -> listener.onItemClick(view, eventViewHolder.getPosition()));
        return eventViewHolder;
    }



    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, int i) {

        viewHolder.imagen.setImageBitmap(items.get(i).getImagen());
        viewHolder.title.setText(items.get(i).getTitle());
        viewHolder.initDate.setText(items.get(i).getInitString());
        viewHolder.finishDate.setText(items.get(i).getFinishString());
        viewHolder.location.setText(items.get(i).getLocation());
        viewHolder.demand.setText(String.valueOf(items.get(i).getDemandOrOffer()));


    }
}

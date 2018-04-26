package me.integrate.socialbank;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> items;

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagen;
        private TextView title;
        private TextView hour;
        private TextView date;
        private TextView place;
        private TextView individual;

            public EventViewHolder(View v) {
                super(v);
                imagen = (ImageView) v.findViewById(R.id.event_photo);
                title = (TextView) v.findViewById(R.id.title_event);
                hour = (TextView) v.findViewById(R.id.hour_event);
                date = (TextView) v.findViewById(R.id.date_event);
                place = (TextView) v.findViewById(R.id.place_event);
                individual = (TextView) v.findViewById(R.id.individual_or_group);

            }
    }

    EventAdapter(List<Event> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.events_card, viewGroup, false);
        return new EventViewHolder(v);
    }



    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, int i) {

        viewHolder.imagen.setImageResource(items.get(i).getImagen());
        viewHolder.title.setText(items.get(i).getTitle());
        viewHolder.hour.setText(items.get(i).getHour());
        viewHolder.date.setText(items.get(i).getDate());
        viewHolder.place.setText(items.get(i).getPlace());
        viewHolder.individual.setText(items.get(i).getIndividual());


    }
}

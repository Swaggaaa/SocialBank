package me.integrate.socialbank;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        private TextView hours;

            public EventViewHolder(View v) {
                super(v);
                imagen = (ImageView) v.findViewById(R.id.event_photo);
                title = (TextView) v.findViewById(R.id.title_event);
                initDate = (TextView) v.findViewById(R.id.init_date_event);
                finishDate = (TextView) v.findViewById(R.id.finish_date_event);
                location = (TextView) v.findViewById(R.id.place_event);
                demand = (TextView) v.findViewById(R.id.demand);
                hours = (TextView) v.findViewById(R.id.hours);

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
        v.setOnClickListener(view -> listener.onItemClick(view, eventViewHolder.getLayoutPosition()));
        return eventViewHolder;
    }



    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, int i) {

        viewHolder.imagen.setImageBitmap(items.get(i).getImage());
        viewHolder.title.setText(items.get(i).getTitle());
        viewHolder.initDate.setText(dateToString(items.get(i).getIniDate()));
        viewHolder.finishDate.setText(dateToString(items.get(i).getEndDate()));
        viewHolder.location.setText(items.get(i).getLocation());
        viewHolder.demand.setText(getDemandOrOffer(items.get(i).getDemand()));
        viewHolder.hours.setText(getHours(items.get(i).getIniDate(), items.get(i).getEndDate()));
    }

    public String getHours(Date hourIni, Date hourEnd) {
        if (hourIni != null && hourEnd != null ) {
           long diff = hourEnd.getTime() - hourIni.getTime();
           long seconds = diff/1000;
           long minutes = seconds/60;
           long hours = minutes/60;
           return String.valueOf(hours);
        } else return context.getResources().getString(R.string.notHour);
    }

    public String dateToString(Date date) {
        if (date == null) return context.getResources().getString(R.string.notDate);
        else{
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return df.format(date);
        }
    }

    public String getDemandOrOffer(boolean isDemand) {
        if(isDemand) return context.getResources().getString(R.string.demand);
        else return context.getResources().getString(R.string.offered);
    }
}

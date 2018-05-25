package me.integrate.socialbank;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import org.json.JSONException;

class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> items;

    private Context context;
    private CustomItemClickListener listener;

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        private TextView user;
        private TextView text;
        private Button delete;

        private CommentViewHolder(View v) {
            super(v);
            user = (TextView) v.findViewById(R.id.name_user);
            text = (TextView) v.findViewById(R.id.comments);
            delete = (Button) v.findViewById(R.id.delete_comment_button);
        }
    }

    CommentAdapter(List<Comment> items, Context context, CustomItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comment_card, viewGroup, false);
        CommentViewHolder userViewHolder = new CommentViewHolder(v);
        v.setOnClickListener(view -> listener.onItemClick(view, userViewHolder.getLayoutPosition()));
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder viewHolder, int i) {
        String name = items.get(i).getUser().concat(" ").concat(items.get(i).getSurname());
        viewHolder.user.setText(name);
        viewHolder.text.setText(items.get(i).getComment());
        if (items.get(i).getEmailCreator().equals(SharedPreferencesManager.INSTANCE.read(this.context, "user_email"))){
            viewHolder.delete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

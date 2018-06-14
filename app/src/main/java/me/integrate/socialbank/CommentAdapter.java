package me.integrate.socialbank;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.List;


import static me.integrate.socialbank.App.getContext;

class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> items;

    private static final String URL = "/comments";


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
        viewHolder.user.setText(items.get(i).getUser());
        viewHolder.text.setText(items.get(i).getComment());
        if (!items.get(i).getEmailCreator().equals(SharedPreferencesManager.INSTANCE.read(this.context, "user_email"))) viewHolder.delete.setVisibility(View.GONE);
        else {
            viewHolder.delete.setOnClickListener(v->{
                if (items.get(i).getEmailCreator().equals(SharedPreferencesManager.INSTANCE.read(this.context, "user_email"))) {
                    deletedComment(items.get(i).getId());
                    items.remove(items.get(i));
                    notifyDataSetChanged();
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void deletedComment(int id) {

        APICommunicator apiCommunicator = new APICommunicator();
        Response.Listener responseListener = (Response.Listener<CustomRequest.CustomResponse>) response -> {

            Toast.makeText(getContext().getApplicationContext(), getContext().getResources().getString(R.string.deleted_comment), Toast.LENGTH_LONG).show();

        };
        Response.ErrorListener errorListener = error -> errorTreatment(error.networkResponse.statusCode);

        apiCommunicator.deleteRequest(getContext().getApplicationContext(), URL + '/' + id , responseListener, errorListener, null);
    }

    private void errorTreatment(int errorCode) {
        String message;
        if (errorCode == 401)
            message = getContext().getString(R.string.unauthorized);
        else if (errorCode == 403)
            message = getContext().getString(R.string.forbidden);
        else if (errorCode == 404)
            message = getContext().getString(R.string.not_found);
        else
            message = getContext().getString(R.string.unexpectedError);

        Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}

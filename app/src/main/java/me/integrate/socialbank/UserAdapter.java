package me.integrate.socialbank;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> items;

    private Context context;
    private CustomItemClickListener listener;

    static class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView userEmail;
        private ImageView image;

        private UserViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.user_name);
            userEmail = (TextView) v.findViewById(R.id.userEmail);
            image = (ImageView) v.findViewById(R.id.user_photo);
        }
    }

    UserAdapter(List<User> items, Context context, CustomItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.users_card, viewGroup, false);
        UserViewHolder userViewHolder = new UserViewHolder(v);
        v.setOnClickListener(view -> listener.onItemClick(view, userViewHolder.getLayoutPosition()));
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder viewHolder, int i) {
        String name = items.get(i).getName().concat(" ").concat(items.get(i).getSurname());
        viewHolder.name.setText(name);
        Bitmap image = (items.get(i).getImage());
        if (image == null) image = BitmapFactory.decodeResource(App.getContext().getResources(),R.drawable.user_icon);
        image = getImageRounded(image);
        viewHolder.image.setImageBitmap(image);
        viewHolder.userEmail.setText(items.get(i).getEmail());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private Bitmap getImageRounded(Bitmap image) {
        image = ImageHelper.cropBitmapToSquare(image);
        image = ImageHelper.getRoundedCornerBitmap(image, 120);
        return image;

    }
}

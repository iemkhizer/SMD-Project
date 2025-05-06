package com.example.natterchatapp.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natterchatapp.R;
import com.example.natterchatapp.listeners.UserListener;
import com.example.natterchatapp.models.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final ArrayList<User> users;
    private final UserListener listener;

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    public UserAdapter(ArrayList<User> users, UserListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_container_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.email.setText(users.get(position).getEmail());
        holder.name.setText(users.get(position).getName());
        holder.image.setImageBitmap(getUserImage(users.get(position).getImage()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUserClicked(users.get(position));
            }
        });
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView email, name;
        RoundedImageView image;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.tvEmailUser);
            name = itemView.findViewById(R.id.tvNameUser);
            image = itemView.findViewById(R.id.rivProfileUser);

        }
    }
}

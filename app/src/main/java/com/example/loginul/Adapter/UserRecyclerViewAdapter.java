package com.example.loginul.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.loginul.Model.User;
import com.example.loginul.R;

import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.MyHolder>{
    Context gContext;
    List<User> userList;

    public UserRecyclerViewAdapter(Context gContext, List<User> userList) {
        this.gContext = gContext;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list,parent,false);
        MyHolder viewHolder = new MyHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String userImage = userList.get(position).getProfile_image();
        final String userName = userList.get(position).getFullName();
        String userEmail = userList.get(position).getEmail();


        // set data
        holder.name.setText(userName);
        holder.email.setText(userEmail);
        Glide.with(gContext).asBitmap().load(userImage).into(holder.profile_pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(gContext,""+ userName,Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView profile_pic;
        TextView name,email;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic=itemView.findViewById(R.id.user_list_profileImg);
            name=itemView.findViewById(R.id.user_list_name);
            email=itemView.findViewById(R.id.user_list_email);

        }
    }
}

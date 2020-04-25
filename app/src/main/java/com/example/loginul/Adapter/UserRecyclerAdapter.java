package com.example.loginul.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.loginul.Model.User;
import com.example.loginul.R;
import com.example.loginul.ui.profile.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import org.w3c.dom.Text;

import java.util.List;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {



    private Context gContext;
    private List<User> gUser;

    private FirebaseUser firebaseUser;

    public UserRecyclerAdapter(Context gContext, List<User> gUser) {
        this.gContext = gContext;
        this.gUser = gUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list,parent,false);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        final User user = gUser.get(position);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullName());
        Glide.with(gContext).load(user.getImageUrl()).into(holder.user_img);

        isFollowing(user.getId(),holder.followButton);

        if(user.getId().equals(firebaseUser.getUid())){
            holder.followButton.setVisibility(View.GONE);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = gContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",user.getId());
                editor.apply();
                ((FragmentActivity)gContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.followButton.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return gUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView fullname;
        public ImageView user_img;
        public Button followButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userlist_Username);
            fullname = itemView.findViewById(R.id.userlist_FullName);
            user_img = itemView.findViewById(R.id.searchlist_userImg);
            followButton = itemView.findViewById(R.id.button_follow);
        }
    }
    public void isFollowing(final String userId, final Button button){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userId).exists()){
                    button.setText("following");

                }else{
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

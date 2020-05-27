package com.example.loginul.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.loginul.Model.Chat;
import com.example.loginul.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.MyHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    Context gContext;
    List<Chat> chatList;
    String imageUrl;

    FirebaseUser firebaseUser;

    public ChatRecyclerViewAdapter(Context gContext, List<Chat> chatList, String imageUrl) {
        this.gContext = gContext;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate  layout for receiver and sender
        // for  row_chat_left

        if(viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(gContext).inflate(R.layout.row_chat_right,parent,false);
            MyHolder viewHolder = new MyHolder(view);
            return viewHolder;
        }
        else {
            View view = LayoutInflater.from(gContext).inflate(R.layout.row_chat_left,parent,false);
            MyHolder viewHolder = new MyHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String message  = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();

        // convert time stamp mm/dd/yy

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa" ,cal).toString();

        //set data

        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);
//        try{
//            Glide.with(gContext).load(imageUrl).into(holder.profile_pic);
//
//        }catch (Exception e){
//            Glide.with(gContext).load(R.drawable.ic_home_black_24dp).into(holder.profile_pic);
//
//        }

        // set seen/deliverd status of message

        if(position==chatList.size()-1){

            if (chatList.get(position).isSeen()) {
                holder.isSeenTv.setText("Seen");
            }
            else{
                holder.isSeenTv.setText("Delivered");
            }
        }else {
            holder.isSeenTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // get currently signed in user

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT ;

        }else {

            return  MSG_TYPE_LEFT;

        }

      //  return super.getItemViewType(position);
    }

    class MyHolder extends RecyclerView.ViewHolder {

        // Views
        ImageView profile_pic;
        TextView messageTv,timeTv,isSeenTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

         //   profile_pic=itemView.findViewById(R.id.profile_img_chat_row);
            messageTv=itemView.findViewById(R.id.message_chat_row);
            timeTv=itemView.findViewById(R.id.time_chat_row);
            isSeenTv=itemView.findViewById(R.id.chat_row_seen);
        }
    }
}

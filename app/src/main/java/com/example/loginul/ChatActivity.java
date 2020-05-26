package com.example.loginul;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.loginul.Adapter.ChatRecyclerViewAdapter;
import com.example.loginul.Model.Chat;
import com.example.loginul.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView her_name;
    RecyclerView recyclerView;
    ImageView profile_image;
    EditText messageEt;
    ImageButton sendBtn;

    List<User> userList;

    // for checking user seen message or not
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<Chat> chatList;
    ChatRecyclerViewAdapter recyclerViewAdapter;


    // UID
    String heruid;
    String myuid;

    String herImage;


    // User
    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;

    DatabaseReference userDBRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        // This to remove action bar

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        Window w = getWindow();
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getSupportActionBar().hide();


        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Her Name");

        recyclerView = findViewById(R.id.chat_recyclerView);
        profile_image=findViewById(R.id.chat_profile_img);
        messageEt = findViewById(R.id.chat_message);
        sendBtn = findViewById(R.id.chat_send_imgButton);
        her_name= findViewById(R.id.chat_name);


        // Layout (linearlayout) for recycler view

        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        // Get UserDeatils on


        Intent intent = getIntent();
        heruid = intent.getStringExtra("herId");

        firebaseAuth=FirebaseAuth.getInstance();


        FirebaseUser user = firebaseAuth.getCurrentUser();

        myuid = user.getUid();





//        intent.putExtra("herId");

        // firebase
        firebaseAuth = FirebaseAuth.getInstance() ;

        firebaseDatabase = FirebaseDatabase.getInstance();

        checkUserStatus();



        userDBRef = firebaseDatabase.getReference("Users");


        // search user to get user's info
        Query userQuery = userDBRef.orderByChild("id").equalTo(heruid);

        // Get User Picture And Name
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // check utility required to get the image
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    // get data
                    String name = " " +ds.child("FullName").getValue();
                     herImage = " " +ds.child("profile_image").getValue();

                    //set data

                    her_name.setText(name);

                    try {
                        Glide.with(getApplicationContext()).load(herImage).into(profile_image);
                    }
                    catch (Exception e){
                        Glide.with(getApplicationContext()).load(R.drawable.ic_home_black_24dp).into(profile_image);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get text from edit text
                String message = messageEt.getText().toString().trim();

                if(TextUtils.isEmpty(message)){

                    // text
                    showMessage(" Your text Is Empty ");
                }
                else{
                    // text not empty
                    sendMessage(message);
                }
            }
        });

        readMessage();

        seenMessage();



    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if( chat.getReceiver() != null && chat.getSender() != null && chat.getReceiver().equals(myuid) && chat.getSender().equals(heruid)){
                        HashMap<String,Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen",true);
                        ds.getRef().updateChildren(hasSeenHashMap);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage() {
        chatList = new ArrayList<>() ;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          //      chatList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if(  chat.getReceiver() != null && chat.getSender() != null &&  chat.getReceiver().equals(myuid) && chat.getSender().equals(heruid) ||
                            chat.getReceiver() != null && chat.getSender() != null &&  chat.getReceiver().equals(heruid) && chat.getSender().equals(myuid)){
                        chatList.add(chat);
                    }

                    recyclerViewAdapter = new ChatRecyclerViewAdapter(ChatActivity.this,chatList,herImage);
                    recyclerViewAdapter.notifyDataSetChanged();
                    // set adapter to recyclerView
                    recyclerView.setAdapter(recyclerViewAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String  timestamp = String.valueOf(System.currentTimeMillis());


        HashMap<String, Object> result = new HashMap<>();
        result.put("sender",myuid);
        result.put("receiver",heruid);
        result.put("message",message);
        result.put("timestamp",timestamp);
        result.put("isSeen",false);

        databaseReference.child("Chats").push().setValue(result);

        // reset edit text
        messageEt.setText("");

    }

    private void checkUserStatus() {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            myuid = user.getUid();

        }else{
            startActivity( new Intent(this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
//        checkUserStatus();
        super.onStart();
    }



    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

        // hide the search view
        menu.findItem(R.id.search_menu).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);

    }





}

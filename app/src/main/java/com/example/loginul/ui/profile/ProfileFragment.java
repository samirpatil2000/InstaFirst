package com.example.loginul.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.example.loginul.BottomActivity;
import com.example.loginul.LoginActivity;
import com.example.loginul.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ProfileFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    FirebaseAuth auth;
    FirebaseUser current_user;

    // best method to get user name from database

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;



    private ProfileViewModel homeViewModel;

    TextView profile_username,profile_fullName,profile_email;
    Button btn_editProfle;
    ImageView profile_image;
    Button logOut;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        profile_fullName=root.findViewById(R.id.profile_name);
        profile_image=root.findViewById(R.id.profile_pic);
        profile_email=root.findViewById(R.id.profile_email);


//        profile_fullName=root.findViewById(R.id.profile_fullname);
//        btn_editProfle=root.findViewById(R.id.btn_edit_profile);
        logOut=root.findViewById(R.id.logOut);


        auth=FirebaseAuth.getInstance();
        current_user=auth.getCurrentUser();

        //
//        profile_username.setText(current_user.getEmail());
//        profile_fullName.setText(current_user.getDisplayName());


        // init firebase
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");

        Query query = databaseReference.orderByChild("email").equalTo(current_user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // check until required data get
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    // get data
                    String fullname= "" + ds.child("FullName").getValue();
                    String username= "" + ds.child("username").getValue();
                    String bio= "" + ds.child("bio").getValue();
                    String image= "" + ds.child("imageUrl").getValue();
                    String email= "" +ds.child("email").getValue();



                    profile_fullName.setText(fullname);
                    profile_email.setText(email);

                    Glide.with(getActivity()).load(current_user.getPhotoUrl()).into(profile_image);


//                    profile_username.setText(current_user.getEmail());
//                    profile_fullName.setText(fullname);

//                    try {
//                        Glide.with(getActivity()).load(image).into(profile_image);
//                    }
//                    catch (Exception e){
//                        Glide.with(getActivity()).load(R.drawable.ic_home_black_24dp).into(profile_image);
//                    }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.getInstance().signOut();

                FirebaseAuth.getInstance().signOut();
                Intent i1 = new Intent(getActivity(), LoginActivity.class);
                startActivity(i1);
                Toast.makeText(getActivity(), "Logout Successfully!", Toast.LENGTH_SHORT).show();


            }
        });







        return root;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
package com.example.loginul.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.example.loginul.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser current_user;


    private ProfileViewModel homeViewModel;

    TextView profile_username,profile_fullName;
    Button btn_editProfle;
    ImageView profile_image;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        profile_username=root.findViewById(R.id.profile_username);
        profile_fullName=root.findViewById(R.id.profile_fullname);
        profile_image=root.findViewById(R.id.profile_image);
        btn_editProfle=root.findViewById(R.id.btn_edit_profile);

        auth=FirebaseAuth.getInstance();
        current_user=auth.getCurrentUser();

        //
        profile_username.setText(current_user.getEmail());
        profile_fullName.setText(current_user.getDisplayName());
        Glide.with(this).load(current_user.getPhotoUrl()).into(profile_image);


//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });






        return root;
    }
}
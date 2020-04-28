package com.example.loginul.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginul.Adapter.UserRecyclerViewAdapter;
import com.example.loginul.Model.User;
import com.example.loginul.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchViewModel dashboardViewModel;
    RecyclerView recyclerView;
    UserRecyclerViewAdapter adapter;

    private List<User> gUser;

    DatabaseReference reference;

    EditText search_bar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        recyclerView=root.findViewById(R.id.recycler_view_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        search_bar= root.findViewById(R.id.search_bar);



        reference = FirebaseDatabase.getInstance().getReference("Users");
//        gUser = new ArrayList<>();
        gUser = new ArrayList<>();
//
        adapter=new UserRecyclerViewAdapter(getActivity(), gUser);
        recyclerView.setAdapter(adapter);
        getAllUsers();

        return root;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                gUser = new ArrayList<>();
//                for(DataSnapshot ds:dataSnapshot.getChildren()){
//                    User userModel = ds.getValue(User.class);
//                    gUser.add(userModel);
//
//                }
//
//                adapter=new UserRecyclerViewAdapter(getActivity(), gUser);
//                recyclerView.setAdapter(adapter);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    private void getAllUsers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // get path of database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        // get all data from path

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                gUser.clear();
                for(DataSnapshot dataSnap:dataSnapshot.getChildren()){
                    User user = dataSnap.getValue(User.class);


                    // get all user expect current User
//                    if(!user.getUid().equals(firebaseUser.getUid())) {
                        gUser.add(user);
//                    }
                }
                adapter=new UserRecyclerViewAdapter(getActivity(), gUser);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
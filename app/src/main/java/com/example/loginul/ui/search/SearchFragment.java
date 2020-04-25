package com.example.loginul.ui.search;

import android.app.DownloadManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginul.Adapter.UserRecyclerAdapter;
import com.example.loginul.Model.User;
import com.example.loginul.R;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class SearchFragment extends Fragment {

    private SearchViewModel dashboardViewModel;
    RecyclerView recyclerView;
    private UserRecyclerAdapter userRecyclerAdapter;
    private List<User> gUser;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_bar= root.findViewById(R.id.search_bar);

        gUser = new ArrayList<>();
        userRecyclerAdapter= new UserRecyclerAdapter(getContext(),gUser);
        recyclerView.setAdapter(userRecyclerAdapter);

        readUsers();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return root;
    }
    private void searchUser(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gUser.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    gUser.add(user);


                }
                userRecyclerAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(search_bar.getText().toString().equals("")){
                    gUser.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        gUser.add(user);
                    }
                    userRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
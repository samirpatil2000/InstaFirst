package com.example.loginul.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginul.Adapter.UserRecyclerViewAdapter;
import com.example.loginul.LoginActivity;
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

//    EditText search_bar;

    // SearchView
    SearchView searchView;
    FirebaseAuth firebaseAuth ;

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
//        search_bar= root.findViewById(R.id.search_bar);

        firebaseAuth=FirebaseAuth.getInstance();



        reference = FirebaseDatabase.getInstance().getReference("Users");
//        gUser = new ArrayList<>();
        gUser = new ArrayList<>();
//
        adapter=new UserRecyclerViewAdapter(getActivity(), gUser);
        recyclerView.setAdapter(adapter);

        // Searching The User
//        searchView= search_ba


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
                    if(!user.getId().equals(firebaseUser.getUid())) {
                        gUser.add(user);
                    }
                }
                adapter=new UserRecyclerViewAdapter(getActivity(), gUser);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // On Search View

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }



    /* inflate menu option */

    private void checkUserStatus(){
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user != null){

        }
        else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);

        MenuItem item = menu.findItem(R.id.search_menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // it will call the keyboard when user press on search bar
                // if search query is not empty then search
                if (!TextUtils.isEmpty(query.trim())){
                    // search text cointains text , search it
                    searchUsers(query);

                }
                else {
                    // search text empty get all users
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText.trim())){
                    // search text cointains text , search it
                    searchUsers(newText);

                }
                else {
                    // search text empty get all users
                }
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);

    }

    private void searchUsers(final String query) {


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // get path of database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        // get all data from path

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gUser.clear();
                for(DataSnapshot dataSnap:dataSnapshot.getChildren()){
                    User user = dataSnap.getValue(User.class);

                    // add Only Search result User
                    if(user.getFullName().toLowerCase().contains(query.toLowerCase())
                            || user.getEmail().toLowerCase().contains(query.toLowerCase())) {

                        gUser.add(user);
                    }
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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.search_menu){
            checkUserStatus();


        }
        return super.onOptionsItemSelected(item);
    }
}
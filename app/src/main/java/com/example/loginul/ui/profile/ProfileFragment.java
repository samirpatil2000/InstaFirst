package com.example.loginul.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.example.loginul.LoginActivity;
import com.example.loginul.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.Key;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class ProfileFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    FirebaseAuth auth;
    FirebaseUser current_user;
    ProgressDialog pd;

    // best method to get user name from database

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    // Permission Constant
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int PICK_IMAGE1=1;
    private static final int PICK_IMAGE2=1;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    int PReqCode = 1 ;
    Uri imageUri;

    // for of picked image ;
    String profileOrCoverPhoto ;

    // Store the images in Fire Base
    StorageReference storageReference;
    // path where images of user profile image store
    String storagePath = "Users_Profile_Cover_Imgs/";







    private ProfileViewModel homeViewModel;

    TextView profile_username,profile_fullName,profile_email,profile_bio;
    Button btn_editProfle;
    ImageView profile_image,profile_coverImg;
    Button logOut , EditProfile;
    FloatingActionButton fab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        profile_fullName=root.findViewById(R.id.profile_name);
        profile_image=root.findViewById(R.id.profile_pic);
        profile_email=root.findViewById(R.id.profile_email);
        profile_bio=root.findViewById(R.id.profile_bio);
        profile_coverImg=root.findViewById(R.id.profile_coverImg);

        fab=root.findViewById(R.id.fab_profile_edit);

        pd= new ProgressDialog(getActivity());


//        profile_fullName=root.findViewById(R.id.profile_fullname);
//        btn_editProfle=root.findViewById(R.id.btn_edit_profile);
        logOut=root.findViewById(R.id.logOut);
//        EditProfile= root.findViewById(R.id.editProfile);



        auth=FirebaseAuth.getInstance();
        current_user=auth.getCurrentUser();



        //
//        profile_username.setText(current_user.getEmail());
//        profile_fullName.setText(current_user.getDisplayName());


        // init firebase
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference();


        Query query = databaseReference.orderByChild("email").equalTo(current_user.getEmail());

        // get the the user data from data
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // check until required data get
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    // get data
                    String fullname= "" + ds.child("FullName").getValue();
                    String username= "" + ds.child("username").getValue();
                    String bio= "" + ds.child("bio").getValue();
                    String image= "" + ds.child("profile_image").getValue();
                    String email= "" +ds.child("email").getValue();
                    String coverPhoto="" +ds.child("coverImage").getValue();



                    profile_fullName.setText(fullname);
                    profile_email.setText(email);
                    profile_bio.setText(bio);
//
//                    Glide.with(getActivity()).load(current_user.getPhotoUrl()).into(profile_image);


//                    profile_username.setText(current_user.getEmail());
//                    profile_fullName.setText(fullname);

                    // Profile Img
                    try {
                        Glide.with(getActivity()).load(image).into(profile_image);
                    }
                    catch (Exception e){
                        Glide.with(getActivity()).load(R.drawable.ic_home_black_24dp).into(profile_image);
                    }

                    // Cover Image
//                    try {
//                        Glide.with(getActivity()).load(coverPhoto).into(profile_coverImg);
//                    }
//                    catch (Exception e){
//                        Glide.with(getActivity()).load(R.drawable.ic_home_black_24dp).into(profile_coverImg);
//                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileEditDialog();
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
                Toast.makeText(getActivity(), "Logout Successfully!", Toast.LENGTH_SHORT).show(); }
        });







        return root;
    }

    private void showProfileEditDialog() {
        String options[] = {"Edit Profile Picture" ,"Edit Cover Photo","Edit Name","Edit Bio"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which ==0){
                    // Edit Profile Pic
                    pd.setMessage("Updating Profile Pic");
//                    pd.show();
                    profileOrCoverPhoto="profile_image";
                    showImagePicDialog();
                }
                else if (which == 1){
                    // Edit cover photo
                    showImagePicDialog();
                    profileOrCoverPhoto="cover_image";
                    pd.setMessage("Updating Cover Image");
//                    pd.show();
                }
                else if( which ==2){
                    // Edit name
                    pd.setMessage("Updating Name");
                    showNameUpdateDialog("FullName");
//                    pd.show();
                }
                else if (which == 3){
                    // edit bio
                    pd.setMessage("Updating Bio");
                    showNameUpdateDialog("bio");
//                    pd.show();
                }

            }
        });

        // create  and Show dialog
        builder.create().show();

    }

    private void showNameUpdateDialog(final String key) {
        //Custom Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+key );
        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);


        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter "+key); // Hint can be  Edit name or Edit phone
        linearLayout.addView(editText);
        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // input text from edit text
                String value = editText.getText().toString().trim();
                if(!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String,Object> result = new HashMap<>();
                    result.put(key,value);

                    databaseReference.child(current_user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    showMessage("Updated...");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            showMessage(e.getMessage());
                        }
                    });

                }else{
                    Toast.makeText(getActivity(),"Enter "+key,Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void showImagePicDialog() {
        String options[] = {"Camera" ,"Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(" Pick Image From ");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which ==0){
                    // Clicked On Camera
                    checkAndRequestForPermissionForCamera();


                }
                else if (which == 1){
                    // Clicked On Gallery
                    checkAndRequestForPermissionForGallery();
                }

            }
        });

        // create  and Show dialog
        builder.create().show();

    }
    private void checkAndRequestForPermissionForCamera() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.CAMERA)){
                Toast.makeText(getActivity(),"Please accept for required permission",Toast.LENGTH_LONG).show();
            }
            else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},PReqCode);
            }
        }else{
            // If every thing goes allRight the App has permission to access user camera
            openCamera();
        }

    }

    private void checkAndRequestForPermissionForGallery(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(getActivity(),"Please accept for required permission",Toast.LENGTH_LONG).show();
            }
            else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }else{
            // If every thing goes allRight the App has permission to access user gallery
            openGallery();
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE2);
    }
    private void openCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap cameraBitmap = (Bitmap) extras.get("data");
//            if (profileOrCoverPhoto == "profile_image") {
                profile_image.setImageBitmap(cameraBitmap);

//            }else{
//                profile_coverImg.setImageBitmap(cameraBitmap);
//
//            }


        }else if(requestCode==PICK_IMAGE2 && resultCode == RESULT_OK){
            imageUri = data.getData();
//            if (profileOrCoverPhoto == "profile_image") {
                profile_image.setImageURI(imageUri);
            uploadPhotoWithCamera(imageUri);

//            }else{
//                profile_coverImg.setImageURI(imageUri);
//            }
        }
    }

    private void uploadPhotoWithCamera(Uri imageUri) {
        ///show Progress Dialog
        String filePathAndName= storagePath+""+profileOrCoverPhoto+"_"+current_user.getUid();

        StorageReference storageReference2= storageReference.child(filePathAndName);
        storageReference2.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // image is uploaded to storage now get its url and store in user's database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        // check if image is uploaded
                        if(uriTask.isSuccessful()){
                            // image uploaded

                            // and Update url in users database
                            HashMap<String,Object> results = new HashMap<>();
                            results.put(profileOrCoverPhoto,downloadUri.toString());

                            databaseReference.child(current_user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(),"Image Updated ...",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Failed ",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            // error
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Some Error Occured",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }


    public  void showMessage(String s){
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

}
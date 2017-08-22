package com.prisrio.prisrio;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.prisrio.prisrio.mainmenu.REQUEST_IMAGE_CAPTURE;


public class postphoto extends Fragment {

    ImageView img_snapppicture;
    View postPhotoview;

    //FIREBASE AUTHENTICATION
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //FIREBASE
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference databaseRef = database.getReference("foodcategory");


    public postphoto() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ((mainmenu)getActivity()).getCurrentLocation();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LayoutInflater lf = getActivity().getLayoutInflater();
        postPhotoview =  lf.inflate(R.layout.fragment_postphoto, container, false);
        img_snapppicture = (ImageView) postPhotoview.findViewById(R.id.img_postphoto_snappicture);
        TextView textView = (TextView) postPhotoview.findViewById(R.id.lb_name);
        Button bn_post = (Button) postPhotoview.findViewById(R.id.bn_postphoto_post);
        bn_post.setEnabled(false);

        return postPhotoview;

    }

    public void showData(DataSnapshot dataSnapshot){
        ArrayList<String> foodCategoryArr = new ArrayList<>();
        ArrayList<String> tempArr = new ArrayList<>();
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Log.d("Tag","Key : " + ds.getKey());
            //Get user map
            foodCategoryArr.add(ds.getKey());
            //foodCategoryArr.add((String)foodCategory.get("type"));
        }

        Spinner spinner = (Spinner) postPhotoview.findViewById(R.id.ddl_postphoto_foodcategory);
        // Create an ArrayAdapter using the string array and a default spinner layout


        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),   android.R.layout.simple_spinner_item, foodCategoryArr);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



}

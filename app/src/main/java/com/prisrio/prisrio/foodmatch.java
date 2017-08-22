package com.prisrio.prisrio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class foodmatch extends Fragment {

    private Context context;
    private OnFragmentInteractionListener mListener;
    public static ArrayList<Photo> friendsPhotoArr = new ArrayList<Photo>();
    //FIREBASE
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference databasePhoto = FirebaseDatabase.getInstance().getReference("photos");


    //FIREBASE AUTHENTICATION
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public foodmatch() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        final View view =  lf.inflate(R.layout.fragment_foodmatch, container, false);


        // Inflate the layout for this fragment

        Query query = databasePhoto.orderByChild("author");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    ImageView icon = (ImageView) view.findViewById(R.id.img_foodmatch_photo);
                    ImageView img_profile = (ImageView) view.findViewById(R.id.img_foodmatch_profile);
                    TextView lb_author = (TextView) view.findViewById(R.id.lb_foodmatch_author);
                    TextView lb_location = (TextView) view.findViewById(R.id.lb_foodmatch_location);
                    TextView lb_caption = (TextView) view.findViewById(R.id.lb_foodmatch_caption);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        String author = (String) ds.child("author").getValue();
                        String downloadURL = (String) ds.child("downloadURL").getValue();
                        String caption = (String) ds.child("caption").getValue();
                        String address = (String) ds.child("address").getValue();
                        String foodCategory = (String) ds.child("foodCategory").getValue();
                        Double locationLatitude =  (Double)ds.child("latitude").getValue();
                        Double locationLongitude = (Double)ds.child("longitude").getValue();

                        for(int i =0; i<login.fbFriendsArr.size();i++) {
                            //true if friends of users
                            String friendsFBId = login.fbFriendsArr.get(i).fbID;
                            Log.d("Tag","FoodMatch : friend" + friendsFBId);
                            Log.d("Tag","FoodMatch : author" + author);
                            if (friendsFBId.equals(author)) {
                                try {
                                    String fbProfileImage = new URL("https://graph.facebook.com/" + friendsFBId + "/picture?width=500&height=500").toString();
                                    lb_author.setText(login.fbFriendsArr.get(i).name);
                                    Glide
                                            .with(getActivity().getApplicationContext())
                                            .load(fbProfileImage)
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(img_profile);
                                    lb_caption.setText(caption);
                                    lb_location.setText(address);
                                    Glide
                                            .with(getActivity().getApplicationContext())
                                            .load(downloadURL)
                                            .centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(icon);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }

                                Photo photo = new Photo(foodCategory, caption, author, address,locationLatitude, locationLongitude,downloadURL);
                                friendsPhotoArr.add(photo);
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}

package com.prisrio.prisrio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;


public class profile extends Fragment {

    CallbackManager callbackManager;
    LoginManager loginManager;

    AccessToken accessToken;

    //FIREBASE
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference databasePhoto = FirebaseDatabase.getInstance().getReference("photos");


    //FIREBASE AUTHENTICATION
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        final View view =  lf.inflate(R.layout.fragment_profile, container, false);
        TextView textView = (TextView) view.findViewById(R.id.lb_name); //


        String fbName = login.FB_NAME;
        String fbID = login.FB_ID;
        String fbProfileImage = login.FB_PROFILEIMAGE;

        ImageView imageView = (ImageView) view.findViewById(R.id.img_profile);

        // show The Image in a ImageView
        new DownloadImageTask(imageView)
                .execute(fbProfileImage);

        Typeface ralewayFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RalewayRegular.ttf");
        //textView.setTypeface(ralewayFont);
        //requires API 5.0
        textView.setLetterSpacing(1.2f);
        textView.setText(fbName);

        final ArrayList<String> photosDownloadArr = new ArrayList();
        final ArrayList<Photo> photoArr = new ArrayList();
        /*
        int lettersIcon[] = {
                R.drawable.appbarlogo, R.drawable.applogo2
        };
        */
        Query query = databasePhoto.orderByChild("author").equalTo(login.FB_ID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        // do something with the individual
                        String downloadURL = (String) ds.child("downloadURL").getValue();
                        String caption = (String) ds.child("caption").getValue();
                        String address = (String) ds.child("address").getValue();
                        String foodCategory = (String) ds.child("foodCategory").getValue();
                        String author = (String) ds.child("author").getValue();
                        Double locationLatitude =  (Double)ds.child("latitude").getValue();
                        Double locationLongitude = (Double)ds.child("longitude").getValue();
                        Log.d("Tag","downloadURL test2: "+downloadURL);
                        Photo photo = new Photo(foodCategory, caption, author, address,locationLatitude, locationLongitude,downloadURL);
                        photoArr.add(photo);
                        photosDownloadArr.add(downloadURL);
                    }
                    GridView gridView = (GridView) view.findViewById(R.id.gv_profile_imagegallery);
                    gridView.setAdapter(new profileGridViewAdapter(getActivity(), photosDownloadArr));
                    //profileGridViewAdapter adapter = new profileGridViewAdapter(getActivity(), lettersIcon);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(getActivity(), photoArr.get(position).address, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //photosDownloadArr.add("https://firebasestorage.googleapis.com/v0/b/prisrio-5d247.appspot.com/o/f5e7bb9a-6998-45cd-a924-dfaeb3f69d59.jpg?alt=media&token=3b3d1433-1c6c-4b84-9ac4-2d86ad369e08");
        //photosDownloadArr.add("https://firebasestorage.googleapis.com/v0/b/prisrio-5d247.appspot.com/o/f5e7bb9a-6998-45cd-a924-dfaeb3f69d59.jpg?alt=media&token=3b3d1433-1c6c-4b84-9ac4-2d86ad369e08");
        //photosDownloadArr.add("https://firebasestorage.googleapis.com/v0/b/prisrio-5d247.appspot.com/o/a83e35d5-f7aa-4795-8706-867b3124f2b6.jpg?alt=media&token=b21959d0-d19b-447c-9bba-58b8feb7db36");
        //photosDownloadArr.add("https://firebasestorage.googleapis.com/v0/b/prisrio-5d247.appspot.com/o/57df65d5-9bd1-4082-92b5-bbff2df89270.jpg?alt=media&token=c602a566-596d-42a0-8896-03909fc78b40");
        //photosDownloadArr.add("https://firebasestorage.googleapis.com/v0/b/prisrio-5d247.appspot.com/o/590c9b14-2e5f-4de9-9ce9-05b5289eee73.jpg?alt=media&token=6ef0a906-855e-4837-ac62-4bb2b9c60a63");

        // Inflate the layout for this fragment
        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

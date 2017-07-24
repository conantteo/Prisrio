package com.prisrio.prisrio;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Typeface;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.jar.*;

public class mainmenu extends AppCompatActivity {


    private ImageView imageView;

    // Create a storage reference from our app
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    CallbackManager callbackManager;
    LoginManager loginManager;

    AccessToken accessToken;

    //LOCATION STUFF
    private LocationManager locationManager;
    private LocationListener locationListener;


    //FIREBASE AUTHENTICATION
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Double locationLongitude=0.0;
    private Double locationLatitude=0.0;

    //FIREBASE
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference databaseRef = database.getReference("foodcategory");
    DatabaseReference databasePhoto = FirebaseDatabase.getInstance().getReference("photos");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        mAuth = FirebaseAuth.getInstance();
        //To get access token from application
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        //accessToken = AccessToken.getCurrentAccessToken();

        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //LOAD DEFAULT FRAGMENT
        Fragment fragment = new foodmatch();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment, fragment);
        ft.commit();

        //Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_home: {
                        //Toast.makeText(mainmenu.this, "Action Add Clicked", Toast.LENGTH_SHORT).show();
                        //Intent main = new Intent(mainmenu.this,postphoto.class);
                        //startActivity(main);

                        Fragment fragment = new foodmatch();
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.fragment, fragment);
                        ft.commit();
                        //getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        break;
                    }
                    case R.id.bottom_navigation_photo: {
                        //Toast.makeText(mainmenu.this, "Action Add Clicked", Toast.LENGTH_SHORT).show();
                        //Intent main = new Intent(mainmenu.this,postphoto.class);
                        //startActivity(main);

                        Fragment fragment = new postphoto();
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.fragment, fragment);
                        ft.commit();
                        break;
                    }
                    case R.id.bottom_navigation_profile: {
                        //Toast.makeText(mainmenu.this, "Action Add Clicked", Toast.LENGTH_SHORT).show();
                        //Intent main = new Intent(mainmenu.this,postphoto.class);
                        //startActivity(main);

                        Fragment fragment = new profile();
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.fragment, fragment);
                        ft.commit();
                        break;
                    }
                }
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreyishBlack));
        }


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        //String fbName = intent.getStringExtra(login.FB_NAME);
        // fbID = intent.getStringExtra(login.FB_ID);
        //String fbProfileImage = intent.getStringExtra(login.FB_PROFILEIMAGE);

        /*
        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.lb_name);
        Typeface ralewayFont = Typeface.createFromAsset(getAssets(), "fonts/RalewayRegular.ttf");
        textView.setTypeface(ralewayFont);
        //requires API 5.0
        textView.setLetterSpacing(1.2f);
        textView.setText(fbName);




        imageView = (ImageView) findViewById(R.id.img_profile);

        // show The Image in a ImageView
        new DownloadImageTask(imageView)
                .execute(fbProfileImage);
                */

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Tag","LOCATION : LAT" + location.getLatitude());
                Log.d("Tag","LOCATION : LONG" + location.getLongitude());
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                Log.d("Tag","LOCATION : country" + country);
                Log.d("Tag","LOCATION : address" + address);
                Log.d("Tag","LOCATION : knownName" + knownName);
                TextView lb_location = (TextView) findViewById(R.id.lb_postphoto_location);
                if(lb_location!=null) {
                    lb_location.setText(address);
                    locationLongitude= longitude ;
                    locationLatitude=latitude ;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }
    public void getCurrentLocation (){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
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

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public Uri imageUri;

    public void takePicture(View view) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, 1);
        }else{
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                //String filename = Environment.getExternalStorageDirectory().getPath() + "/folder/testfile.jpg";
                // Uri imageUri = Uri.fromFile(new File(filename));

                // start default camera
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult (cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    public void goToCamera(View view) {
        Intent main = new Intent(mainmenu.this,postphoto.class);
        startActivity(main);
        /*
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, 1);
        }else{
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                String filename = Environment.getExternalStorageDirectory().getPath() + "/folder/testfile.jpg";
                 imageUri = Uri.fromFile(new File(filename));

                // start default camera
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult (cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView img_snapppicture = (ImageView) findViewById(R.id.img_postphoto_snappicture); //
            img_snapppicture.setImageBitmap(imageBitmap);
            //imageView.setImageURI(imageUri);




        }

    }

    /*
    public void createToolbar (){
        // Get the ActionBar
        ActionBar ab = getSupportActionBar();
        // Set the ActionBar background color
        // ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7FC0C0C0")));

        // Create a TextView programmatically.
        TextView tv = new TextView(getApplicationContext());
        ImageView imageView = new ImageView(this);
        int id = getResources().getIdentifier("appbarlogo", "drawable", getPackageName());
        imageView.setImageResource(id);

        // Set text to display in TextView
        // This will set the ActionBar title text
        tv.setText("PRISRIO");

        // Set the text color of TextView
        // This will change the ActionBar title text color
        tv.setTextColor(Color.parseColor("#212121"));

        // Center align the ActionBar title
        tv.setGravity(Gravity.CENTER);

        // Set the serif font for TextView text
        // This will change ActionBar title text font
        Typeface ralewayFont = Typeface.createFromAsset(getAssets(), "fonts/RalewayRegular.ttf");
        tv.setTypeface(ralewayFont);
        tv.setLetterSpacing(1.2f);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        //TextView customView = (TextView) LayoutInflater.from(this).inflate(R.layout.actionbar_custom_title_view_centered,null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);

        // Finally, set the newly created TextView as ActionBar custom view
        ab.setCustomView(imageView,params);
    }
    */

    public void logout(View view){
        LoginManager.getInstance().logOut();
        Intent main = new Intent(mainmenu.this,login.class);
        startActivity(main);
        finish();
    }

    public void postPhoto(View view){

        //Get information from form
        EditText tb_postphoto_caption = (EditText) findViewById(R.id.tb_postphoto_caption);
        final String caption = tb_postphoto_caption.getText().toString();

        TextView lb_postphoto_location = (TextView) findViewById(R.id.lb_postphoto_location);
        final String address = lb_postphoto_location.getText().toString();

        Spinner ddl_postphoto_foodcategory = (Spinner) findViewById(R.id.ddl_postphoto_foodcategory);
        final String foodCategory = ddl_postphoto_foodcategory.getSelectedItem().toString();



        ImageView img_snapppicture = (ImageView) findViewById(R.id.img_postphoto_snappicture); //
        // Get the data from an ImageView as bytes
        img_snapppicture.setDrawingCacheEnabled(true);
        img_snapppicture.buildDrawingCache();
        Bitmap bitmap = img_snapppicture.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] dataBtye = baos.toByteArray();


        //
        // Create a reference to "mountains.jpg"
        // Create a random filename
        final String fileName = UUID.randomUUID().toString();
        StorageReference mountainsRef = storageRef.child(fileName+".jpg");

        UploadTask uploadTask = mountainsRef.putBytes(dataBtye);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //post photo

                //Insert into Database
                Photo photo = new Photo(foodCategory, caption, login.FB_ID,address,locationLatitude,locationLongitude,downloadUrl.toString());
                databasePhoto.child(fileName).setValue(photo);

                Toast.makeText(mainmenu.this, "Your post is uploaded!", Toast.LENGTH_SHORT).show();
            }
        });
    }




}

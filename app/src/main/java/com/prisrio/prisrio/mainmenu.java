package com.prisrio.prisrio;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Typeface;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class mainmenu extends AppCompatActivity {


    private ImageView imageView;

    // Create a storage reference from our app
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String fbName = intent.getStringExtra(login.FB_NAME);
        String fbID = intent.getStringExtra(login.FB_ID);
        String fbProfileImage = intent.getStringExtra(login.FB_PROFILEIMAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.lb_name);
        Typeface ralewayFont = Typeface.createFromAsset(getAssets(), "fonts/RalewayRegular.ttf");
        textView.setTypeface(ralewayFont);
        //requires API 5.0
        textView.setLetterSpacing(1.2f);
        textView.setText(fbName);

        // Get the ActionBar
        ActionBar ab = getSupportActionBar();

        // Set the ActionBar background color
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7FC0C0C0")));

        // Create a TextView programmatically.
        TextView tv = new TextView(getApplicationContext());



        // Set text to display in TextView
        // This will set the ActionBar title text
        tv.setText("PRISRIO");

        // Set the text color of TextView
        // This will change the ActionBar title text color
        tv.setTextColor(Color.parseColor("#FFF5EE"));

        // Center align the ActionBar title
        tv.setGravity(Gravity.CENTER);

        // Set the serif font for TextView text
        // This will change ActionBar title text font
        tv.setTypeface(ralewayFont);
        tv.setLetterSpacing(1.2f);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        //TextView customView = (TextView) LayoutInflater.from(this).inflate(R.layout.actionbar_custom_title_view_centered,null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);

        // Finally, set the newly created TextView as ActionBar custom view
        ab.setCustomView(tv,params);


        imageView = (ImageView) findViewById(R.id.img_profile);

        // show The Image in a ImageView
        new DownloadImageTask(imageView)
                .execute(fbProfileImage);
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
    private void dispatchTakePictureIntent() {

    }

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    public void goToCamera(View view) {
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
                /*
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                        imageUri);
                */
                startActivityForResult (cameraIntent, REQUEST_IMAGE_CAPTURE);
                //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            //imageView.setImageURI(imageUri);

            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = imageView.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBtye = baos.toByteArray();


            //
            // Create a reference to "mountains.jpg"
            // Create a random filename
            String fileName = UUID.randomUUID().toString();
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
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
        }
    }
}

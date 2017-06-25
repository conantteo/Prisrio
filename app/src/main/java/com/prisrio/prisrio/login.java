package com.prisrio.prisrio;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class login extends AppCompatActivity {
    CallbackManager callbackManager;
    AccessToken accessToken;

   // private CallbackManager callbackManager;
    //private AccessTokenTracker accessTokenTracker;
    //private ProfileTracker profileTracker;
    private LoginButton loginButton;
    private String id,firstName,lastName, email,birthday,gender;
    private URL profilePicture;
    private String userId;
    private String TAG = "LoginActivity";

    //FACEBOOK USER DETAILS
    public static final String FB_NAME = "name";
    public static final String FB_GENDER = "gender";
    public static final String FB_ID = "id";
    public static final String FB_PROFILEIMAGE = "profileimage";

    //FIREBASE
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

    //FIREBASE AUTHENTICATION
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private VideoView videoView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set video background
        videoView = (VideoView) findViewById(R.id.vv_loginVideo);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.loginvideo);

        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                videoView.start();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // Capture the layout's TextView and set the string as its text

        TextView lb_description1 = (TextView) findViewById(R.id.lb_login_description);
        TextView lb_description2 = (TextView) findViewById(R.id.lb_login_description2);
        Typeface ralewayFont = Typeface.createFromAsset(getAssets(), "fonts/RalewayRegular.ttf");
        lb_description1.setTypeface(ralewayFont);
        lb_description2.setTypeface(ralewayFont);
        //requires API 5.0
        lb_description1.setLetterSpacing(1f);
        //lb_description2.setLetterSpacing(1f);



        //To get access token from application
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        //accessToken = AccessToken.getCurrentAccessToken();

        callbackManager = CallbackManager.Factory.create();

        LoginButton authButton = (LoginButton)this.findViewById(R.id.login_button);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        //this following method is to the keyhash for facebook app
        /*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.prisrio.prisrio",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        */



       // authButton.setReadPermissions(Arrays.asList("user_status","user_friends","email"));
        authButton.setReadPermissions("email", "public_profile");
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));

        authButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            handleFacebookAccessToken(loginResult.getAccessToken());
                            facebookGraph();
                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                        }
                    });
        if(isLoggedIn()==true){
            facebookGraph();
        }
    }

    public void facebookGraph(){
        accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                // Application code
                try {
                    userId = object.getString("id");
                    profilePicture = new URL("https://graph.facebook.com/" + userId + "/picture?width=500&height=500");
                    if (object.has("id"))
                        id = object.getString("id");
                    if(object.has("first_name"))
                        firstName = object.getString("first_name");
                    if(object.has("last_name"))
                        lastName = object.getString("last_name");
                    if (object.has("email"))
                        email = object.getString("email");
                    if (object.has("birthday"))
                        birthday = object.getString("birthday");
                    if (object.has("gender"))
                        gender = object.getString("gender");

                    Intent main = new Intent(login.this,mainmenu.class);
                    main.putExtra(FB_NAME, firstName);
                    main.putExtra(FB_GENDER, gender);
                    main.putExtra(FB_ID, id);
                    main.putExtra(FB_PROFILEIMAGE, profilePicture.toString());

                    //main.putExtra("name",firstName);
                    //main.putExtra("surname",lastName);
                    // main.putExtra("imageUrl",profilePicture.toString());


                    // creating user object
                    //User user = new User(firstName);

                    // pushing user to 'users' node using the userId
                    //mDatabase.child(userId).setValue(user);


                    mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                // use "username" already exists
                                // Let the user know he needs to pick another username
                                mDatabase.child(userId).child("name").setValue(firstName);

                            } else {
                                // User does not exist. NOW call createUserWithEmailAndPassword
                                mDatabase.child(userId).setValue(new User(firstName));

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Log.e(TAG,response.toString());
                    Log.e(TAG,id);

                    startActivity(main);

                    //This remove the logout button for now
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        //Here we put the requested fields to be returned from the JSONObject
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, birthday, gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //supdateUI(currentUser);
    }

    // [END auth_with_facebook]
    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        //updateUI(null);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    public void goMainMenu(){
        Intent intent = new Intent(this, mainmenu.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

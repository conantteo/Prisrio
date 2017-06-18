package com.prisrio.prisrio;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //To get access token from application
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        accessToken = AccessToken.getCurrentAccessToken();

        callbackManager = CallbackManager.Factory.create();

        LoginButton authButton = (LoginButton)this.findViewById(R.id.login_button);
        authButton.setReadPermissions(Arrays.asList("user_status","user_friends"));


        
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

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
                                    User user = new User(firstName);

                                    // pushing user to 'users' node using the userId
                                    mDatabase.child(userId).setValue(user);


                                    Log.e(TAG,response.toString());
                                    Log.e(TAG,id);


                                    startActivity(main);
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

                        //goMainMenu();
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
        }*/
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

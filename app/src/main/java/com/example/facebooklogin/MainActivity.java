 package com.example.facebooklogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

 public class MainActivity extends AppCompatActivity {




     private LoginButton loginButton;
     private CircleImageView circleImageView;
     private TextView txtName,txtEmail;

     private CallbackManager callbackManager;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         loginButton = findViewById(R.id.login_button);
         txtName = findViewById(R.id.profile_name);
         txtEmail = findViewById(R.id.profile_email);
         circleImageView = findViewById(R.id.profile_pic);

         callbackManager = CallbackManager.Factory.create();
         loginButton.setPermissions(Arrays.asList("email","public_profile,user_friends,user_birthday"));
         checkLoginStatus();

         loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
             @Override
             public void onSuccess(LoginResult loginResult)
             {

             }

             @Override
             public void onCancel() {

             }

             @Override

             public void onError(FacebookException error) {

             }
         });
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         callbackManager.onActivityResult(requestCode,resultCode,data);
         super.onActivityResult(requestCode, resultCode, data);
     }

     AccessTokenTracker tokenTracker = new AccessTokenTracker() {
         @Override
         protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
         {
             if(currentAccessToken==null)
             {
                 txtName.setText("");
                 txtEmail.setText("");
                 circleImageView.setImageResource(0);
                 Toast.makeText(MainActivity.this,"User Logged out", Toast.LENGTH_LONG).show();
             }
             else
                 loadUserProfile(currentAccessToken);
         }
     };

     private void loadUserProfile(AccessToken newAccessToken)
     {
         GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
             @Override
             public void onCompleted(JSONObject object, GraphResponse response)
             {
                 try {
                     String first_name = object.getString("first_name");
                     String last_name = object.getString("last_name");
                     String email = object.getString("email");
                     String id = object.getString("id");
                     String user_friends = object.getString("user_friends");
                     String user_birthday = object.getString("user_birthday");


                     String image_url = "https://graph.facebook.com/"+id+ "/picture?type=normal";

                     txtEmail.setText(email);
                     txtName.setText(first_name +" "+last_name);
                     RequestOptions requestOptions = new RequestOptions();
                     requestOptions.dontAnimate();

                     Glide.with(MainActivity.this).load(image_url).into(circleImageView);


                 } catch (JSONException e) {
                     e.printStackTrace();
                 }

             }
         });

         Bundle parameters = new Bundle();
         parameters.putString("fields","first_name,last_name,email,id,user_friends,user_birthday");
         request.setParameters(parameters);
         request.executeAsync();

     }

     private void checkLoginStatus()
     {
         if(AccessToken.getCurrentAccessToken()!=null)
         {
             loadUserProfile(AccessToken.getCurrentAccessToken());
         }
     }
 }

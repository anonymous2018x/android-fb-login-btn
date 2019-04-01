package com.denno.cloncha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.HttpMethod;

import java.util.Arrays;

import org.json.JSONObject;
import org.json.JSONException;

public class LoginActivity extends Activity {

    private CallbackManager callbackManager;
    public TextView info;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.main_activity);
        info = (TextView)findViewById(R.id.info);
        getFbInfo();
        
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                
                GraphRequestAsyncTask request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                   
                    info.setText("User ID:  " + user.optString("id") +"\n" +
                                 "User Name:  " + user.optString("name") +"\n" +
                                 "User Email:  " + user.optString("email") +"\n");
                  
              }
            }).executeAsync();
            

            }

            @Override
            public void onCancel() {
                info.setText("Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
private void getFbInfo() {
    GraphRequest request = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken(),
            new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(
                        JSONObject object,
                        GraphResponse response) {
                    try {
                        Log.d("obj", "fb json object: " + object);
                        Log.d("rsp", "fb graph response: " + response);

                        info.setText(object.getString("email"));
                        //String first_name = object.getString("first_name");
                        //String last_name = object.getString("last_name");
                        //String gender = object.getString("gender");
                        //String birthday = object.getString("birthday");
                        //String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";

                        /*String email;
                        if (object.has("email")) {
                            email = object.getString("email");
                        }*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
    Bundle parameters = new Bundle();
    parameters.putString("fields", "id,name,email"); // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
    request.setParameters(parameters);
    request.executeAsync();
}
}

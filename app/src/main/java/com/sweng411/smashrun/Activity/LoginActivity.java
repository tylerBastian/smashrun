package com.sweng411.smashrun.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;


public class LoginActivity extends AppCompatActivity {

    String clientID = "classdemo_a1d8w1";
    String secret = "66fghqw74";
    String specialRedirect = "urn:ietf:wg:oauth:2.0:oob";
    String scope = "read_activity write_activity";
    String AuthURLString = "https://secure.smashrun.com/oauth2/token";
    String CheckUrlString = "https://api.smashrun.com/v1/auth/";
    SharedPreferences sharedPref;
    private OkHttpClient client = new OkHttpClient();


    // Prepare URL
    Uri uri = Uri.parse("https://secure.smashrun.com/oauth2/authenticate?")
            .buildUpon()
            .appendQueryParameter("client_id", clientID)
            .appendQueryParameter("redirect_uri", specialRedirect)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", scope)
            .appendQueryParameter("state", "mystate")
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("com.sweng411.smashrun", Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        //Check if user has already signed in if yes send to mainActivity
        //This to avoid signing in everytime you open the app.
        super.onStart();
        String accessToken = sharedPref.getString("token", "");
        Log.d("Auth", "Current token: " + accessToken);
        if(accessToken == "") {
            String authToken = sharedPref.getString("auth", "");
            if(authToken == "") {
                GetAuthToken();
            }
            else {
                try {
                    GetAccessToken(authToken);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            CheckIfExpired(accessToken);
        }



    }

    private void CheckIfExpired(String accessToken) {
        Request request = new Request.Builder()
                .url(CheckUrlString + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try(ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    String expireString = responseBody.string();
                    JSONObject reponseJson = new JSONObject(expireString);

                    int expireTime = reponseJson.getInt("expires_in");
                    Log.d("Auth", "Expires in: " + expireTime);

                    if(expireTime == 0) {
                        //Refresh Access Token
                    }
                    else {
                        SendToMainActivity();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private void GetAuthToken() {

        //Sets up a webpage to get the Authentication token for step 1 of OAUTH
        WebView webView = new WebView(this);
        Log.d("Auth", uri.toString());
        webView.loadUrl(uri.toString());
        webView.getSettings().setJavaScriptEnabled(true);

        //Puts the webpage into view

        Log.d("Auth", "Setting webview");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //Once the page loads, grab the authentication token from the title
                String authToken = view.getTitle();
                Log.d("Auth", "Webpage title " + authToken);


                //There is a weird error on first time log in, might be able to pull code from response
                if (!authToken.equals("Smashrun - Stats for runners")) {
                    if(authToken.equals("undefined")) {
                        view.reload();
                    }
                    try {
                        Log.d("Auth", "New Authentication Token: " + authToken);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("auth", authToken);
                        editor.apply();

                        GetAccessToken(authToken);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    finish();
                }

            }



        });

        setContentView(webView);



    }

    private void GetAccessToken(String authToken) throws IOException {
        //Creates a URL for POST call with AUTH Token

        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                String postParams = "grant_type=authorization_code&code=" + authToken + "&client_id=" + clientID + "&client_secret=" + secret;
                OutputStream os = sink.outputStream();
                byte[] input = postParams.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        };

        Request request = new Request.Builder()
                .url(AuthURLString)
                .post(requestBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String responseBody = response.body().string();
                Log.d ("Auth", responseBody);

                try {
                    JSONObject JsonResponse = new JSONObject(responseBody);
                    //Store keys in Shared Preferences
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("token", JsonResponse.getString("access_token"));
                    editor.putString("refresh", JsonResponse.getString("refresh_token"));
                    editor.apply();
                    SendToMainActivity();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });



    }

    private void SendToMainActivity() {
        //This is to send user to MainActivity
        Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(MainIntent);
    }
}
package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button btnTweet;

    TwitterClient twitterClient;

    TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCount = findViewById(R.id.tvCount);

        twitterClient = TwitterClientApp.getRestClient(this);

        SharedPreferences settings = getApplicationContext().getSharedPreferences("SAVE", 0);
        String myTweet = settings.getString("text", "");
        if (myTweet.length() >0) {
            etCompose.setText(myTweet);
        } else {
            etCompose.setText(null);
            etCompose.setHint("What's happening?");
        }


        etCompose.addTextChangedListener(new TextWatcher() {
            int sum = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvCount.setText(charSequence.length() + "/" + MAX_TWEET_LENGTH);
                tvCount.setTextColor(getResources().getColor(R.color.gray));
                if (charSequence.length() > MAX_TWEET_LENGTH) {
                    tvCount.setTextColor(getResources().getColor(R.color.colorRed));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                // Make sure to check whether returned data will be null.
                String titleOfPage = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);
                etCompose.setText("Here from: " + titleOfPage + ". Visit, " + urlOfPage);
            }
        }


        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String compose = etCompose.getText().toString();
                if (compose.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG);
                    return;
                }
                if (compose.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG);
                    return;
                }

                twitterClient.publishTweet(compose, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            Tweet tweet = Tweet.fromJSONObject(json.jsonObject);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                    }
                });
            }
        });
        //Make the call to Twitter API to publish the Tweet
    }

    @Override
    public void onBackPressed() {
        if (etCompose.length() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("You have unsaved drafts.")
                    .setMessage("Would you like to save before exiting?")
                    //.setNegativeButton(android.R.string.no, null)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            SharedPreferences settings = getApplicationContext().getSharedPreferences("SAVE", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("text", null);
                            editor.apply();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences settings = getApplicationContext().getSharedPreferences("SAVE", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("text", etCompose.getText().toString());
                            editor.apply();
                            Toast.makeText(ComposeActivity.this, "Draft is saved", Toast.LENGTH_SHORT);
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }).create().show();
        } else {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
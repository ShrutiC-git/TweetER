package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class IndividualTweet extends AppCompatActivity {

    TextView tvName, tvTweet, tvScreen;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_tweet);

        tvName = findViewById(R.id.tvName);
        tvTweet = findViewById(R.id.tvTweet);
        ivImage = findViewById(R.id.ivImage);
        tvScreen = findViewById(R.id.tvScreen);

        String name = getIntent().getStringExtra("name");
        String screenName = getIntent().getStringExtra("screenName");
        String image = getIntent().getStringExtra("image");
        String tweet = getIntent().getStringExtra("tweet");

        tvName.setText(String.format("%1s", name));
        tvScreen.setText(String.format("%s",screenName));
        tvTweet.setText(tweet);
        Glide.with(this).load(image).into(ivImage);

    }
}
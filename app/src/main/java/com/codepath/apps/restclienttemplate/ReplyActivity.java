package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

public class ReplyActivity extends AppCompatActivity {

    TextView tvName, tvReplyTo;
    ImageView ivDP;
    Button btnReply;
    EditText etReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        tvReplyTo = findViewById(R.id.tvReplyTo);
        ivDP = findViewById(R.id.ivDP);
        btnReply = findViewById(R.id.btnReply);
        etReply = findViewById(R.id.etReply);

        String name = getIntent().getStringExtra("name");
        String screenName = getIntent().getStringExtra("screenName");
        String image = getIntent().getStringExtra("image");

        tvReplyTo.setText("Replying to "+name);
        etReply.setText("@"+screenName);
        etReply.setSelection(etReply.length());
        Glide.with(this).load(image).into(ivDP);


        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
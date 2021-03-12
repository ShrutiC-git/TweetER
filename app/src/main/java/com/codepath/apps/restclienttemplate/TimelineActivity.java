package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String TAG = "TimelineAct";

    public static final int REQ_CODE = 42;
    public static final int REQ_CODE_RT = 12;

    TweetDao tweetDao;

    TwitterClient client;

    RecyclerView rvTweets;

    List<Tweet> tweets;
    TweetsAdapter adapter;

    FloatingActionButton btnCompose;

    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        getSupportActionBar().setLogo(R.drawable.twitter_bird);

        client = TwitterClientApp.getRestClient(TimelineActivity.this);

        tweetDao = ((TwitterClientApp) getApplicationContext()).getMyDatabase().tweetDao();


        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("Timeline", "Fetching New Data");
                populateHomeTimeline();

            }
        });

        rvTweets = findViewById(R.id.rvTweets);

        btnCompose =  findViewById(R.id.btnCompose);

        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivityForResult(intent,REQ_CODE);
            }
        });

        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(tweets, this);

        adapter.setOnItemClickListener(new TweetsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent intent = new Intent(TimelineActivity.this, IndividualTweet.class);
                String name = tweets.get(position).user.name;
                String screenName = tweets.get(position).user.screenName;
                String image = tweets.get(position).user.publicImageURL;
                String tweet = tweets.get(position).body;
                intent.putExtra("name", name);
                intent.putExtra("screenName", screenName);
                intent.putExtra("image", image);
                intent.putExtra("tweet", tweet);
                startActivityForResult(intent, REQ_CODE);
            }

            @Override
            public void onItemReply(View itemView, final int position) {

                        Intent intent = new Intent(TimelineActivity.this, ReplyActivity.class);
                        String name = tweets.get(position).user.name;
                        String screenName = tweets.get(position).user.screenName;
                        String image = tweets.get(position).user.publicImageURL;
                        intent.putExtra("name",name);
                        intent.putExtra("screenName", screenName);
                        intent.putExtra("image", image);
                        startActivityForResult(intent, REQ_CODE_RT);
                    }

        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreData();
            }
        };

        //Add the scroll listener to RV
        rvTweets.addOnScrollListener(scrollListener);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);
                adapter.clear();
                adapter.addAll(tweetsFromDB);

            }
        });

        populateHomeTimeline();

    }


    private void loadMoreData() {
        client.getNextPageofTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                List<Tweet> tweets = null;
                try {
                    tweets = Tweet.fromJSONArray(jsonArray);
                    adapter.addAll(tweets);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        }, tweets.get(tweets.size() - 1).id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
            // get data from the intent
            if(data.getExtras()==null){
                Log.e("Test", "No DATA");
                return;
            }
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            //update RV
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "OnSuccess" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJSONArray(jsonArray);
                    adapter.clear();
                    adapter.addAll(tweetsFromNetwork);
                    swipeContainer.setRefreshing(false);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<User> usersFromNetwork=User.fromJSONTweetArray(tweetsFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }
}
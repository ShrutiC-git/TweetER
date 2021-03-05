package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {

    public String body;
    public String createdAt;
    public User user;
    public long id;


    public static Tweet fromJSONObject(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");

        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray array) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i=0; i<array.length(); i++){
            tweets.add(fromJSONObject(array.getJSONObject(i)));
        }
        return tweets;
    }

    public String getFormattedTimestamp(){
        return TimeFormatter.getTimeDifference(this.createdAt);
    }
}

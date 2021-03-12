package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class,parentColumns = "id", childColumns = "userId"))
public class Tweet {

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public long userId;

    @Ignore
    public User user;

    @ColumnInfo
    @PrimaryKey
    public long id;

    //empty constructor for the Parceler Library
    public Tweet(){}

    public static Tweet fromJSONObject(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.id = jsonObject.getLong("id");
        User user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.user = user;
        tweet.userId = user.id;
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

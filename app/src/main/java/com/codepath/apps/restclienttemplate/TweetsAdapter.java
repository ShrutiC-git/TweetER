package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

/**
 * Class to bind the data with the view on RecyclerView
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    List<Tweet> tweets;
    Context context;

    private OnItemClickListener listener;

    public TweetsAdapter(List<Tweet> tweets, Context context) {
        this.tweets = tweets;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tweets, parent, false);
        return new ViewHolder(view);
    }

    //Bind the layout to items based on position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet>tweetList){
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }
    // Pass the context and data required by the adapter

    // For each row, inflate the layout and wrap in ViewHolder

    // Bind values based on the elements

    // Get the viewholder to display the data

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBody, tvName, tvTime;
        ImageView ivProfile;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        listener.onItemClick(itemView, position);
                    }
                }
            });
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvName.setText(String.format("%1s @ %2s", tweet.user.name, tweet.user.screenName));
            Glide.with(context).load(tweet.user.publicImageURL).into(ivProfile);
            tvTime.setText(String.format("Posted %s ago", tweet.getFormattedTimestamp()));
        }
    }
}

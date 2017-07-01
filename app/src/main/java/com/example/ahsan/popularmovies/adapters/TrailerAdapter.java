package com.example.ahsan.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.model.details.Videos;

import static com.squareup.picasso.Picasso.with;

/**
 * Created by Ahsan on 2017-06-02.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    private int mId;
    private Cursor currentCursor;
    private Context ctx;
    private Videos videos;
    public TrailerAdapter(Context context, int movieid) {
        ctx = context;
    }

    
    public void setData(Videos trailervideos){
        videos = trailervideos;
        notifyDataSetChanged();
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View trailerItem = LayoutInflater.from(ctx).inflate(R.layout.layout_list_view_row_trailers, parent, false);
        ViewHolder viewHolder = new ViewHolder(trailerItem);
        trailerItem.setFocusable(true);
        
        return viewHolder;
        
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (videos != null && videos.getResults().size() > 0) {
            final String imageKey = videos.getResults().get(position).getKey();
                with(ctx).load("https://img.youtube.com/vi/" + imageKey + "/1.jpg").into(holder.trailerThumbnail);
        }
    }
    
    @Override
    public int getItemCount() {
        if(videos==null)
            return 0;
        
        return videos.getResults().size();
    }
    
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        
        String key;
        private ImageView trailerThumbnail;
        
        public ViewHolder(View itemView) {
            super(itemView);
            trailerThumbnail = (ImageView) itemView.findViewById(R.id.trailer_imageview);
            trailerThumbnail.setOnClickListener(this);
        }
        
        public void setKey(String key) {
            this.key = key;
        }
        
        @Override
        public void onClick(View v) {
            playVideo(key);
        }
        
        public void playVideo(String key) {
            
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
            
            // Check if the youtube app exists on the device
            if (intent.resolveActivity(ctx.getPackageManager()) == null) {
                // If the youtube app doesn't exist, then use the browser
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(YOUTUBE_URL + key));
            }
            
            ctx.startActivity(intent);
        }
        
    }
    
    
}

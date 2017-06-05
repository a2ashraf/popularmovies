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
import com.example.ahsan.popularmovies.data.MovieContract;
import com.squareup.picasso.RequestCreator;

import static com.squareup.picasso.Picasso.with;

/**
 * Created by Ahsan on 2017-06-02.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    
    private Cursor currentCursor;
    private Context ctx;
    private final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    public TrailerAdapter(Context context, boolean autoRequery) {
    ctx = context;
    }
    
    
    public void swapCursor(Cursor newCursor) {
        currentCursor = newCursor;
        notifyDataSetChanged();
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View trailerItem= LayoutInflater.from(ctx).inflate(R.layout.layout_list_view_row_trailers, parent, false);
        ViewHolder viewHolder = new ViewHolder(trailerItem);
        trailerItem.setFocusable(true);
    
        return viewHolder;
    
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        currentCursor.moveToPosition(position);
        
        int imageColumnIndex = currentCursor.getColumnIndex(MovieContract.MovieTrailers.COLUMN_TRAILER_KEY);
        final String imageKey= currentCursor.getString(imageColumnIndex);
        holder.setKey( imageKey);
        with(ctx).load("https://img.youtube.com/vi/"+holder.key+"/2.jpg").into(holder.trailerThumbnail);
    
        RequestCreator picassoLoadRequest = with(ctx).load("https://img.youtube.com/vi/" + holder.key + "/1.jpg");
        if(picassoLoadRequest!=null)
            picassoLoadRequest.into(holder.trailerThumbnail);
                
                
        
    }
    
    
    @Override
    public int getItemCount() {
        
        
        if (null == currentCursor)
            return 0;
        
        return currentCursor.getCount();
    }
    
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    
        private ImageView trailerThumbnail;
    
        public void setKey(String key) {
            this.key = key;
        }
    
        String key;
        public ViewHolder(View itemView) {
            super(itemView);
            trailerThumbnail = (ImageView) itemView.findViewById(R.id.trailer_imageview);
            trailerThumbnail.setOnClickListener(this);
        }
    
        @Override
        public void onClick(View v) {
            playVideo(key);
        }
        public void playVideo(String key){
        
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

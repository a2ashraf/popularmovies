package com.example.ahsan.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.data.MovieContract;

/**
 * Created by Ahsan on 2017-06-02.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    
    private int mId;
    private Cursor currentCursor;
    private Context ctx;
    
    public ReviewAdapter(Context context, int movieid) {
        mId = movieid;
        ctx = context;
    }
    
    public void setmId(int mId) {
        this.mId = mId;
    }
    
    public void swapCursor(Cursor newCursor) {
        currentCursor = newCursor;
        notifyDataSetChanged();
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View trailerItem = LayoutInflater.from(ctx).inflate(R.layout.layout_list_view_row_reviews, parent, false);
        ViewHolder viewHolder = new ViewHolder(trailerItem);
        trailerItem.setFocusable(true);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!currentCursor.isClosed()) {
            currentCursor.moveToPosition(position);
            int authorIndex = currentCursor.getColumnIndex(MovieContract.MovieReview.COLUMN_REVIEW_AUTHOR);
            int contentIndex = currentCursor.getColumnIndex(MovieContract.MovieReview.COLUMN_REVIEW_CONTENT);
            int movieIdIndex = currentCursor.getColumnIndex(MovieContract.MovieReview.COLUMN_MOVIEID);
            
            final String author_name = currentCursor.getString(authorIndex);
            final String author_content = currentCursor.getString(contentIndex);
            final int movieId = Integer.parseInt(currentCursor.getString(movieIdIndex));
            if (movieId == mId) {
                holder.reviewAuthor.setText(author_name);
                holder.reviewContent.setText(author_content);
            }
        }
    }
    @Override
    public int getItemCount() {
  
        if (null == currentCursor)
            return 0;
        
        return currentCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        
    
         private TextView reviewContent;
        private TextView reviewAuthor;
        
        public ViewHolder(View itemView) {
            super(itemView);
            reviewContent = (TextView) itemView.findViewById(R.id.review_content);
            reviewAuthor= (TextView) itemView.findViewById(R.id.review_author);
            
         }
        
       
    }
    
    
}

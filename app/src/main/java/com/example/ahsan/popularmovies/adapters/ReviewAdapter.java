package com.example.ahsan.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ahsan.popularmovies.R;
import com.example.ahsan.popularmovies.model.details.Reviews;

/**
 * Created by Ahsan on 2017-06-02.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    
    private int mId;
    private Cursor currentCursor;
    private Context ctx;
    private Reviews reviews;
    
    public ReviewAdapter(Context context, int movieid) {
        ctx = context;
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
        if (null!=reviews && reviews.getReviewResults().size()>0) {
            
             
            final String author_name = reviews.getReviewResults().get(position).getAuthor();
            final String author_content = reviews.getReviewResults().get(position).getContent();
            holder.reviewAuthor.setText(author_name);
            holder.reviewContent.setText(author_content);
        }
    }
    @Override
    public int getItemCount() {
  
        if (null == reviews)
            return 0;
    
        return reviews.getReviewResults().size();
    }
    
    public void setData(Reviews movieReviews) {
        reviews = movieReviews;
        notifyDataSetChanged();
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

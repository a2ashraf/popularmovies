package com.example.ahsan.popularmovies.enums;

/**
 * Created by Ahsan on 2017-02-10.
 */

public enum MovieResponse {
    TITLE("original_title"),
    THUMBNAIL("poster_path"),
    OVERVIEW("overview"),
    RELEASE_DATE("release_date"),
    RATING("vote_average"),
    MOVIEID("movie_id"),
    FAVORITE("is_favorite"),
    POSTERPATH("poster_path"),
    DURATION("duration");
    
    public String value;

    MovieResponse(String title) {
        value = title;
    }
}


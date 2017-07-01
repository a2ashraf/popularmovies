
package com.example.ahsan.popularmovies.model.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Videos {

    @SerializedName("results")
    @Expose
    private List<VideoResult> results = null;

    public List<VideoResult> getResults() {
        return results;
    }

    public void setResults(List<VideoResult> results) {
        this.results = results;
    }

}

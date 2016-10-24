package com.feliperrm.animationsproject.network;

import com.feliperrm.animationsproject.models.Movie;

import java.io.Serializable;
import java.util.ArrayList;

public class NowPlayingReturn implements Serializable {
    int page;
    ArrayList<Movie> results;
    int total_pages;
    int total_results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<Movie> getResults() {
        return results;
    }

    public void setResults(ArrayList<Movie> results) {
        this.results = results;
    }
}



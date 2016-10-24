package com.feliperrm.animationsproject.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by felip on 02/10/2016.
 */

public interface ApiCalls {

    public static final String API_URL = "https://api.themoviedb.org/3/";
    public static final String API_KEY = "81b8ab905c63d3aadb78a7d41df27a53";

    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";

    public static final String BACKDROP_CONFIG = "w500";

    public static final String POSTER_CONFIG = "w500";


    @GET("movie/now_playing?api_key=" + API_KEY)
    Call<NowPlayingReturn> getNowPlaying();

}

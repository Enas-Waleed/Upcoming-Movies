package com.feliperrm.animationsproject.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.View;

import com.feliperrm.animationsproject.R;
import com.feliperrm.animationsproject.adapters.MovieAdapter;
import com.feliperrm.animationsproject.models.Movie;
import com.feliperrm.animationsproject.utils.AnimationUtils;
import com.feliperrm.animationsproject.utils.MyApp;

import java.util.ArrayList;

public class ListActivity extends BaseActivity {

    public static final String MOVIES_KEY = "movieskey";
    RecyclerView recyclerView;
    Toolbar toolbar;
    ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        recoverInfo();
        findViews();
        setUpViews();
        getWindow().setEnterTransition(AnimationUtils.makeEnterTransition());
    }

    private void recoverInfo(){
        movies = (ArrayList<Movie>) getIntent().getSerializableExtra(MOVIES_KEY);
    }

    private void findViews()
    {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setUpViews(){
        recyclerView.setVisibility(View.GONE);


        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(ListActivity.this, LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(new MovieAdapter(ListActivity.this, toolbar, movies));
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Otherwise the shared elements (the logo in this case) blinks for a second.
    }


}

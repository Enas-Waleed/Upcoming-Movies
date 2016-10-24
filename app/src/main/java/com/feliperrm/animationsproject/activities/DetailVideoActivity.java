package com.feliperrm.animationsproject.activities;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.feliperrm.animationsproject.R;
import com.feliperrm.animationsproject.models.Movie;
import com.feliperrm.animationsproject.utils.AnimationUtils;

/**
 *  There is a lot of code in this Activity to fix some bugs that happen during Lollipop's Transition Animations,
 *  like ignoring the Navbar and the Statusbar, which have to be added to the transition animation manually (thus,
 *  postponing it until the view is ready).
 */

public class DetailVideoActivity extends BaseActivity {


    public static String MOVIE_BUNDLE_KEY = "moviebundlekey";
    public static final String ANIMATE_POSTPONE_KEY = "animate";


    Movie thisMovie;
    ImageView postImage;
    TextView postTitle, postText;
    Toolbar toolbar;
    FrameLayout backArrow;
    NestedScrollView scrollView;

    boolean navBarReady = false;
    boolean imageReady = false;

    private boolean animatePostPone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        recoverInfoFromBundle();
        findViews();
        setUpViews();
    }


    private void recoverInfoFromBundle(){
        thisMovie = (Movie) getIntent().getExtras().getSerializable(MOVIE_BUNDLE_KEY);
        animatePostPone = getIntent().getExtras().getBoolean(ANIMATE_POSTPONE_KEY, false);
    }

    private void findViews(){
        postImage = (ImageView) findViewById(R.id.postImage);
        postTitle = (TextView) findViewById(R.id.postTitle);
        postText = (TextView) findViewById(R.id.postText);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        backArrow = (FrameLayout) findViewById(R.id.backArrow);
    }

    private void setUpViews(){
            backArrow.setVisibility(View.GONE);
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            postTitle.setText(thisMovie.getTitle());
            postText.setText(thisMovie.getOverview());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            animateTextDown();
            setUpImageView();
            fixNavigationBgBug();
    }


    private void animateBackArrow(){
        backArrow.setTranslationX(-100f);
        backArrow.setAlpha(0.5f);
        backArrow.setVisibility(View.VISIBLE);
        backArrow.animate().translationX(0).alpha(1f).setInterpolator(new DecelerateInterpolator()).setDuration(500).start();
    }

    private void animateTextDown(){
        postTitle.setTranslationY(-130f);
        postTitle.animate().translationY(0f).setDuration(800).setInterpolator(new AccelerateDecelerateInterpolator()).setStartDelay(100).start();

        postText.setAlpha(0f);
        postText.setTranslationY(-70f);
        postText.animate().translationY(0f).alpha(1f).setDuration(800).setInterpolator(new AccelerateDecelerateInterpolator()).setStartDelay(250).start();

    }


    private void fixNavigationBgBug(){
        if(animatePostPone)
            ActivityCompat.postponeEnterTransition(this);
        final View decorView = getWindow().getDecorView();
        decorView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                decorView.removeOnLayoutChangeListener(this);
                View navigationBarBackground = getWindow().getDecorView().findViewById(android.R.id.navigationBarBackground);
                if (navigationBarBackground != null) {
                    android.support.v4.view.ViewCompat.setTransitionName(navigationBarBackground, "navigationBg");
                }
                navBarReady = true;
                startTransitionIfReady();
            }
        });

    }

    private void startTransitionIfReady(){
        if(navBarReady && imageReady){
            ActivityCompat.startPostponedEnterTransition(DetailVideoActivity.this);
            getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    animateBackArrow();
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
    }

        private void setUpImageView(){
        postImage.setVisibility(View.VISIBLE);
        if ((thisMovie.getBackdrop_path() != null) && (!thisMovie.getBackdrop_path().isEmpty()))
            Glide.with(DetailVideoActivity.this).load(thisMovie.getBackdrop_path()).dontAnimate().listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    imageReady = true;
                    startTransitionIfReady();
                    return false;
                }
            }).diskCacheStrategy(DiskCacheStrategy.SOURCE).fitCenter().into(postImage);
        else {
            postImage.setImageDrawable(ContextCompat.getDrawable(DetailVideoActivity.this, R.drawable.no_img));
            imageReady = true;
            backArrow.setVisibility(View.VISIBLE);
        }
    }

    private static final int OUT_ANIM_DURATION = 115;
    @Override
    public void onBackPressed()
    {
        backArrow.animate().translationX(-100).alpha(0.5f).setDuration(OUT_ANIM_DURATION).setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                backArrow.setVisibility(View.GONE);
            }
        }).start();
        if(scrollView!=null) {
            scrollView.smoothScrollTo(0,0);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DetailVideoActivity.super.onBackPressed();
                }
            },OUT_ANIM_DURATION);
        }
        else
            super.onBackPressed();
    }

}

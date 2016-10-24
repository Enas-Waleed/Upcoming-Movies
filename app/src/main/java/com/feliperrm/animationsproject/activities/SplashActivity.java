package com.feliperrm.animationsproject.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feliperrm.animationsproject.R;
import com.feliperrm.animationsproject.models.Movie;
import com.feliperrm.animationsproject.network.NowPlayingReturn;
import com.feliperrm.animationsproject.utils.MyApp;
import com.github.javiersantos.bottomdialogs.BottomDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

    ImageView logo;
    TextView loading;
    TextView dot1, dot2, dot3;
    LinearLayout loadingParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findViews();
        animateLogo();
    }

    private void findViews(){
        logo = (ImageView) findViewById(R.id.logo);
        loading = (TextView) findViewById(R.id.loading_text);
        dot1 = (TextView) findViewById(R.id.txtDot1);
        dot2 = (TextView) findViewById(R.id.txtDot2);
        dot3 = (TextView) findViewById(R.id.txtDot3);
        loadingParent = (LinearLayout) findViewById(R.id.loadingParent);
    }

    private void animateLogo(){
        logo.setScaleX(0);
        logo.setScaleY(0);
        logo.setRotation(180);

        logo.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotation(0f)
                .setDuration(1250)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animateLoadingTxt();
                        getVideoData();
                    }
                })
                .start();
    }

    private void animateLoadingTxt(){
        loadingParent.setVisibility(View.VISIBLE);

        PropertyValuesHolder dot1Y = PropertyValuesHolder.ofFloat(dot1.TRANSLATION_Y, -30.0f);
        PropertyValuesHolder dot1X = PropertyValuesHolder.ofFloat(dot1.TRANSLATION_X, 0);
        ObjectAnimator dot1anim = ObjectAnimator.ofPropertyValuesHolder(dot1, dot1X, dot1Y);
        dot1anim.setRepeatCount(-1);
        dot1anim.setRepeatMode(ValueAnimator.REVERSE);
        dot1anim.setDuration(500);
        dot1anim.setInterpolator(new AccelerateDecelerateInterpolator());
        dot1anim.start();

        PropertyValuesHolder dot2Y = PropertyValuesHolder.ofFloat(dot2.TRANSLATION_Y, -30.0f);
        PropertyValuesHolder dot2X = PropertyValuesHolder.ofFloat(dot2.TRANSLATION_X, 0);
        ObjectAnimator dot2Anim = ObjectAnimator.ofPropertyValuesHolder(dot2, dot2X, dot2Y);
        dot2Anim.setRepeatCount(-1);
        dot2Anim.setRepeatMode(ValueAnimator.REVERSE);
        dot2Anim.setDuration(500);
        dot2Anim.setStartDelay(150);
        dot2Anim.setInterpolator(new AccelerateDecelerateInterpolator());
        dot2Anim.start();

        PropertyValuesHolder dot3Y = PropertyValuesHolder.ofFloat(dot3.TRANSLATION_Y, -30.0f);
        PropertyValuesHolder dot3X = PropertyValuesHolder.ofFloat(dot3.TRANSLATION_X, 0);
        ObjectAnimator dot3anim = ObjectAnimator.ofPropertyValuesHolder(dot3, dot3X, dot3Y);
        dot3anim.setRepeatCount(-1);
        dot3anim.setRepeatMode(ValueAnimator.REVERSE);
        dot3anim.setDuration(500);
        dot3anim.setStartDelay(250);
        dot3anim.setInterpolator(new AccelerateDecelerateInterpolator());
        dot3anim.start();

    }

    private void getVideoData(){
        ((MyApp)getApplication()).getApiCalls().getNowPlaying().enqueue(new Callback<NowPlayingReturn>() {
            @Override
            public void onResponse(Call<NowPlayingReturn> call, Response<NowPlayingReturn> response) {
                if(response.isSuccessful()) {

                    goToListActivity(response.body().getResults());
                }
                else
                    onFailure(call, new Exception("Network error code: " + response.code()));
            }

            @Override
            public void onFailure(Call<NowPlayingReturn> call, Throwable t) {
                /*
                    Shows the error dialog when there's no connection to the Internet or if some other problem happened.
                    Could have used something to persist the data if it had previously loaded, like SharedPreferences, SQLite or Realm,
                    and display it in this error scenario.
                 */
                t.printStackTrace();
                loadingParent.setVisibility(View.GONE);
                new BottomDialog.Builder(SplashActivity.this)
                        .setTitle(getString(R.string.erro_download_title))
                        .setContent(getString(R.string.error_download_body))
                        .setIcon(R.drawable.error)
                        .setPositiveText(R.string.ok)
                        .setCancelable(false)
                        .autoDismiss(false)
                        .onPositive(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(@NonNull BottomDialog bottomDialog) {
                                bottomDialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    private void goToListActivity(ArrayList<Movie> movies){
        Intent listActivity = new Intent(SplashActivity.this, ListActivity.class);
        ArrayList<Pair<View, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<View, String>(logo, logo.getTransitionName()));
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs.get(0));
        listActivity.putExtra(ListActivity.MOVIES_KEY, movies);
        startActivity(listActivity, transitionActivityOptions.toBundle());
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}

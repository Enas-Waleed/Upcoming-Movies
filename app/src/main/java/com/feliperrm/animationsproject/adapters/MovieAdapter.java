package com.feliperrm.animationsproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.feliperrm.animationsproject.activities.DetailVideoActivity;
import com.feliperrm.animationsproject.models.Movie;
import com.feliperrm.animationsproject.utils.AnimationUtils;

import java.util.ArrayList;

/**
 * Created by felip on 02/10/2016.
 */

public class MovieAdapter extends RecyclerView.Adapter <MovieAdapter.VideoCellViewHolder> {


    Context context;
    ArrayList<Movie> movies;
    Toolbar toolbar;
    int lastPosition; // used to animate when first reaching the view
    int screenWidth; // used to animate views
    int firs_run_delay = 125; // used as a proportional delay when animating views on the entry screen (otherwise all would animate together).

    public MovieAdapter(Context context, Toolbar toolbar, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
        this.toolbar = toolbar;

        /*
            Used to get Screen Width to animate views when first seeing them
         */
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;

        /*
            Sets the delay to 0, as the other views will only animate when reached.
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                firs_run_delay = 0;
            }
        },700);
    }

    @Override
    public VideoCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video_row, parent, false);
        VideoCellViewHolder viewHolder = new VideoCellViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VideoCellViewHolder holder, int position) {
        /*
            Used to animate the view the first time it appears.
         */
        setAnimation(holder.cardView,position);


        final Movie currentItem = movies.get(position);

        /*
            Using Glide to load the images. This block of code here loads the image on the cropped and on the fullscreen resolution, so a smooth animation can be played between this
            cropped view and the full screen image on the Detail Screen. If this wasn't here, the image would have to be loaded again and the animation would look terrible.
         */

        if ((currentItem.getBackdrop_path() != null) && (!currentItem.getBackdrop_path().isEmpty()))
            Glide.with(context).load(currentItem.getBackdrop_path()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    currentItem.setImageLoaded(true);
                    return false;
                }
            }).into(holder.image);
        else
            holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.no_img));

        holder.title.setText(currentItem.getTitle());
        holder.description.setText(currentItem.getOverview());


        /*
            This block of code here sets up the transition animation between the two views, addind views like status bar, toolbar and navigation bar, which are all shared between screens.
            If those views weren't added, they would "blink" when transitioning. As not all phones have a navigation bar (some have physical buttons), it can be null, so this must be checked.
         */
        holder.viewToClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent prox = new Intent(context, DetailVideoActivity.class);
                    prox.putExtra(DetailVideoActivity.MOVIE_BUNDLE_KEY, currentItem);
                    if (currentItem.isImageLoaded()) {
                        View decor = ((Activity) context).getWindow().getDecorView();
                        View navigationBarBackground = decor.findViewById(android.R.id.navigationBarBackground);
                        View statusBar = decor.findViewById(android.R.id.statusBarBackground);
                        Pair<View, String> statusBarTransition = Pair.create(statusBar, statusBar.getTransitionName());
                        Pair<View, String> imageTransition = Pair.create((View) holder.image, "image");
                        Pair<View, String> navbarTransition = Pair.create(navigationBarBackground, "navigationBg");
                        Pair<View, String> toolbarTransition = Pair.create((View)toolbar, toolbar.getTransitionName());
                        Pair<View, String>[] pairs = (navigationBarBackground != null) ? new Pair[]{statusBarTransition, toolbarTransition, navbarTransition, imageTransition} : new Pair[]{statusBarTransition, toolbarTransition, imageTransition};
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) context, pairs);
                        prox.putExtra(DetailVideoActivity.ANIMATE_POSTPONE_KEY, true);
                        context.startActivity(prox, options.toBundle());
                    } else {
                        prox.putExtra(DetailVideoActivity.ANIMATE_POSTPONE_KEY, false);
                        context.startActivity(prox);
                    }

            }
        });

        /*
            Animation to elevate the view when clicking on it, as recomended by Google's Material Design.
         */
        AnimationUtils.setAnimationElevation(holder.viewToClick, holder.cardView, true);
    }

    @Override
    public int getItemCount() {
        return  movies.size();
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {

            viewToAnimate.setTranslationX(-screenWidth);
            viewToAnimate.setAlpha(0f);
            viewToAnimate.animate().translationX(0).alpha(1f).setStartDelay(firs_run_delay * position).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();
            lastPosition = position;


        }
    }

    public static class VideoCellViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, description;
        FrameLayout viewToClick;
        CardView cardView;

        public VideoCellViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imageView);
            title = (TextView) itemView.findViewById(R.id.textTitle);
            description = (TextView) itemView.findViewById(R.id.textDescription);
            viewToClick = (FrameLayout) itemView.findViewById(R.id.viewToClick);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }

}


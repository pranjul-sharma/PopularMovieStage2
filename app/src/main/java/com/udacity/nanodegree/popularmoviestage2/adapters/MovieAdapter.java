package com.udacity.nanodegree.popularmoviestage2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.popularmoviestage2.DataUtils.Movie;
import com.udacity.nanodegree.popularmoviestage2.DetailActivity;
import com.udacity.nanodegree.popularmoviestage2.MainActivity;
import com.udacity.nanodegree.popularmoviestage2.R;
import com.udacity.nanodegree.popularmoviestage2.Utils.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Adapter class for binding data with recycler view
 * and providing functionality on click.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    // Constants for inflating different layouts depending upon
    // the position in recycler view.
    public static final int VIEWTYPE_FIRST_ITEM = 0;
    public static final int VIEWTYPE_REST_ITEMS = 1;


    private final Context context;
    private List<Movie> movies;

    private MovieViewHolder viewHolder;

    // Constructor for initializing adapter.
    public MovieAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;
        switch (viewType) {
            case VIEWTYPE_FIRST_ITEM:
                layoutId = R.layout.item_recycler_at_first;
                break;
            case VIEWTYPE_REST_ITEMS:
                layoutId = R.layout.item_recycler_rest;
                break;
            default:
                throw new IllegalArgumentException("Invalid value for view type, found : " + viewType);
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new MovieViewHolder(view);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEWTYPE_FIRST_ITEM;
        else return VIEWTYPE_REST_ITEMS;
    }

    // method for updating the movies fetched from the tmdb server.
    public void updateMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, final int position) {
        viewHolder = holder;
        if (position != 0) {

            // getting display height so that card view can adjust itself
            // depending upon the display size. So on each device layout
            // will look approximately similar.
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            holder.ivMovieThumbnail.setMinimumHeight((int) (height / 2.75));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


            int marginCenters = (int) context.getResources().getDimension(R.dimen.cardview_margin);
            int marginEnd = (int) context.getResources().getDimension(R.dimen.cardview_marginEnd);

            // applying different margins to views depending upon their respective
            // position in list so that the UI will look equi-spaced from left and right.
            if (position % 2 != 0)
                params.setMargins(marginEnd, marginCenters, marginCenters, marginCenters);
            else
                params.setMargins(marginCenters, marginCenters, marginEnd, marginCenters);

            holder.cardViewMovie.setLayoutParams(params);

            if (movies.size() != 0) {
                Picasso.with(context).load(NetworkUtils.buildImgUrl(movies.get(position - 1).getPosterPath()).toString())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(holder.ivMovieThumbnail);
            }

            holder.ivMovieThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(MainActivity.PARCELABLE_KEY, movies.get(position - 1));
                    context.startActivity(intent);
                }
            });
        } else {
            // getting the current movie category to show it in the textview at the top of list.
            SharedPreferences menuPrefs = context.getSharedPreferences(context.getResources().getString(R.string.sp_name), Context.MODE_PRIVATE);
            holder.tvSortOrder.setText(menuPrefs.getString(context.getResources().getString(R.string.sp_key_movie_current_type),
                    context.getResources().getString(R.string.movie_by_popularity_key)));
        }

    }

    public List<Movie> getMovies() {
        return movies;
    }

    public int getViewHolderPosition() {
        if (viewHolder != null)
            return viewHolder.getAdapterPosition();
        else return 0;
    }

    @Override
    public int getItemCount() {
        if (movies != null)
            return movies.size() + 1;
        else return 1;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.iv_movie_thumb)
        ImageView ivMovieThumbnail;
        @Nullable
        @BindView(R.id.tv_sort_order)
        TextView tvSortOrder;
        @Nullable
        @BindView(R.id.card_movies)
        CardView cardViewMovie;

        MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

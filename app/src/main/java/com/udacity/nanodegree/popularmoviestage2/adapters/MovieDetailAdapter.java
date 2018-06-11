package com.udacity.nanodegree.popularmoviestage2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.popularmoviestage2.DataUtils.Movie;
import com.udacity.nanodegree.popularmoviestage2.DataUtils.MovieVideo;
import com.udacity.nanodegree.popularmoviestage2.R;
import com.udacity.nanodegree.popularmoviestage2.Utils.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailAdapter extends RecyclerView.Adapter<MovieDetailAdapter.MovieVideoViewHolder> {

    private final Context context;
    private final int VIEWTYPE_DETAILS_AT_FIRST = 0;
    private final int VIEWTYPE_VIDEOS_AT_REST = 1;
    private List<MovieVideo> movieVideos;
    private Movie movie;

    private MovieVideoViewHolder viewHolder;

    public MovieDetailAdapter(Context context, Movie movie) {
        this.context = context;
        this.movie = movie;
    }

    public void updateMovieVideos(List<MovieVideo> movieVideos) {
        this.movieVideos = movieVideos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout_id;
        switch (viewType) {
            case VIEWTYPE_DETAILS_AT_FIRST:
                layout_id = R.layout.item_movie_detail_at_first;
                break;
            case VIEWTYPE_VIDEOS_AT_REST:
                layout_id = R.layout.item_video_view;
                break;
            default:
                throw new IllegalArgumentException("Invalid value for viewtype parameter: " + viewType);
        }
        View view = LayoutInflater.from(context).inflate(layout_id, parent, false);
        return new MovieVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVideoViewHolder holder, int position) {
        viewHolder = holder;
        if (position == 0) {
            //populating UI components
            // fetching images for cover photo and poster photo from tmdb server.
            Picasso.with(context).load(NetworkUtils.buildImgUrl(movie.getCoverPath()).toString())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.coverPhoto);
            Picasso.with(context).load(NetworkUtils.buildImgUrl(movie.getPosterPath()).toString())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.posterPhoto);

            // setting the title of the movie.
            holder.movieTitle.setText(movie.getTitle());
            String temp = String.valueOf(movie.getVoteAverage()) + "/10";

            // displaying rating of movie in numerical form.
            holder.movieRating.setText(temp);

            // displaying rating of movie in graphical form.
            holder.movieRatingBar.setRating((float) (movie.getVoteAverage() / 2));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd", Locale.US);

            // displaying other information available about the movie.
            holder.releaseDate.setText(sdf.format(movie.getReleaseDate()));
            holder.originalLang.setText(movie.getOriginalLang());
            holder.overview.setText(movie.getOverview());
        } else {
            MovieVideo movieVideo = movieVideos.get(position - 1);
            holder.videoName.setText(movieVideo.getName());
            String source = "Source: ".concat(movieVideo.getSite());
            holder.videoSource.setText(source);
            String type = "Type: ".concat(movieVideo.getType());
            holder.videoType.setText(type);
            final Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + movieVideo.getKey()));
            final Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(NetworkUtils.buildVideoUrl(movieVideo.getSite(), movieVideo.getKey()).toString()));
            holder.layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isYoutubeAppInstalled())
                        context.startActivity(appIntent);
                    else context.startActivity(webIntent);
                }
            });
        }

    }

    public List<MovieVideo> getMovieVideos() {
        return movieVideos;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEWTYPE_DETAILS_AT_FIRST;
        else return VIEWTYPE_VIDEOS_AT_REST;
    }

    public int getViewHolderPosition() {
        if (viewHolder != null)
            return viewHolder.getAdapterPosition();
        else return 0;
    }

    @Override
    public int getItemCount() {
        if (movieVideos != null)
            return movieVideos.size() + 1;
        else return 1;
    }

    public boolean isYoutubeAppInstalled() {
        String packageNameForYoutube = "com.google.android.youtube";
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageNameForYoutube);
        if (intent != null)
            return true;
        else return false;
    }

    public static class MovieVideoViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.tv_video_name)
        TextView videoName;
        @Nullable
        @BindView(R.id.tv_video_type)
        TextView videoType;
        @Nullable
        @BindView(R.id.tv_video_source)
        TextView videoSource;
        @Nullable
        @BindView(R.id.iv_cover_photo)
        ImageView coverPhoto;
        @Nullable
        @BindView(R.id.iv_poster_photo)
        ImageView posterPhoto;
        @Nullable
        @BindView(R.id.tv_movie_title)
        TextView movieTitle;
        @Nullable
        @BindView(R.id.tv_rating_movie)
        TextView movieRating;
        @Nullable
        @BindView(R.id.release_date)
        TextView releaseDate;
        @Nullable
        @BindView(R.id.original_lang)
        TextView originalLang;
        @Nullable
        @BindView(R.id.overview)
        TextView overview;
        @Nullable
        @BindView(R.id.rating_movie)
        RatingBar movieRatingBar;
        @Nullable
        @BindView(R.id.movie_video_holder)
        RelativeLayout layoutView;

        MovieVideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

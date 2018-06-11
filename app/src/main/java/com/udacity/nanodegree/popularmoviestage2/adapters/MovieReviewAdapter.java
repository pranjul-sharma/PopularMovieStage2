package com.udacity.nanodegree.popularmoviestage2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.udacity.nanodegree.popularmoviestage2.DataUtils.MovieReview;
import com.udacity.nanodegree.popularmoviestage2.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieReviewAdapter extends ArrayAdapter<MovieReview> {
    private final Context context;
    private List<MovieReview> movieReviews;

    public MovieReviewAdapter(@NonNull Context context, @NonNull List<MovieReview> movieReviews) {
        super(context, R.layout.item_review_view, movieReviews);
        this.context = context;
        this.movieReviews = movieReviews;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MovieReviewViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new MovieReviewViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_review_view, parent, false);
            ButterKnife.bind(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MovieReviewViewHolder) convertView.getTag();
        }

        viewHolder.reviewAuthorName.setText(movieReviews.get(position).getAuthor());
        viewHolder.reviewContent.setText(Html.fromHtml(movieReviews.get(position).getContent(),Html.FROM_HTML_MODE_COMPACT));
        return convertView;
    }

    class MovieReviewViewHolder {
        @Nullable
        @BindView(R.id.tv_author_name)
        TextView reviewAuthorName;
        @Nullable
        @BindView(R.id.tv_content)
        TextView reviewContent;
    }
}

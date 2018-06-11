package com.udacity.nanodegree.popularmoviestage2.Utils;

import com.udacity.nanodegree.popularmoviestage2.DataUtils.Movie;
import com.udacity.nanodegree.popularmoviestage2.DataUtils.MovieReview;
import com.udacity.nanodegree.popularmoviestage2.DataUtils.MovieVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
 * Class for fetching movie data from json string returned by network call to tmdb server.
 */
public class MoviesDataJsonUtils {

    // method for getting list of movies from the data string.
    public static List<Movie> getMoviesFromJson(String jsonString) {
        List<Movie> movies = new ArrayList<>();

        // Various keys to fetch from JSONObject for constructing a movie object.
        final String id = "id";
        final String title = "original_title";
        final String posterPath = "poster_path";
        final String voteAverage = "vote_average";
        final String originalLang = "original_language";
        final String coverPath = "backdrop_path";
        final String overview = "overview";
        final String releaseDate = "release_date";
        final String results = "results";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        // fetching the keys from JSONObject and creating a movie object.
        try {
            JSONObject baseObject = new JSONObject(jsonString);
            JSONArray moviesArray = baseObject.getJSONArray(results);
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObj = moviesArray.getJSONObject(i);
                Movie movie = new Movie(
                        movieObj.getInt(id),
                        movieObj.getString(title),
                        movieObj.getString(posterPath),
                        movieObj.getDouble(voteAverage),
                        movieObj.getString(originalLang),
                        movieObj.getString(coverPath),
                        movieObj.getString(overview),
                        sdf.parse(movieObj.getString(releaseDate)),
                        false
                );

                // Adding newly created movie object to list of movies.
                movies.add(i, movie);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        // returning the list of movies.
        return movies;
    }

    public static List<MovieReview> getReviewsFromJson(String jsonString) {
        List<MovieReview> movieReviewsList = new ArrayList<>();
        final String id = "id";
        final String author = "author";
        final String content = "content";
        final String url = "url";
        final String results = "results";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray movieReviews = jsonObject.getJSONArray(results);

            for (int i = 0; i < movieReviews.length(); i++) {
                JSONObject movieReviewObj = movieReviews.getJSONObject(i);
                MovieReview movieReview = new MovieReview(
                        movieReviewObj.getString(id),
                        movieReviewObj.getString(author),
                        movieReviewObj.getString(content),
                        movieReviewObj.getString(url)
                );
                movieReviewsList.add(i, movieReview);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieReviewsList;
    }

    public static List<MovieVideo> getMovieVideosFromJson(String jsonString) {
        List<MovieVideo> movieVideos = new ArrayList<>();
        final String id = "id";
        final String key = "key";
        final String name = "name";
        final String site = "site";
        final String type = "type";
        final String results = "results";

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            MovieVideo movieVideo = null;
            JSONArray resultArr = jsonObject.getJSONArray(results);
            for (int i = 0; i < resultArr.length(); i++) {
                JSONObject movieVideoJson = resultArr.getJSONObject(i);
                movieVideo = new MovieVideo(
                        movieVideoJson.getString(id),
                        movieVideoJson.getString(key),
                        movieVideoJson.getString(name),
                        movieVideoJson.getString(site),
                        movieVideoJson.getString(type)
                );
                movieVideos.add(i, movieVideo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieVideos;
    }
}

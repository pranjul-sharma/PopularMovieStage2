package com.udacity.nanodegree.popularmoviestage2.Utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/*
 * Class for handling various network tasks i.e. creating URLs,
 * fetching data from server etc.
 */
public class NetworkUtils {

    // method for creating URL to fetch movie data from server.
    public static URL buildMovieUrl(String sortOrder, String api_key, String moviedb_api_key) {

        // base URL string for fetching list of movies depending upon
        // user preferences (popular, top rated) from tmdb server.
        String baseMovieUrlString = "http://api.themoviedb.org/3/movie";

        //Creating complete URI for fetching movies.
        Uri uri = Uri.parse(baseMovieUrlString).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(api_key, moviedb_api_key).build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildVideoUrl(String site, String key) {
        String baseStr = ("http://www." + site + ".com").toLowerCase();
        Uri uri = Uri.parse(baseStr).buildUpon()
                .appendPath("watch")
                .appendQueryParameter("v", key).build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // method for creating URL to fetch image from server.
    public static URL buildImgUrl(String path) {

        // Base URL string for fetching images from tmdb server.
        String baseImgUrlString = "http://image.tmdb.org/t/p/w185/";

        // building complete URI to fetch specific image from tmdb server.
        Uri uri = Uri.parse(baseImgUrlString).buildUpon()
                .appendPath(path.substring(1)).build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    //method for building url to fetch trailers and reviews
    public static URL buildMovieVideoReviewUrl(int id, String type, String api_key, String moviedb_api_key) {

        // base URL string for fetching list of movies depending upon
        // user preferences (popular, top rated) from tmdb server.
        String baseMovieUrlString = "http://api.themoviedb.org/3/movie";

        //Creating complete URI for fetching movies.
        Uri uri = Uri.parse(baseMovieUrlString).buildUpon()
                .appendPath(String.valueOf(id))
                .appendPath(type)
                .appendQueryParameter(api_key, moviedb_api_key).build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // method for fetching movie data from server.
    public static String getJsonStringFromUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasData = scanner.hasNext();
            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}

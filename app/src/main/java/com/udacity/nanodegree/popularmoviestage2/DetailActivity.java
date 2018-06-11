package com.udacity.nanodegree.popularmoviestage2;

import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.udacity.nanodegree.popularmoviestage2.DataUtils.AppDatabase;
import com.udacity.nanodegree.popularmoviestage2.DataUtils.Movie;
import com.udacity.nanodegree.popularmoviestage2.DataUtils.MovieReview;
import com.udacity.nanodegree.popularmoviestage2.DataUtils.MovieVideo;
import com.udacity.nanodegree.popularmoviestage2.Utils.AppExecutors;
import com.udacity.nanodegree.popularmoviestage2.Utils.MoviesDataJsonUtils;
import com.udacity.nanodegree.popularmoviestage2.Utils.NetworkUtils;
import com.udacity.nanodegree.popularmoviestage2.adapters.MovieDetailAdapter;
import com.udacity.nanodegree.popularmoviestage2.adapters.MovieReviewAdapter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Activity for displaying the detailed information about a movie
 * available currently.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String MOVIE_BUNDLE_KEY = "load_movie_using_saved_bundle";
    private static final String MOVIE_REVIEW_LIST_KEY = "movie_reviews_list";
    private static final String CURRENT_MOVIE_INDEX = "current_movie_index";
    private static final String REVIEW_DIALOG_SHOWN = "check_if_review_dialog_is_shown";

    @BindView(R.id.progress_bar_main)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view_movies)
    RecyclerView recyclerView;

    Movie movie;
    MovieDetailAdapter adapter;
    Menu favoriteMenu;

    Dialog dialog;
    ListView listViewReviews;
    MovieReviewAdapter movieReviewAdapter;
    List<MovieReview> movieReviewList;

    private AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_and_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        // getting data from intent to populate the UI components.
        Intent intent = getIntent();
        movie = intent.getParcelableExtra(MainActivity.PARCELABLE_KEY);
        adapter = new MovieDetailAdapter(this, movie);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
        mDb = AppDatabase.getInstance(getApplicationContext());
        setFavoriteMenu();

        if (savedInstanceState != null) {
            List<MovieVideo> movieVideos = savedInstanceState.getParcelableArrayList(MOVIE_BUNDLE_KEY);
            adapter.updateMovieVideos(movieVideos);
            recyclerView.scrollToPosition(savedInstanceState.getInt(CURRENT_MOVIE_INDEX));
            movieReviewList = savedInstanceState.getParcelableArrayList(MOVIE_REVIEW_LIST_KEY);
            if (savedInstanceState.getBoolean(REVIEW_DIALOG_SHOWN))
                getReviewDialog();
        } else if (isNetworkAvailable()) {
            FetchMovieVideosTask fetchMovieVideosTask = new FetchMovieVideosTask();
            fetchMovieVideosTask.execute(movie.getId());
            FetchMovieReviewsTask fetchMovieReviewsTask = new FetchMovieReviewsTask();
            fetchMovieReviewsTask.execute(movie.getId());
        } else
            Toast.makeText(getApplicationContext(), R.string.no_video_internet, Toast.LENGTH_SHORT).show();
    }

    // method to check if internet is available on user device.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(DetailActivity.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if (id == R.id.action_add_favorite) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().addToFavorites(movie);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.added_favorite, Toast.LENGTH_SHORT).show();
                            favoriteMenu.findItem(R.id.action_add_favorite).setVisible(false);
                            favoriteMenu.findItem(R.id.action_un_favorite).setVisible(true);
                        }
                    });

                }
            });
        } else if (id == R.id.action_un_favorite) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().deleteFromFavorites(movie);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.removed_favorite, Toast.LENGTH_SHORT).show();
                            favoriteMenu.findItem(R.id.action_add_favorite).setVisible(true);
                            favoriteMenu.findItem(R.id.action_un_favorite).setVisible(false);
                        }
                    });

                }
            });
        } else if (id == R.id.action_read_review) {
            if (movieReviewList != null && isNetworkAvailable()) {
                if (movieReviewList.size() > 0)
                    getReviewDialog();
                else
                    Toast.makeText(getApplicationContext(), R.string.no_review_available, Toast.LENGTH_SHORT).show();

            } else
                Toast.makeText(getApplicationContext(), R.string.no_review_available, Toast.LENGTH_SHORT).show();


        }
        return super.onOptionsItemSelected(item);
    }

    public void getReviewDialog() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        if (dialog == null)
            dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_listview);
        dialog.getWindow().setLayout(width, (int) (height * 0.9));
        dialog.setCancelable(true);

        listViewReviews = dialog.findViewById(R.id.listview_videos);

        movieReviewAdapter = new MovieReviewAdapter(this, movieReviewList);
        listViewReviews.setAdapter(movieReviewAdapter);
        listViewReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieReviewList.get(i).getUrl()));
                startActivity(webIntent);
            }
        });
        if (!dialog.isShowing())
            dialog.show();
        else dialog.cancel();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        favoriteMenu = menu;
        setFavoriteMenu();
        showMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    public void setFavoriteMenu() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Movie singleMovie = mDb.movieDao().getSingleMovie(movie.getId());
                if (singleMovie != null && singleMovie.getId() == movie.getId()) {
                    movie.setFavorite(true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMenu();
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    public void showMenu() {
        if (favoriteMenu != null) {
            if (movie.getFavorite()) {
                favoriteMenu.findItem(R.id.action_add_favorite).setVisible(false);
                favoriteMenu.findItem(R.id.action_un_favorite).setVisible(true);
            } else {
                favoriteMenu.findItem(R.id.action_add_favorite).setVisible(true);
                favoriteMenu.findItem(R.id.action_un_favorite).setVisible(false);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_BUNDLE_KEY, (ArrayList<MovieVideo>) adapter.getMovieVideos());
        if (dialog != null) {
            outState.putBoolean(REVIEW_DIALOG_SHOWN, dialog.isShowing());
            outState.putParcelableArrayList(MOVIE_REVIEW_LIST_KEY, (ArrayList<MovieReview>) movieReviewList);
            dialog.cancel();
        } else outState.putBoolean(REVIEW_DIALOG_SHOWN, false);
        outState.putInt(CURRENT_MOVIE_INDEX, adapter.getViewHolderPosition());
        super.onSaveInstanceState(outState);
    }

    class FetchMovieReviewsTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... args) {

            // fetching the actual movie data from tmdb server in background.
            String searchType = getResources().getString(R.string.movie_reviews_async_key);
            int id = 0;
            if (args.length != 0)
                id = args[0];
            URL url = NetworkUtils.buildMovieVideoReviewUrl(id, searchType, getString(R.string.api_key),
                    getString(R.string.moviedb_api_key));
            try {
                String jsonData = NetworkUtils.getJsonStringFromUrl(url);
                if (!jsonData.isEmpty())
                    movieReviewList = MoviesDataJsonUtils.getReviewsFromJson(jsonData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class FetchMovieVideosTask extends AsyncTask<Integer, Void, List<MovieVideo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // displaying loading indicator in case of slow internet connection,
            // it will act to show user that some process is going on and UI is not
            // blocked and has some task to process.
            if (!progressBar.isShown())
                progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected List<MovieVideo> doInBackground(Integer... args) {

            // fetching the actual movie data from tmdb server in background.
            String searchType = getResources().getString(R.string.movie_video_async_key);
            int id = 0;
            if (args.length != 0)
                id = args[0];
            URL url = NetworkUtils.buildMovieVideoReviewUrl(id, searchType, getString(R.string.api_key),
                    getString(R.string.moviedb_api_key));
            try {
                String jsonData = NetworkUtils.getJsonStringFromUrl(url);
                List<MovieVideo> movieVideos = null;
                if (!jsonData.isEmpty())
                    movieVideos = MoviesDataJsonUtils.getMovieVideosFromJson(jsonData);
                return movieVideos;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieVideo> movies) {
            super.onPostExecute(movies);

            // Once the movie data is fetched from server and
            // list of movies is fetched from this data,
            // disable indicator and show actual data.
            if (progressBar.isShown())
                progressBar.setVisibility(View.INVISIBLE);
            // Update the adapter's content to newly fetched movies.
            if (movies.size() > 0) {
                int position = adapter.getViewHolderPosition();
                adapter.updateMovieVideos(movies);
                recyclerView.scrollToPosition(position);
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_video_available, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

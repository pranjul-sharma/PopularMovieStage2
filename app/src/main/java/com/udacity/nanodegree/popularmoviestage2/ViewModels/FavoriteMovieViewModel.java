package com.udacity.nanodegree.popularmoviestage2.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.nanodegree.popularmoviestage2.DataUtils.AppDatabase;
import com.udacity.nanodegree.popularmoviestage2.DataUtils.Movie;

import java.util.List;

public class FavoriteMovieViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movies;
    public FavoriteMovieViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d("FAVORITE_VIEW_MODEL", "Actively retrieving the tasks from the DataBase");
        movies = database.movieDao().getFavoriteMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}

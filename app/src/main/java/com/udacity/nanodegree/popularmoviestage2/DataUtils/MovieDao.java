package com.udacity.nanodegree.popularmoviestage2.DataUtils;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM favorites ORDER BY id")
    LiveData<List<Movie>> getFavoriteMovies();

    @Insert
    void addToFavorites(Movie movie);

    @Delete
    void deleteFromFavorites(Movie movie);

    @Query("SELECT * FROM favorites WHERE id=:id")
    Movie getSingleMovie(int id);
}

package com.udacity.nanodegree.popularmoviestage2.DataUtils;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Class for encapsulating data fetched from server into an object form
 * so that data can be accessed efficiently and will be easy to handle.
 */
@Entity(tableName = "favorites")
public class Movie implements Parcelable {

    // Parcelable creator for creating parcels for movie class object such that
    // it will be efficiently sent from one activity to another activity.
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
    @PrimaryKey
    private int id;
    private String title;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @ColumnInfo(name = "vote_average")
    private double voteAverage;
    @ColumnInfo(name = "original_lang")
    private String originalLang;
    @ColumnInfo(name = "cover_path")
    private String coverPath;
    private String overview;
    @ColumnInfo(name = "release_date")
    private Date releaseDate;
    @ColumnInfo(name = "is_favorite")
    private Boolean isFavorite;

    //Constructor for creating and initializing movie class object from fata fetched from server.
    public Movie(int id, String title, String posterPath, double voteAverage, String originalLang, String coverPath, String overview, Date releaseDate, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.originalLang = originalLang;
        this.coverPath = coverPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.isFavorite = isFavorite;
    }

    //Constructor for creating and initializing movie class object from data which is put into parcel.
    @Ignore
    private Movie(Parcel parcel) {
        id = parcel.readInt();
        title = parcel.readString();
        posterPath = parcel.readString();
        voteAverage = parcel.readDouble();
        originalLang = parcel.readString();
        coverPath = parcel.readString();
        overview = parcel.readString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        try {
            releaseDate = sdf.parse(sdf.format(parcel.readLong()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        isFavorite = parcel.readByte() != 0;
    }

    /*
     * Various getter methods to get properties of the movie class object.
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOriginalLang() {
        return originalLang;
    }

    public void setOriginalLang(String originalLang) {
        this.originalLang = originalLang;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id: ").append(id).append("\n")
                .append("title: ").append(title).append("\n")
                .append("Poster path: ").append(posterPath).append("\n")
                .append("Vote average: ").append(voteAverage).append("\n")
                .append("Original Lang: ").append(originalLang).append("\n")
                .append("Cover path: ").append(coverPath).append("\n")
                .append("Overview: ").append(overview).append("\n")
                .append("Release Date: ").append(releaseDate).append("\n")
                .append("isFavorite: ").append(isFavorite);

        return builder.toString();
    }

    // Overridden methods from Parcelable interface.
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(posterPath);
        parcel.writeDouble(voteAverage);
        parcel.writeString(originalLang);
        parcel.writeString(coverPath);
        parcel.writeString(overview);
        parcel.writeLong(releaseDate.getTime());
        parcel.writeByte((byte) (isFavorite ? 1 : 0));
    }
}

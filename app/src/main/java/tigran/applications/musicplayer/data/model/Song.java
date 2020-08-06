package tigran.applications.musicplayer.data.model;

import android.net.Uri;

public class Song {

    private long id;
    private String title;
    private String artist;
    private Uri albumArtUri;

    public Song(long id, String title, String artist, Uri albumArtUri) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumArtUri = albumArtUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Uri getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumArtUri(Uri albumArtUri) {
        this.albumArtUri = albumArtUri;
    }
}

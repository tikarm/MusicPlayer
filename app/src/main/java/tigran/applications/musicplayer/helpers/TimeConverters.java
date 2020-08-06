package tigran.applications.musicplayer.helpers;

public class TimeConverters {

    public static int getSecondsFromSongMillis(String millis) {
        long durationInMillis = Long.parseLong(millis);
        return (int) ((durationInMillis / 1000) % 60);
    }

    public static long getMinutesFromSongMillis(String millis) {
        long durationInMillis = Long.parseLong(millis);
        return (durationInMillis / 1000) / 60;
    }
}

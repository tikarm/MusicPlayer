package tigran.applications.musicplayer.data.repository;

public class PlayerRepository {
    private static PlayerRepository instance;

    private PlayerRepository() {
    }

    public static PlayerRepository getInstance() {
        if (instance == null) {
            return new PlayerRepository();
        }
        return instance;
    }
}

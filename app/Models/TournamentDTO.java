package Models;


import java.util.Date;
import java.util.List;

/**
 * Created by arnarkari on 27/10/15.
 *
 * @author arnarkari
 */

public class TournamentDTO {
    private String tournamentName;
    private double entry_fee;
    private int maxentries;
    private Date start_time;
    private Date end_time;
    private List<GameDTO> games;
    private List<PlayerDTO> available_players;
    private double score;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public TournamentDTO(double entry_fee, int maxentries, Date start_time, Date end_time, String tournamentName, double score) {
        this.entry_fee = entry_fee;
        this.maxentries = maxentries;
        this.start_time = start_time;
        this.end_time = end_time;
        this.tournamentName = tournamentName;
        this.score = score;

    }
    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }
    public void addGame(GameDTO game) {
        games.add(game);
    }

    public double getEntry_fee() {
        return entry_fee;
    }

    public void setEntry_fee(double entry_fee) {
        this.entry_fee = entry_fee;
    }

    public int getMaxentries() {
        return maxentries;
    }

    public void setMaxentries(int maxentries) {
        this.maxentries = maxentries;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public List<GameDTO> getGames() {
        return games;
    }

    public void setGames(List<GameDTO> games) {
        this.games = games;
    }

    public List<PlayerDTO> getAvailable_players() {
        return available_players;
    }

    public void setAvailable_players(List<PlayerDTO> available_players) {
        this.available_players = available_players;
    }

}

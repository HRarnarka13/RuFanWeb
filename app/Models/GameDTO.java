package Models;

import java.util.Date;

/**
 * Created by arnarkari on 27/10/15.
 *
 * @author arnarkari
 */
public class GameDTO {

    private TeamDTO homeTeam;
    private TeamDTO awayTeam;
    private Date start_time;
    private String venue;

    public GameDTO(TeamDTO homeTeam, TeamDTO awayTeam, Date start_time, String venue) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.start_time = start_time;
        this.venue = venue;
    }

    public TeamDTO getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(TeamDTO homeTeam) {
        this.homeTeam = homeTeam;
    }

    public TeamDTO getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(TeamDTO awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}

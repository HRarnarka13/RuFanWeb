package Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnarkari on 27/10/15.
 *
 * @author arnarkari
 */
public class TeamDTO {

    private String name;
    private String abbrivation;
    private List<PlayerDTO> players;

    public TeamDTO(String name, String abbrivation) {
        this.name = name;
        this.abbrivation = abbrivation;
        players = new ArrayList<PlayerDTO>();
    }

    public TeamDTO(String name, String abbrivation, List<PlayerDTO> players) {
        this.name = name;
        this.abbrivation = abbrivation;
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbrivation() {
        return abbrivation;
    }

    public void setAbbrivation(String abbrivation) {
        this.abbrivation = abbrivation;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDTO> players) {
        this.players = players;
    }
}

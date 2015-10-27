package Models;

import is.rufan.player.domain.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnarkari on 27/10/15.
 *
 * @author arnarkari
 */
public class PlayerDTO {

    private int playerid;
    private String firstname;
    private String lastname;
    private List<Position> positions;

    public PlayerDTO(int playerid, String firstname, String lastname) {
        this.playerid = playerid;
        this.firstname = firstname;
        this.lastname = lastname;
        positions = new ArrayList<Position>();
    }

    public PlayerDTO(int playerid, String firstname, String lastname, List<Position> positions) {
        this.playerid = playerid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.positions = positions;
    }

    public int getPlayerid() {
        return playerid;
    }

    public void setPlayerid(int playerid) {
        this.playerid = playerid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
}

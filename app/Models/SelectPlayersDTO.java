package Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnarkari on 27/10/15.
 *
 * @author arnarkari
 */
public class SelectPlayersDTO {

    private List<PlayerDTO> GK;
    private List<PlayerDTO> D;
    private List<PlayerDTO> M;
    private List<PlayerDTO> F;

    public SelectPlayersDTO() {
        GK = new ArrayList<PlayerDTO>();
        D = new ArrayList<PlayerDTO>();
        M = new ArrayList<PlayerDTO>();
        F = new ArrayList<PlayerDTO>();
    }

    /**
     * Add a new goalkeeper to the list of available goalkeepers
     * @param gk the player dto for the goalkeeper
     */
    public void addGK(PlayerDTO gk) {
        GK.add(gk);
    }

    /**
     * Add a new defender to the list of available goalkeepers
     * @param d the player dto for the defender
     */
    public void addDF(PlayerDTO d) {
        D.add(d);
    }

    /**
     * Add a new defender to the list of available midfielder
     * @param m the player dto for the midfielder
     */
    public void addMF(PlayerDTO m) {
        M.add(m);
    }

    /**
     * Add a new forward to the list of availavle forwards
     * @param f the player dto for the forward
     */
    public void addFW(PlayerDTO f) {
        F.add(f);
    }


    public List<PlayerDTO> getGK() {
        return GK;
    }

    public void setGK(List<PlayerDTO> GK) {
        this.GK = GK;
    }

    public List<PlayerDTO> getD() {
        return D;
    }

    public void setD(List<PlayerDTO> d) {
        this.D = d;
    }

    public List<PlayerDTO> getM() {
        return M;
    }

    public void setM(List<PlayerDTO> m) {
        this.M = m;
    }

    public List<PlayerDTO> getF() {
        return F;
    }

    public void setF(List<PlayerDTO> f) {
        this.F = f;
    }
}

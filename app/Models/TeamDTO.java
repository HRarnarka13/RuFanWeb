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



    public TeamDTO(String name, String abbrivation) {
        this.name = name;
        this.abbrivation = abbrivation;
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
}

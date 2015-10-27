package Models;

import play.data.validation.Constraints;

/**
 * Created by arnarkari on 27/10/15.
 *
 * @author arnarkari
 */
public class FantasyTeamViewModel {

    @Constraints.Required
    public int goalkeeper;
    @Constraints.Required
    public int defender1;
    @Constraints.Required
    public int defender2;
    @Constraints.Required
    public int defender3;
    @Constraints.Required
    public int defender4;
    @Constraints.Required
    public int midfielder1;
    @Constraints.Required
    public int midfielder2;
    @Constraints.Required
    public int midfielder3;
    @Constraints.Required
    public int midfielder4;
    @Constraints.Required
    public int striker1;
    @Constraints.Required
    public int striker2;

}

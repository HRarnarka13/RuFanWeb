package controllers;

import is.rufan.player.service.PlayerService;
import is.rufan.team.service.GameService;
import is.rufan.team.service.TeamService;
import is.rufan.tournament.domain.FantasyTeam;
import is.rufan.tournament.service.FantasyTeamService;
import is.rufan.user.domain.User;
import is.rufan.user.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.myfantasyteams;

import java.util.List;

/**
 * Created by arnarkari on 27/10/15.
 *
 * @author arnarkari
 */
public class FantasyTeamController extends Controller {

    protected ApplicationContext ctx = new FileSystemXmlApplicationContext("/conf/userapp.xml");
    TeamService teamService;
    GameService gameService;
    PlayerService playerService;
    FantasyTeamService fantasyTeamService;
    UserService userService;

    public FantasyTeamController() {
        teamService = (TeamService) ctx.getBean("teamService");
        gameService = (GameService) ctx.getBean("gameService");
        playerService = (PlayerService) ctx.getBean("playerService");
        fantasyTeamService = (FantasyTeamService) ctx.getBean("fantasyTeamService");
        userService = (UserService) ctx.getBean("userService");
    }


    /**
     * This method gets a list of all active fantasy teams for a given user.
     * @return list of all active fantasy teams for a given user.
     */
    public Result getFantasyTeams() {
        User user = userService.getUserByUsername(session().get("username"));

        List<FantasyTeam> teams = fantasyTeamService.getFantasyTeamByUserId(user.getId());
        return ok(myfantasyteams.render(teams));
    }
}

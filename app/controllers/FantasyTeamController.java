package controllers;

import is.rufan.player.service.PlayerService;
import is.rufan.team.service.GameService;
import is.rufan.team.service.TeamService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.mvc.Controller;
import play.mvc.Result;

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

    public FantasyTeamController() {
        teamService = (TeamService) ctx.getBean("teamService");
        gameService = (GameService) ctx.getBean("gameService");
        playerService = (PlayerService) ctx.getBean("playerService");
    }
}

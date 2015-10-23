package controllers;

import is.rufan.team.data.TeamDataGateway;
import is.rufan.team.domain.Team;
import is.rufan.team.service.TeamService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.mvc.Controller;

import java.util.List;

/**
 * Created by arnarkari on 23/10/15.
 *
 * @author arnarkari
 */
public class TeamController extends Controller {

    protected ApplicationContext ctx = new FileSystemXmlApplicationContext("conf/userapp.xml");
    TeamService service = (TeamService) ctx.getBean("teamService");

    public List<Team> getTeams() {
        return service.getTeams();
    }
}

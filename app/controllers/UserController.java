package controllers;


import is.rufan.team.domain.Team;
import is.rufan.team.service.TeamService;
import is.rufan.user.domain.User;
import is.rufan.user.domain.UserRegistration;
import is.rufan.user.service.UserService;
import javassist.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;
import views.html.summary;

import static play.data.Form.form;

public class UserController extends Controller
{
    protected ApplicationContext ctx = new FileSystemXmlApplicationContext("/conf/userapp.xml");
    final static Form<User> loginForm = form(User.class);

    public Result blank() {

        UserService userService = (UserService) ctx.getBean("userService");
        TeamService teamService = (TeamService) ctx.getBean("teamService");

        User user = userService.getUserByUsername(session().get("username"));

        if (user == null) {

            return ok(login.render(loginForm));
        }

        Team team = teamService.getTeamByAbbrivation(user.getFav_teamabb());

        return ok(summary.render(user, team, teamService.getTeams()));
    }

}

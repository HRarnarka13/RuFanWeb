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
import views.html.index;
import views.html.login;
import views.html.profile;

import static play.data.Form.form;

public class UserController extends Controller
{
    protected ApplicationContext ctx = new FileSystemXmlApplicationContext("/conf/userapp.xml");
    final static Form<User> profileFrom = form(User.class);

    public Result blank() {

        UserService userService = (UserService) ctx.getBean("userService");
        TeamService teamService = (TeamService) ctx.getBean("teamService");

        // Get the user who is logged in.
        User user = userService.getUserByUsername(session().get("username"));

        if (user == null) {
            return ok(login.render(profileFrom));
        }

        // Get users favorite team.
        //
        Team team = teamService.getTeamByAbbrivation(user.getFav_teamabb());
        return ok(profile.render(user, team, teamService.getTeams(), profileFrom));
    }

    public Result updateUser() {

        Form<User> filledForm = profileFrom.bindFromRequest();
        UserService userService = (UserService) ctx.getBean("userService");
        TeamService teamService = (TeamService) ctx.getBean("teamService");

        String teamabb = filledForm.data().get("fav_teamabb");

        User user = userService.getUserByUsername(session().get("username"));
        User usertest = filledForm.get();
        if (user == null) {
            return ok(index.render("Hello"));
        }
        boolean updateUser = false;

        Team team = teamService.getTeamByAbbrivation(teamabb);
        if (team != null) {
            user.setFav_teamabb(team.getAbbreviation());

        } else {
            team = teamService.getTeamByAbbrivation(user.getFav_teamabb());
        }

        if((filledForm.field("credit_card_number").value().isEmpty() == false
                && filledForm.field("credit_card_exp_date_month").value().isEmpty() == false
                && filledForm.field("credit_card_exp_date_year").value().isEmpty() == false
                && filledForm.field("credit_card_type").value().isEmpty() == false)){
            if(filledForm.field("credit_card_number").value().length() != 16) {
                filledForm.reject("credit_card_number", "Credit card number has to be 16 digits");
            } else {
                user.setCredit_card_type(filledForm.data().get("credit_card_type"));
                user.setCredit_card_number(filledForm.data().get("credit_card_number"));
                String month = filledForm.data().get("credit_card_exp_date_month"); // Get the month
                String year = filledForm.data().get("credit_card_exp_date_year"); // Get the year
                user.setCredit_card_exp_date(month + "/" + year.substring(2));
                updateUser = true;
            }
        } else if(filledForm.field("credit_card_number").value().isEmpty() == false
                || filledForm.field("credit_card_exp_date_month").value().isEmpty() == false
                || filledForm.field("credit_card_exp_date_year").value().isEmpty() == false
                || filledForm.field("credit_card_type").value().isEmpty() == false) {
            filledForm.reject("credit_card_number", "Fill out all creditcard information, or leave everything empty.");
        }

        if (updateUser) {
            userService.updateUser(user.getId(), user);
        }

        return ok(profile.render(user, team, teamService.getTeams(), profileFrom));
    }

}

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


    /**
     * Get the profile page for a given user. There is both a detail information about the user and
     * a form to update the information.
     * @return the user's profile page
     */
    public Result blank() {

        UserService userService = (UserService) ctx.getBean("userService");
        TeamService teamService = (TeamService) ctx.getBean("teamService");

        // Get the user who is logged in.
        User user = userService.getUserByUsername(session().get("username"));

        if (user == null) {
            // Render the login form agian if the user is not found by this user
            return ok(login.render(profileFrom));
        }

        // Get users favorite team.
        Team team = teamService.getTeamByAbbrivation(user.getFav_teamabb());
        return ok(profile.render(user, team, teamService.getTeams(), profileFrom));
    }

    /**
     * This method is called when a user sends a post method to update his/her user information
     * @return updated profile view
     */
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
            updateUser = true;
        } else {
            team = teamService.getTeamByAbbrivation(user.getFav_teamabb());
        }

        if((filledForm.field("credit_card_number").value().isEmpty() == false
                && filledForm.field("credit_card_exp_date_month").value().isEmpty() == false
                && filledForm.field("credit_card_exp_date_year").value().isEmpty() == false
                && filledForm.field("credit_card_type").value() != null)){
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
                || filledForm.field("credit_card_type").value() != null) {
            filledForm.reject("credit_card_number", "Fill out all creditcard information, or leave everything empty.");
        }

        if (updateUser) {
            userService.updateUser(user.getId(), user);
        }

        return ok(profile.render(user, team, teamService.getTeams(), profileFrom));
    }

}

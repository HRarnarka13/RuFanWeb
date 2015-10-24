package controllers;

import is.rufan.team.domain.Team;
import is.rufan.team.service.TeamService;
import is.rufan.user.domain.UserRegistration;
import is.rufan.user.service.UserService;
import play.mvc.*;
import play.data.*;

import views.html.profile;
import views.html.signup;
import static play.data.Form.*;


public class SignupController extends AccountController {
    final static Form<UserRegistration> signupForm = form(UserRegistration.class);

    public Result blank() {
        TeamService teamService = (TeamService) ctx.getBean("teamService");
        return ok(signup.render(signupForm, teamService.getTeams()));
    }

    public Result signup() {
        Form<UserRegistration> filledForm = signupForm.bindFromRequest();


        UserService service = (UserService) ctx.getBean("userService");
        TeamService teamService = (TeamService) ctx.getBean("teamService");

        if (!"true".equals(filledForm.field("accept").value())) {
            filledForm.reject("accept", "You must accept the terms and conditions");
        }

        if(service.getUserByUsername(filledForm.field("username").value()) != null) {
            filledForm.reject("username", "Username already exists");
        }

        if (filledForm.field("username").value().length() < 4) {
            filledForm.reject("username", "Display Name must be at least 4 characters");
        }

        if (!filledForm.field("password").value().equals(filledForm.field("repeatPassword").value())) {
            filledForm.reject("repeatPassword", "The passwords you entered don't match");
        }

        if (filledForm.field("password").value().length() < 6) {
            filledForm.reject("password", "The password is too short");
        }

        if (filledForm.hasErrors()) {
            return badRequest(signup.render(filledForm, teamService.getTeams()));
        } else {
            UserRegistration created = filledForm.get();

            String month = filledForm.data().get("credit_card_exp_date_month"); // Get the month
            String year = filledForm.data().get("credit_card_exp_date_year"); // Get the year
            created.setCredit_card_exp_date(month + "/" + year.substring(2));
            service.addUser(created);

            session("username", created.getUsername());
            session("displayName", created.getName());

            // Get user favorite team
            Team fav_team = teamService.getTeamByAbbrivation(created.getFav_teamabb());
            return ok(profile.render(created, fav_team, teamService.getTeams()));
        }
    }
}

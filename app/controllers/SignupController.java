package controllers;

import is.rufan.team.domain.Team;
import is.rufan.team.service.TeamService;
import is.rufan.user.domain.User;
import is.rufan.user.domain.UserRegistration;
import is.rufan.user.service.UserService;
import play.mvc.*;
import play.data.*;

import views.html.helper.form;
import views.html.profile;
import views.html.signup;
import static play.data.Form.*;


public class SignupController extends AccountController {
    final static Form<UserRegistration> signupForm = form(UserRegistration.class);
    final static Form<User> updateForm = form(User.class);

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

        if (service.getUserByUsername(filledForm.field("username").value()) != null) {
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

        // Validate Credit card information
        String month = null;
        String year = null;
        System.out.println("4" + filledForm.field("credit_card_type").value() + " "  + filledForm.data().get("credit_card_type"));
        System.out.println("1" + filledForm.field("credit_card_number").value() + filledForm.data().get("credit_card_number"));
        System.out.println("2" + filledForm.field("credit_card_exp_date_month").value() + filledForm.data().get("credit_card_exp_date_month"));
        System.out.println("3" + filledForm.field("credit_card_exp_date_year").value()  + filledForm.data().get("credit_card_exp_date_year"));

        if((filledForm.field("credit_card_number").value().isEmpty() == false
            && filledForm.field("credit_card_exp_date_month").value().isEmpty() == false
            && filledForm.field("credit_card_exp_date_year").value().isEmpty() == false
            && filledForm.field("credit_card_type").value().isEmpty() == false)){
            if(filledForm.field("credit_card_number").value().length() != 16) {
                filledForm.reject("credit_card_number", "Credit card number has to be 16 digits");
            }
        } else if(filledForm.field("credit_card_number").value().isEmpty() == false
            || filledForm.field("credit_card_exp_date_month").value().isEmpty() == false
            || filledForm.field("credit_card_exp_date_year").value().isEmpty() == false
            || filledForm.field("credit_card_type").value().isEmpty() == false) {
            filledForm.reject("credit_card_number", "Fill out all creditcard information, or leave everything empty.");
        }

        if (filledForm.hasErrors()) {
            return badRequest(signup.render(filledForm, teamService.getTeams()));
        } else {
            UserRegistration created = filledForm.get();
            if(month != null && year != null){
                month = filledForm.data().get("credit_card_exp_date_month"); // Get the month
                year = filledForm.data().get("credit_card_exp_date_year"); // Get the year
                created.setCredit_card_exp_date(month + "/" + year.substring(2));
            }
            service.addUser(created);

            session("username", created.getUsername());
            session("displayName", created.getName());



            // Get user favorite team
            Team fav_team = teamService.getTeamByAbbrivation(created.getFav_teamabb());
            return ok(profile.render(created, fav_team, teamService.getTeams(), updateForm));
        }
    }
}

package controllers;


import is.rufan.user.data.UserNotFoundException;
import is.rufan.user.domain.User;
import is.rufan.user.service.UserService;
import play.data.*;
import play.mvc.*;

import static play.data.Form.form;

import views.html.index;
import views.html.login;

public class LoginController extends UserController
{
  final static Form<User> loginForm = form(User.class);

  public Result blank()
  {
    return ok(login.render(loginForm));
  }

  public Result login()
  {
    Form<User> filledForm = loginForm.bindFromRequest();

    UserService service = (UserService) ctx.getBean("userService");


    User user = service.getUserByUsername(filledForm.field("username").value());
    if (user == null)
    {
      filledForm.reject("password", "User not found or incorrect password entered.");
      return badRequest(login.render(filledForm));
    }

    if (!user.getPassword().equals(filledForm.field("password").value()))
    {
      filledForm.reject("password", "User not found or incorrect password entered.");
      return badRequest(login.render(filledForm));
    }

    session("username", user.getUsername());
    session("displayName", user.getName());

    if (session().get("username") != null)
    {
      return ok(index.render("Home"));
    }
    else
      return redirect("/");
  }

  public Result logout()
  {
    session().clear();
    return redirect("/");
  }
}

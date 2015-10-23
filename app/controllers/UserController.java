package controllers;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.mvc.Controller;

public class UserController extends Controller
{
  protected ApplicationContext ctx = new FileSystemXmlApplicationContext("/conf/userapp.xml");
}

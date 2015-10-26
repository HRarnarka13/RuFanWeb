package controllers;

import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.TournamentService;
import is.rufan.user.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.newtournament;
import views.html.tournaments;
import views.html.tournament;

import java.util.List;

import static play.data.Form.form;

/**
 * Created by arnarkari on 26/10/15.
 *
 * @author arnarkari
 */
public class TournamentController extends Controller {

    protected ApplicationContext ctx = new FileSystemXmlApplicationContext("/conf/userapp.xml");
    final static Form<Tournament> tournamentForm = form(Tournament.class);


    public Result getActiveTournaments() {

        TournamentService tournamentService = (TournamentService) ctx.getBean("tournamentService");

        if (tournamentService == null) {
            System.out.println("YEAH!" + tournamentService);
        }
        List<Tournament> tournamentList = tournamentService.getActiveTournaments();

        if (tournamentList.isEmpty()) {
            System.out.println("TOURNAMENT LIST EMPTY");
        }

        return ok(tournaments.render(tournamentList));
    }

    public Result getTournamentById(int tournamentid) {
        TournamentService tournamentService = (TournamentService) ctx.getBean("tournamentService");
        Tournament t = tournamentService.getTournamentById(tournamentid);
        if (t == null) {
            return notFound();
        }

        return ok(tournament.render(t));
    }

    public Result blank() {
        return ok(newtournament.render(tournamentForm));
    }

    public Result addTournament() {
        Form<Tournament> filledForm = tournamentForm.bindFromRequest();
        Tournament newTournament = filledForm.get();

        return ok(tournament.render(newTournament));
    }
}

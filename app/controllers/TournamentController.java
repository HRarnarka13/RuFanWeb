package controllers;

import Models.*;
import heplers.TournamentHelper;
import is.rufan.player.domain.Player;
import is.rufan.player.domain.Position;
import is.rufan.player.service.PlayerService;
import is.rufan.team.domain.Game;
import is.rufan.team.domain.Team;
import is.rufan.team.service.GameService;
import is.rufan.team.service.TeamService;
import is.rufan.tournament.domain.FantasyTeam;
import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.domain.TournamentEnrollment;
import is.rufan.tournament.service.FantasyTeamService;
import is.rufan.tournament.service.TournamentService;
import is.rufan.user.domain.User;
import is.rufan.user.service.UserService;
import org.apache.commons.lang3.time.DateUtils;
import org.h2.mvstore.DataUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.newtournament;
import views.html.tournaments;
import views.html.tournament;

import java.util.*;

import static play.data.Form.form;

/**
 * Created by arnarkari on 26/10/15.
 *
 * @author arnarkari
 */
public class TournamentController extends Controller {

    protected ApplicationContext ctx = new FileSystemXmlApplicationContext("/conf/userapp.xml");
    final static Form<Tournament> tournamentForm = form(Tournament.class);
    final static Form<FantasyTeamViewModel> fantasyTeamForm = form(FantasyTeamViewModel.class);
    private TournamentService tournamentService;
    TeamService teamService;
    GameService gameService;
    UserService userService;
    PlayerService playerService;
    FantasyTeamService fantasyTeamService;

    public TournamentController() {
        tournamentService = (TournamentService) ctx.getBean("tournamentService");
        teamService = (TeamService) ctx.getBean("teamService");
        gameService = (GameService) ctx.getBean("gameService");
        userService = (UserService) ctx.getBean("userService");
        playerService = (PlayerService) ctx.getBean("playerService");
        fantasyTeamService = (FantasyTeamService) ctx.getBean("fantasyTeamService");
    }

    public Result getActiveTournaments() {
        List<Tournament> tournamentList = tournamentService.getActiveTournaments();
        return ok(tournaments.render(tournamentList));
    }

    public Result getTournamentById(int tournamentid) {

        Tournament t = tournamentService.getTournamentById(tournamentid);
        if (t == null) {
            return notFound();
        }
        TournamentDTO tournamentDTO = new TournamentDTO(t.getEntryFee(), t.getMaxEntries(), t.getStartTime(), t.getEndTime());;

        List<Game> games = new ArrayList<Game>();
        for (Integer id : tournamentService.getTournamentGames(tournamentid)) {
            games.add(gameService.getGame(id));
        }

        User user = userService.getUserByUsername(session().get("username"));
        if (user == null) {
            return redirect(routes.LoginController.blank());
        }

        // Get the fantasy team if the user has already created a fantasy team for the current tournament
        List<PlayerDTO> fantasy_players = new ArrayList<PlayerDTO>();
        for (TournamentEnrollment te : t.getEnrollments()) {
            // Get the fantasy team for each enrollment
            FantasyTeam ft = fantasyTeamService.getFantasyTeam(te.getTeamId());
            /// check if the fantasy team belongs to the current user
            if (ft.getUserId() == user.getId()) {
                for (Integer fantasy_playerid : ft.getPlayers()) {
                    Player fantasy_player = playerService.getPlayer(fantasy_playerid);
                    fantasy_player.setPositions(new ArrayList<Position>(playerService.getPlayerPosition(fantasy_playerid)));
                    Team team = teamService.getTeamById(fantasy_player.getTeamId());

                    // Create DTO objects
                    TeamDTO teamDTO = new TeamDTO(team.getDisplayName(), team.getAbbreviation());
                    PlayerDTO playerDTO = new PlayerDTO(fantasy_playerid, fantasy_player.getFirstName(),
                            fantasy_player.getLastName(), teamDTO, fantasy_player.getPositions());
                    fantasy_players.add(playerDTO);
                }
            }
        }
        // If the user has not created a fantasy team for the tournament render a view to create a new fantasy team for
        // the current tournament
        if (fantasy_players.isEmpty()) {
            SelectPlayersDTO available_players = new TournamentHelper().getAvailablePlayers(tournamentid);
            return ok(tournament.render(t, games, null, available_players, fantasyTeamForm));
        }

        return ok(tournament.render(t, games, fantasy_players, null, fantasyTeamForm));

    }

    /**
     * Renders a form to create a new tournament.
     * @return a create new tournament form.
     */
    public Result blank() {
        List<Game> games = gameService.getGames();
        return ok(newtournament.render(tournamentForm, games));
    }

    /**
     * This method gets called when a operator creates
     * @return
     */
    public Result addTournament() {
        Form<Tournament> filledForm = tournamentForm.bindFromRequest();

        // Get the tournament details from the form
        String name, entry_fee, maxentries;
        name = filledForm.data().get("name");
        entry_fee = filledForm.data().get("entry_fee");
        maxentries = filledForm.data().get("maxentries");

        // Get the games that the operator has picked for the tournament
        List<Integer> selected_gameids = new ArrayList<Integer>();
        int i = 0;
        String key = "tournamentGames[" + i + "]";
        while(filledForm.data().get(key) != null){
            int gameid = Integer.parseInt(filledForm.data().get(key));
            selected_gameids.add(gameid);
            i++;
            key = "tournamentGames[" + i + "]";
        }

        // Validate form
        if (name == "") {
            filledForm.reject("name", "Please provide a name for the tournament");
        }
        if (entry_fee == "") {
            filledForm.reject("entry_fee", "Please fill out the entry fee for the tournament");
        }
        if (maxentries == "") {
            filledForm.reject("maxentries", "Please provide max entries for the tournament");
        }
        if (selected_gameids.isEmpty()) {
            filledForm.reject("tournamentGames[]", "Please select games for the tournament");
        }

        if (filledForm.hasErrors()) {
            List<Game> games = gameService.getGames();
            return badRequest(newtournament.render(tournamentForm, games));
        } else {

            // Get the new tournament information
            Tournament newTournament = filledForm.get();
            newTournament.setEntryFee(Double.parseDouble(entry_fee));
            newTournament.setMaxEntries(Integer.parseInt(maxentries));
            newTournament.setStatus(true);

            // Find the actual games the operator has selected and get the start time and end time for the tournament
            // based on the games.
            List<Game> games = new ArrayList<Game>();
            Date first_game_date = null, last_game_date = null;
            for(Integer gameid : selected_gameids) {
                Game game = gameService.getGame(gameid);
                if (game != null) {
                    Date game_date = game.getStartTime();
                    if (first_game_date == null || first_game_date.after(game_date)) {
                        first_game_date = game_date;
                    }
                    if (last_game_date == null || last_game_date.before(game_date) ) {
                        last_game_date = game_date;
                    }
                    newTournament.setTournamentGames(selected_gameids);
                    games.add(game);
                }
            }
            // Update the start and end time
            newTournament.setStartTime(first_game_date);
            newTournament.setEndTime(DateUtils.addHours(last_game_date, 2));
            int tournamentid = tournamentService.addTournament(newTournament);

            // Get list of abailable players for the current tournament
            SelectPlayersDTO available_players = new TournamentHelper().getAvailablePlayers(tournamentid);

            return ok(tournament.render(newTournament, games, null, available_players, fantasyTeamForm));
        }
    }

    /**
     * This method enrolls a user in a give tournament, the user provides his fantasy team that he/she has selected.
     * @param tournamentid the id of the tournament
     * @return
     */
    public Result enroll(int tournamentid) {
        Form<FantasyTeamViewModel> filledForm = fantasyTeamForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return redirect(routes.TournamentController.getTournamentById(tournamentid));
        } else {
            FantasyTeamViewModel fantasyTeamForm = filledForm.get();
            List<Integer> fantasyTeamPlayers = new ArrayList<Integer>();

            // Check if there are duplicates in the list
            Set<Integer> set = new HashSet<Integer>(fantasyTeamPlayers);
            if(set.size() < fantasyTeamPlayers.size()){
                Tournament tournament = tournamentService.getTournamentById(tournamentid);

                // return badRequest(tournament.render(tournamentService.getTournamentById(tournamentid), ))
            }


            // region add every fantasy team player to a list
            fantasyTeamPlayers.add(fantasyTeamForm.goalkeeper);
            fantasyTeamPlayers.add(fantasyTeamForm.defender1);
            fantasyTeamPlayers.add(fantasyTeamForm.defender2);
            fantasyTeamPlayers.add(fantasyTeamForm.defender3);
            fantasyTeamPlayers.add(fantasyTeamForm.defender4);
            fantasyTeamPlayers.add(fantasyTeamForm.midfielder1);
            fantasyTeamPlayers.add(fantasyTeamForm.midfielder2);
            fantasyTeamPlayers.add(fantasyTeamForm.midfielder3);
            fantasyTeamPlayers.add(fantasyTeamForm.midfielder4);
            fantasyTeamPlayers.add(fantasyTeamForm.striker1);
            fantasyTeamPlayers.add(fantasyTeamForm.striker2);
            // endregion

            User user = userService.getUserByUsername(session().get("username"));
            if (user == null) {
                return redirect(routes.LoginController.blank());
            }

            int fantasy_teamid = fantasyTeamService.addFantasyTeam(user.getId(), fantasyTeamPlayers);
            tournamentService.addEnrollment(tournamentid, fantasy_teamid);
            return redirect(routes.TournamentController.getTournamentById(tournamentid));
        }
    }
}

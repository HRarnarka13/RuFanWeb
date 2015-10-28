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
import is.rufan.tournament.domain.*;
import is.rufan.tournament.service.FantasyPlayer.FantasyPlayerService;
import is.rufan.tournament.service.FantasyPoint.FantasyPointService;
import is.rufan.tournament.service.FantasyTeam.FantasyTeamService;
import is.rufan.tournament.service.Tournament.TournamentService;
import is.rufan.user.domain.User;
import is.rufan.user.service.UserService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.newtournament;
import views.html.tournamentOver;
import views.html.tournaments;
import views.html.tournamentDetails;

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
    FantasyPlayerService fantasyPlayerService;
    FantasyPointService fantasyPointService;

    public TournamentController() {
        tournamentService = (TournamentService) ctx.getBean("tournamentService");
        teamService = (TeamService) ctx.getBean("teamService");
        gameService = (GameService) ctx.getBean("gameService");
        userService = (UserService) ctx.getBean("userService");
        playerService = (PlayerService) ctx.getBean("playerService");
        fantasyTeamService = (FantasyTeamService) ctx.getBean("fantasyTeamService");
        fantasyPlayerService = (FantasyPlayerService) ctx.getBean("fantasyPlayerService");
        fantasyPointService = (FantasyPointService) ctx.getBean("fantasyPointService");
    }

    /**
     * Returns a list of all active tournaments in the system
     * @return all active tournaments
     */
    public Result getActiveTournaments() {
        List<Tournament> tournamentList = tournamentService.getActiveTournaments();
        return ok(tournaments.render(tournamentList));
    }

    /**
     * Renders a form to create a new tournamentDetails.
     * @return a create new tournamentDetails form.
     */
    public Result blank() {
        List<Game> games = gameService.getGames();
        return ok(newtournament.render(tournamentForm, games));
    }

    /**
     * Gets a specific tournamentDetails
     * @param tournamentid the tournamentDetails to be returned
     * @return a view containing details about the tournamentDetails
     */
    public Result getTournamentById(int tournamentid) {

        Tournament t = tournamentService.getTournamentById(tournamentid);
        if (t == null) {
            return notFound();
        }
        TournamentDTO tournamentDTO = new TournamentDTO(t.getEntryFee(), t.getMaxEntries(), t.getStartTime(), t.getEndTime(), t.getName(), 0);

        List<Game> games = new ArrayList<Game>();
        for (Integer id : tournamentService.getTournamentGames(tournamentid)) {
            games.add(gameService.getGame(id));
        }

        User user = userService.getUserByUsername(session().get("username"));
        if (user == null) {
            return redirect(routes.LoginController.blank());
        }

        List<PlayerDTO> fantasy_players = new ArrayList<PlayerDTO>();
        if (t.getMaxEntries() <= t.getEnrollments().size()) {
           return ok(tournamentDetails.render(t, games, fantasy_players, null, fantasyTeamForm));
        }
        // Get the fantasy team if the user has already created a fantasy team for the current tournamentDetails
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
        // If the user has not created a fantasy team for the tournamentDetails render a view to create a new fantasy team for
        // the current tournamentDetails
        if (fantasy_players.isEmpty()) {
            SelectPlayersDTO available_players = new TournamentHelper().getAvailablePlayers(tournamentid);
            return ok(tournamentDetails.render(t, games, null, available_players, fantasyTeamForm));
        }

        return ok(tournamentDetails.render(t, games, fantasy_players, null, fantasyTeamForm));
    }

    /**
     * This method gets called when a operator creates
     * @return
     */
    public Result addTournament() {
        Form<Tournament> filledForm = tournamentForm.bindFromRequest();

        // Get the tournamentDetails details from the form
        String name, entry_fee, maxentries;
        name = filledForm.data().get("name");
        entry_fee = filledForm.data().get("entry_fee");
        maxentries = filledForm.data().get("maxentries");

        // Get the games that the operator has picked for the tournamentDetails
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
            filledForm.reject("name", "Please provide a name for the tournamentDetails");
        }
        if (entry_fee == "") {
            filledForm.reject("entry_fee", "Please fill out the entry fee for the tournamentDetails");
        }
        if (maxentries == "") {
            filledForm.reject("maxentries", "Please provide max entries for the tournamentDetails");
        }
        if (selected_gameids.isEmpty()) {
            filledForm.reject("tournamentGames[]", "Please select games for the tournamentDetails");
        }

        if (filledForm.hasErrors()) {
            List<Game> games = gameService.getGames();
            return badRequest(newtournament.render(tournamentForm, games));
        } else {

            // Get the new tournamentDetails information
            Tournament newTournament = filledForm.get();
            newTournament.setEntryFee(Double.parseDouble(entry_fee));
            newTournament.setMaxEntries(Integer.parseInt(maxentries));
            newTournament.setStatus(true);

            // Find the actual games the operator has selected and get the start time and end time for the tournamentDetails
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

            if (newTournament.getMaxEntries() == 0) {
                List<PlayerDTO> fantasy_players = new ArrayList<PlayerDTO>();
                return ok(tournaments.render(tournamentService.getActiveTournaments()));
            }

            // Get list of abailable players for the current tournamentDetails
            SelectPlayersDTO available_players = new TournamentHelper().getAvailablePlayers(tournamentid);

            return ok(tournaments.render(tournamentService.getActiveTournaments()));
        }
    }

    /**
     * This method enrolls a user in a give tournamentDetails, the user provides his fantasy team that he/she has selected.
     * @param tournamentid the id of the tournamentDetails
     * @return a view containing the enrolled team
     */
    public Result enroll(int tournamentid) {
        Form<FantasyTeamViewModel> filledForm = fantasyTeamForm.bindFromRequest();

        Tournament tournament = tournamentService.getTournamentById(tournamentid);
        List<Game> games = new ArrayList<Game>();
        for (Integer gameid : tournamentService.getTournamentGames(tournamentid)) {
            games.add(gameService.getGame(gameid));
        }
        SelectPlayersDTO available_players = new TournamentHelper().getAvailablePlayers(tournamentid);
        if (filledForm.hasErrors()) {
            filledForm.reject("Bad request");
            return badRequest(views.html.tournamentDetails.render(tournament, games, null, available_players,
                    fantasyTeamForm));
        } else {
            FantasyTeamViewModel ftForm = filledForm.get();
            List<Integer> fantasyTeamPlayers = new ArrayList<Integer>();
            // Check if there are duplicates in the list
            Set<Integer> set = new HashSet<Integer>(fantasyTeamPlayers);
            if(set.size() < fantasyTeamPlayers.size()){
                return badRequest(views.html.tournamentDetails.render(tournament, games, null, available_players,
                        fantasyTeamForm));
            }

            // region add every fantasy team player to a list
            fantasyTeamPlayers.add(ftForm.goalkeeper);
            fantasyTeamPlayers.add(ftForm.defender1);
            fantasyTeamPlayers.add(ftForm.defender2);
            fantasyTeamPlayers.add(ftForm.defender3);
            fantasyTeamPlayers.add(ftForm.defender4);
            fantasyTeamPlayers.add(ftForm.midfielder1);
            fantasyTeamPlayers.add(ftForm.midfielder2);
            fantasyTeamPlayers.add(ftForm.midfielder3);
            fantasyTeamPlayers.add(ftForm.midfielder4);
            fantasyTeamPlayers.add(ftForm.striker1);
            fantasyTeamPlayers.add(ftForm.striker2);
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

    public Result calculatePointsForTournament(int tournamentid) {

        Tournament tournament = tournamentService.getTournamentById(tournamentid); // Get the tournament
        // Loop through every entollment in the tournament

        TournamentEnrollment winner = new TournamentEnrollment(0,0, 0.0);

        for (TournamentEnrollment enrollment : tournament.getEnrollments()) {
            FantasyTeam fantasyTeam = fantasyTeamService.getFantasyTeam(enrollment.getTeamId());
            if (fantasyTeam.isOpen()) {
                // Get the a list of the fantasy players in the current fantasy team
                List<FantasyPlayer> fantasy_players = fantasyPlayerService
                        .getFantasyPlayersByTeamId(fantasyTeam.getFantasyTeamId());

                double score = 0.0;
                // For each player get his score and sum the scores up
                for (FantasyPlayer fantasyPlayer : fantasy_players) {
                    FantasyPoint fantasyPoint = fantasyPointService
                            .getFantasyPointByPlayerId(fantasyPlayer.getPlayerid());
                    if (fantasyPoint != null) {
                        score += fantasyPoint.getFantasyPoints();
                    }
                }
                enrollment.setScore(score); // update the score
                tournamentService.setScore(tournamentid, fantasyTeam.getFantasyTeamId(), score);

                if (winner.getScore() < score) {
                    winner = enrollment; // update the winner
                }

                fantasyTeam.setIsOpen(false); // close the fantasy team
            }
            tournamentService.closeTournament(tournamentid); // close the tournament
        }

        // If we did not findd a winner
        if (winner.getScore() == 0) {
            return ok();
        }

        // Find the user.
        FantasyTeam bestTeam = fantasyTeamService.getFantasyTeam(winner.getTeamId());
        User user = userService.getUser(bestTeam.getUserId());
        if (user == null) {
            return badRequest();
        }

        // return the user who won, his score and number of participants.
        return ok(tournamentOver.render(tournament, user, winner.getScore(), tournament.getEnrollments().size()));
    }
}

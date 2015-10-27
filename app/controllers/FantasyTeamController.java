package controllers;

import Models.FantasyTeamViewModel;
import Models.PlayerDTO;
import Models.TeamDTO;
import Models.TournamentDTO;
import is.rufan.player.domain.Player;
import is.rufan.player.service.PlayerService;
import is.rufan.team.domain.Team;
import is.rufan.team.service.GameService;
import is.rufan.team.service.TeamService;
import is.rufan.tournament.domain.FantasyPlayer;
import is.rufan.tournament.domain.FantasyTeam;
import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.FantasyPlayerService;
import is.rufan.tournament.service.FantasyTeamService;
import is.rufan.tournament.service.TournamentService;
import is.rufan.user.domain.User;
import is.rufan.user.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.myfantasyteams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnarkari on 27/10/15.
 *
 * @author arnarkari
 */
public class FantasyTeamController extends Controller {

    protected ApplicationContext ctx = new FileSystemXmlApplicationContext("/conf/userapp.xml");
    TeamService teamService;
    GameService gameService;
    PlayerService playerService;
    FantasyTeamService fantasyTeamService;
    FantasyPlayerService fantasyPlayerService;
    TournamentService tournamentService;
    UserService userService;

    public FantasyTeamController() {
        teamService = (TeamService) ctx.getBean("teamService");
        gameService = (GameService) ctx.getBean("gameService");
        playerService = (PlayerService) ctx.getBean("playerService");
        fantasyTeamService = (FantasyTeamService) ctx.getBean("fantasyTeamService");
        fantasyPlayerService = (FantasyPlayerService) ctx.getBean("fantasyPlayerService");
        tournamentService = (TournamentService) ctx.getBean("tournamentService");
        userService = (UserService) ctx.getBean("userService");
    }


    /**
     * This method gets a list of all active fantasy teams for a given user.
     * @return list of all active fantasy teams for a given user.
     */
    public Result getFantasyTeams() {
        User user = userService.getUserByUsername(session().get("username"));
        List<FantasyTeam> teams = fantasyTeamService.getFantasyTeamByUserId(user.getId());

        List<TournamentDTO> tournaments = new ArrayList<TournamentDTO>();
        for(Tournament tournament : tournamentService.getActiveTournaments()) {
            List<FantasyTeam> fantasyTeams = tournamentService.getFantasyTeamsByTournamentId(tournament.getTournamentid());
            for (FantasyTeam t : fantasyTeams) {
                if(t.getUserId() == user.getId()){
                    TournamentDTO tournamentDTO = new TournamentDTO(tournament.getEntryFee(), tournament.getMaxEntries(), tournament.getStartTime(), tournament.getEndTime());
                    List<FantasyPlayer> teamPlayers = fantasyPlayerService.getFantasyPlayersByTeamId(t.getFantasyTeamId());
                    List<PlayerDTO> players = new ArrayList<PlayerDTO>();
                    for(FantasyPlayer fp : teamPlayers){
                        Player player = playerService.getPlayer(fp.getPlayerid());
                        Team team = teamService.getTeamById(t.getFantasyTeamId());
                        TeamDTO teamDTO = new TeamDTO(team.getDisplayName(), team.getAbbreviation());
                        PlayerDTO playerDTO = new PlayerDTO(player.getPlayerId(), player.getFirstName(), player.getLastName(), teamDTO);
                        players.add(playerDTO);
                    }
                    tournamentDTO.setAvailable_players(players);
                    tournaments.add(tournamentDTO);
                }
            }
        }
        return ok(myfantasyteams.render(tournaments));
    }
}

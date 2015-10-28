package controllers;

import Models.FantasyTeamViewModel;
import Models.PlayerDTO;
import Models.TeamDTO;
import Models.TournamentDTO;
import is.rufan.player.domain.Player;
import is.rufan.player.domain.Position;
import is.rufan.tournament.domain.TournamentEnrollment;
import is.rufan.tournament.service.FantasyPoint.FantasyPointService;
import is.rufan.player.service.PlayerService;
import is.rufan.team.domain.Team;
import is.rufan.team.service.GameService;
import is.rufan.team.service.TeamService;
import is.rufan.tournament.domain.FantasyPlayer;
import is.rufan.tournament.domain.FantasyTeam;
import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.FantasyPlayer.FantasyPlayerService;
import is.rufan.tournament.service.FantasyTeam.FantasyTeamService;
import is.rufan.tournament.service.Tournament.TournamentService;
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
    FantasyPointService fantasyPointService;


    public FantasyTeamController() {
        teamService = (TeamService) ctx.getBean("teamService");
        gameService = (GameService) ctx.getBean("gameService");
        playerService = (PlayerService) ctx.getBean("playerService");
        fantasyTeamService = (FantasyTeamService) ctx.getBean("fantasyTeamService");
        fantasyPlayerService = (FantasyPlayerService) ctx.getBean("fantasyPlayerService");
        tournamentService = (TournamentService) ctx.getBean("tournamentService");
        userService = (UserService) ctx.getBean("userService");
        fantasyPointService = (FantasyPointService) ctx.getBean("fantasyPointService");
    }


    /**
     * This method gets a list of all active fantasy teams for a given user.
     * @return list of all active fantasy teams for a given user.
     */
    public Result getFantasyTeams() {
        User user = userService.getUserByUsername(session().get("username"));
        List<FantasyTeam> teams = fantasyTeamService.getFantasyTeamByUserId(user.getId());
        List<TournamentDTO> tournaments = new ArrayList<TournamentDTO>();
        for(Tournament tournament : tournamentService.getTournaments()) {
            List<FantasyTeam> fantasyTeams = tournamentService.getFantasyTeamsByTournamentId(tournament.getTournamentid());
            for (FantasyTeam t : fantasyTeams) {
                if(t.getUserId() == user.getId()){
                    TournamentDTO tournamentDTO = new TournamentDTO(tournament.getEntryFee(), tournament.getMaxEntries(), tournament.getStartTime(), tournament.getEndTime(), tournament.getName(), 0);
                    List<FantasyPlayer> teamPlayers = fantasyPlayerService.getFantasyPlayersByTeamId(t.getFantasyTeamId());
                    List<PlayerDTO> players = new ArrayList<PlayerDTO>();
                    for(FantasyPlayer fp : teamPlayers){
                        Player player = playerService.getPlayer(fp.getPlayerid());
                        player.setPositions(new ArrayList<Position>(playerService.getPlayerPosition(fp.getPlayerid())));
                        Team team = teamService.getTeamById(player.getTeamId());
                        TeamDTO teamDTO = new TeamDTO(team.getDisplayName(), team.getAbbreviation());
                        PlayerDTO playerDTO = new PlayerDTO(player.getPlayerId(), player.getFirstName(), player.getLastName(), teamDTO, player.getPositions());
                        players.add(playerDTO);
                    }
                    tournamentDTO.setAvailable_players(players);
                    for(TournamentEnrollment te : tournament.getEnrollments()) {
                        if (te.getTeamId() == t.getFantasyTeamId()) {
                            tournamentDTO.setScore(te.getScore());
                        }
                    }
                    tournaments.add(tournamentDTO);
                }
            }
        }
        return ok(myfantasyteams.render(tournaments));
    }
}

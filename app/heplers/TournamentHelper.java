package heplers;

import Models.GameDTO;
import Models.PlayerDTO;
import Models.SelectPlayersDTO;
import Models.TeamDTO;
import is.rufan.player.domain.Player;
import is.rufan.player.domain.Position;
import is.rufan.player.service.PlayerService;
import is.rufan.team.domain.Game;
import is.rufan.team.service.GameService;
import is.rufan.team.service.TeamService;
import is.rufan.tournament.domain.Tournament;
import is.rufan.tournament.service.FantasyTeamService;
import is.rufan.tournament.service.TournamentService;
import is.rufan.user.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.data.Form;

import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

/**
 * Created by arnarkari on 27/10/15.
 *
 * @author arnarkari
 */
public class TournamentHelper {

    protected ApplicationContext ctx = new FileSystemXmlApplicationContext("/conf/userapp.xml");
    final static Form<Tournament> tournamentForm = form(Tournament.class);
    private TournamentService tournamentService;
    TeamService teamService;
    GameService gameService;
    UserService userService;
    PlayerService playerService;
    FantasyTeamService fantasyTeamService;

    public TournamentHelper() {
        tournamentService = (TournamentService) ctx.getBean("tournamentService");
        teamService = (TeamService) ctx.getBean("teamService");
        gameService = (GameService) ctx.getBean("gameService");
        userService = (UserService) ctx.getBean("userService");
        playerService = (PlayerService) ctx.getBean("playerService");
        fantasyTeamService = (FantasyTeamService) ctx.getBean("fantasyTeamService");
    }

    public SelectPlayersDTO getAvailablePlayers(int tournamentid) {
        SelectPlayersDTO available_players = new SelectPlayersDTO();

        Tournament t = tournamentService.getTournamentById(tournamentid);

        List<Game> games = new ArrayList<Game>();
        for (Integer id : tournamentService.getTournamentGames(tournamentid)) {
            games.add(gameService.getGame(id));
        }

        for(Game game : games) {
            for (Player player : playerService.getPlayersByTeamId(0, game.getTeamHome().getTeamId())) {
                player.setPositions(new ArrayList<Position>(playerService.getPlayerPosition(player.getPlayerId())));
                TeamDTO teamDTO = new TeamDTO(game.getTeamHome().getDisplayName(), game.getTeamHome().getAbbreviation());
                PlayerDTO p = new PlayerDTO(player.getPlayerId(), player.getFirstName(), player.getLastName(), teamDTO,
                        player.getPositions());
                for (Position pos : p.getPositions()) {
                    if (pos.getAbbreviation().equals("GK")) {
                        available_players.addGK(p);
                    } else if (pos.getAbbreviation().equals("D")) {
                        available_players.addDF(p);
                    } else if (pos.getAbbreviation().equals("M")) {
                        available_players.addMF(p);
                    } else if (pos.getAbbreviation().equals("F")) {
                        available_players.addFW(p);
                    }
                }
            }
            for (Player player : playerService.getPlayersByTeamId(0, game.getTeamAway().getTeamId())) {
                player.setPositions(new ArrayList<Position>(playerService.getPlayerPosition(player.getPlayerId())));
                TeamDTO teamDTO = new TeamDTO(game.getTeamAway().getDisplayName(), game.getTeamAway().getAbbreviation());
                PlayerDTO p = new PlayerDTO(player.getPlayerId(), player.getFirstName(), player.getLastName(), teamDTO,
                        player.getPositions());
                for (Position pos : p.getPositions()) {
                    if (pos.getAbbreviation().equals("GK")) {
                        available_players.addGK(p);
                    } else if (pos.getAbbreviation().equals("D")) {
                        available_players.addDF(p);
                    } else if (pos.getAbbreviation().equals("M")) {
                        available_players.addMF(p);
                    } else if (pos.getAbbreviation().equals("F")) {
                        available_players.addFW(p);
                    }
                }
            }
        }
        return available_players;
    }
}

package com.tourneynizer.tourneynizer.controller;

import com.tourneynizer.tourneynizer.error.BadRequestException;
import com.tourneynizer.tourneynizer.error.InternalErrorException;
import com.tourneynizer.tourneynizer.model.ErrorMessage;
import com.tourneynizer.tourneynizer.model.Team;
import com.tourneynizer.tourneynizer.model.Tournament;
import com.tourneynizer.tourneynizer.model.User;
import com.tourneynizer.tourneynizer.service.SessionService;
import com.tourneynizer.tourneynizer.service.TeamService;
import com.tourneynizer.tourneynizer.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller("TeamController")
public class TeamController {

    private final TeamService teamService;
    private final TournamentService tournamentService;
    private final SessionService sessionService;

    @Autowired
    public TeamController(TeamService teamService, TournamentService tournamentService, SessionService sessionService) {
        this.teamService = teamService;
        this.tournamentService = tournamentService;
        this.sessionService = sessionService;
    }

    @PostMapping("/api/tournament/{id}/team/create")
    public ResponseEntity<?> findById(@PathVariable("id") long id,
                                      @CookieValue("session") String session,
                                      @RequestBody Map<String, String> values) {
        try {
            Tournament tournament = tournamentService.findById(id);
            User user = sessionService.findBySession(session);
            Team team = teamService.createTeam(user, tournament, values);

            return new ResponseEntity<>(team, new HttpHeaders(), HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<Object>(new ErrorMessage(e), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        } catch (InternalErrorException e) {
            return new ResponseEntity<Object>(new ErrorMessage(e), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

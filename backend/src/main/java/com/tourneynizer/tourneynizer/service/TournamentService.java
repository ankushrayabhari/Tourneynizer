package com.tourneynizer.tourneynizer.service;

import com.tourneynizer.tourneynizer.dao.MatchDao;
import com.tourneynizer.tourneynizer.dao.TeamDao;
import com.tourneynizer.tourneynizer.dao.TournamentDao;
import com.tourneynizer.tourneynizer.error.BadRequestException;
import com.tourneynizer.tourneynizer.error.InternalErrorException;
import com.tourneynizer.tourneynizer.model.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class TournamentService {
    private final TournamentDao tournamentDao;
    private final TeamDao teamDao;
    private final MatchGenerator matchGenerator;

    public TournamentService(TournamentDao tournamentDao, TeamDao teamDao, MatchDao matchDao) {
        this.tournamentDao = tournamentDao;
        this.teamDao = teamDao;
        this.matchGenerator = new MatchGenerator(matchDao);
    }

    public Tournament createTournament(Map<String, String> values, User user) throws BadRequestException, InternalErrorException {
        Tournament tournament;
        try {
            tournament = new Tournament(
                    values.get("name"),
                    Double.parseDouble(values.get("lat")),
                    Double.parseDouble(values.get("lng")),
                    new Timestamp(Long.parseLong(values.get("startTime"))),
                    Integer.parseInt(values.get("teamSize")),
                    Integer.parseInt(values.get("maxTeams")),
                    TournamentType.valueOf(values.get("type")),
                    user.getId(),
                    TournamentStatus.CREATED
            );
        }
        catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            throw new BadRequestException(e.getMessage(), e);
        }

        try {
            tournamentDao.insert(tournament, user);
        } catch (SQLException e) {
            throw new InternalErrorException(e);
        }

        return tournament;
    }

    public List<Tournament> getAll() throws InternalErrorException {
        try {
            return tournamentDao.getAll();
        } catch (SQLException e ) {
            throw new InternalErrorException(e);
        }
    }

    public Tournament findById(Long id) throws BadRequestException, InternalErrorException {
        Tournament tournament;
        try {
            tournament = tournamentDao.findById(id);
        } catch (SQLException e) {
            throw new InternalErrorException(e);
        }

        if (tournament == null) {
            throw new BadRequestException("Couldn't find tournament with id " + id);
        }

        return tournament;
    }

    public List<Tournament> ownedBy(User user) throws InternalErrorException{
        try {
            return tournamentDao.ownedBy(user);
        }
        catch (SQLException e) {
            throw new InternalErrorException(e);
        }
    }

    public void startTournament(long id, User user) throws InternalErrorException, BadRequestException {
        Tournament tournament;
        try { tournament = tournamentDao.findById(id); }
        catch (SQLException e) { throw new InternalErrorException(e); }

        if (tournament == null) { throw new BadRequestException("Couldn't find tournament with id " + id); }

        if (tournament.getCreatorId() != user.getId()) {
            throw new BadRequestException("You are not the creator of that tournament");
        }


        try { tournamentDao.startTournament(tournament); }
        catch (IllegalArgumentException e) { throw new BadRequestException(e.getMessage()); }

        List<Team> teams = teamDao.findByTournament(tournament, true);
        try { matchGenerator.createTournamentMatches(teams, user, tournament); }
        catch (SQLException e) { throw new InternalErrorException(e); }
    }
}

//
//  TeamRequestService.swift
//  tournenizer
//
//  Created by Ankush Rayabhari on 3/9/18.
//  Copyright © 2018 Ankush Rayabhari. All rights reserved.
//

import Foundation;

class TeamRequestService : Service {
    static let shared = TeamRequestService();

    func getRequestsForCurrentUser(cb: @escaping ((String?, [TeamRequest]?, [Tournament]?, [User]?, [Team]?) -> Void)) {
        makeRequest(url: Constants.route.user.requests, type: .GET, body: Data(base64Encoded: "")) { (error: String?, data: Data?) in
            if(error != nil) {
                return cb(error, nil, nil, nil, nil);
            }

            let teamRequests: [TeamRequest]? = self.decode(data!);
            if(teamRequests == nil) {
                return cb(Constants.error.genericError, nil, nil, nil, nil);
            }

            var users = [User?](repeating: nil, count: teamRequests!.count);
            var teams = [Team?](repeating: nil, count: teamRequests!.count);
            var tournaments = [Tournament?](repeating: nil, count: teamRequests!.count);

            let group = DispatchGroup();
            var errorOccurred = AtomicBoolean();
            for (index, req) in teamRequests!.enumerated() {
                if(errorOccurred.value) {
                    return cb(Constants.error.serverError, nil, nil, nil, nil);
                }

                group.enter();
                group.enter();
                group.enter();

                UserService.shared.getUser(req.requesterId, cb: { (error: String?, user: User?) in
                    if(error != nil) {
                        errorOccurred.value = true;
                        return;
                    }

                    users[index] = user!;
                    group.leave();
                });

                TeamService.shared.getTeam(req.teamId, cb: { (error: String?, team: Team?) in
                    if(error != nil) {
                        errorOccurred.value = true;
                        return;
                    }

                    teams[index] = team!;
                    group.leave();

                    TournamentService.shared.getTournament(team!.tournamentId, cb: { (error: String?, tournament: Tournament?) in
                        if(error != nil) {
                            errorOccurred.value = true;
                            return;
                        }

                        tournaments[index] = tournament!;
                        group.leave();
                    });
                });
            }

            group.notify(queue: .main) {
                if(errorOccurred.value) {
                    return cb(Constants.error.serverError, nil, nil, nil, nil);
                }

                return cb(nil, teamRequests, tournaments as? [Tournament], users as? [User], teams as? [Team]);
            }
        }
    }

    func requestToJoinTeam(_ team: Team, cb: @escaping ((String?) -> Void)) {
        makeRequest(url: Constants.route.team.joinTeam(team.id), type: .POST, body: Data(base64Encoded: "")) { (error: String?, data: Data?) in
            return cb(error);
        }
    }

    func requestUserToJoinTeam(team: Team, user: User, cb: @escaping ((String?) -> Void)) {
        makeRequest(url: Constants.route.team.userJoinTeam(userId: user.id, teamId: team.id), type: .POST, body: Data(base64Encoded: "")) { (error: String?, data: Data?) in
            return cb(error);
        }
    }

    func acceptRequest(_ teamRequest: TeamRequest, cb: @escaping ((String?) -> Void)) {
        makeRequest(url: Constants.route.teamRequest.accept(teamRequest.id), type: .POST, body: Data(base64Encoded: "")) { (error: String?, data: Data?) in
            cb(error);
        }
    }

    func rejectRequest(_ teamRequest: TeamRequest, cb: @escaping ((String?) -> Void)) {
        makeRequest(url: Constants.route.teamRequest.reject(teamRequest.id), type: .DELETE, body: Data(base64Encoded: "")) { (error: String?, data: Data?) in
            cb(error);
        }
    }
};

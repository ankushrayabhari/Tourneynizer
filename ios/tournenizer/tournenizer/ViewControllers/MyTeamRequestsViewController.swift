//
//  MyTeamRequestsViewController.swift
//  tournenizer
//
//  Created by Ankush Rayabhari on 3/9/18.
//  Copyright © 2018 Ankush Rayabhari. All rights reserved.
//

import Foundation;
import UIKit;

class MyTeamRequestsViewController : UIViewController {
    let teamRequestView = TeamRequestListViewController();

    override func loadView() {
        view = UIView();
        view.backgroundColor = Constants.color.white;
        teamRequestView.setReloadCallback {
            self.loadTeamRequests();
        }
        teamRequestView.setAcceptCallback(acceptRequest(_:index:));
        teamRequestView.setRejectCallback(rejectRequest(_:index:));
        
        addChildViewController(teamRequestView);
        teamRequestView.view.frame = view.bounds;
        view.addSubview(teamRequestView.view);
        teamRequestView.didMove(toParentViewController: self);

        loadTeamRequests();

        view.setNeedsUpdateConstraints();
    }

    func acceptRequest(_ teamRequest: TeamRequest, index: Int) {

    }

    func rejectRequest(_ teamRequest: TeamRequest, index: Int) {

    }

    // Ensures that the corresponding methods are only called once
    var didUpdateConstraints = false;

    // Sets constraints on all views
    override func updateViewConstraints() {
        super.updateViewConstraints();
    }

    func loadTeamRequests() {
        TeamRequestService.shared.getRequestsForCurrentUser { (error: String?, teamRequests: [TeamRequest]?, tournaments: [Tournament]?, users: [User]?) in
            if(error != nil) {
                return;
            }
            self.teamRequestView.setData(teamRequests: teamRequests!, tournaments: tournaments!, requesters: users!);
        }
    }
}

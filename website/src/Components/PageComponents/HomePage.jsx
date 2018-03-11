import React, { Component } from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import NavBar from '../HeaderComponent/NavBar';
import LoginPage from './LoginPage.jsx';
import {Jumbotron, Carousel} from 'react-bootstrap'
import HomePageContent from './HomePageContent.jsx'

class HomePage extends Component {

	render() {
		let welcome = <div>Welcome to Tourneynizer</div>;

		return (
			<div>
				<Jumbotron>
				<h1>HomePage</h1>
				<h4>{welcome}</h4>
				</Jumbotron>
				<HomePageContent />
			</div>
		);
	}
}
export default HomePage
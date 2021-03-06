import React from 'react';
import PlacesAutocomplete, { geocodeByAddress, getLatLng } from 'react-places-autocomplete';
import { Form } from 'react-bootstrap';

class GooglePlacepicker extends React.Component {
	constructor(props) {
		super(props)
		this.state = { address: '' }
		this.onChange = (address) => {
			this.setState({address});
			geocodeByAddress(address)
			.then(results => getLatLng(results[0]))
			.then(({ lat, lng }) => this.props.formStateSetter(lat, lng))
		}
	}

	render() {
		const inputProps = {
			value: this.state.address,
			onChange: this.onChange,
			placeholder: 'Search for an address',
		}

		return (
			<PlacesAutocomplete inputProps={inputProps} />
		)
	}
}

export default GooglePlacepicker;
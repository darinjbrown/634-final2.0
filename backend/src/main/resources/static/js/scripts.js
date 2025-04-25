document.addEventListener('DOMContentLoaded', () => {
	const tripType = document.getElementById('tripType');
	const returnDateGroup = document.getElementById('returnDateGroup');
	const flightSearchForm = document.getElementById('flightSearchForm');
	const flightResults = document.getElementById('flightResults');
	const resultsList = document.getElementById('resultsList');

	// Toggle return date visibility based on trip type
	tripType.addEventListener('change', () => {
		if (tripType.value === 'round-trip') {
			returnDateGroup.style.display = 'block';
		} else {
			returnDateGroup.style.display = 'none';
		}
	});

	// Ensure the form data is converted to JSON and sent with the correct Content-Type header
	flightSearchForm.addEventListener('submit', async (event) => {
		event.preventDefault();

		const formData = new FormData(flightSearchForm);
		const data = Object.fromEntries(formData.entries());

		try {
			const response = await fetch('/api/flights/search', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify(data), // Convert form data to JSON
			});

			if (!response.ok) {
				const error = await response.text();
				alert('Error: ' + error);
				return;
			}

			const flights = await response.json();
			resultsList.innerHTML = '';

			flights.forEach((flight) => {
				const listItem = document.createElement('li');
				listItem.className = 'list-group-item';
				listItem.innerHTML = `
                    <strong>${flight.airline}</strong> - ${flight.flightNumber}<br>
                    ${flight.departure} to ${flight.arrival}<br>
                    Departure: ${flight.departureTime}<br>
                    Arrival: ${flight.arrivalTime}<br>
                    Price: $${flight.price}
                `;
				resultsList.appendChild(listItem);
			});

			flightResults.style.display = 'block';
		} catch (error) {
			alert('Error: ' + error.message);
		}
	});
});

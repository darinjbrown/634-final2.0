document.addEventListener('DOMContentLoaded', () => {
	const tripType = document.getElementById('tripType');
	const returnDateGroup = document.getElementById('returnDateGroup');
	const flightSearchForm = document.getElementById('flightSearchForm');
	const flightResults = document.getElementById('flightResults');
	const resultsList = document.getElementById('resultsList');
	const searchButton = document.querySelector('button[type="submit"]');

	// Toggle return date visibility based on trip type
	tripType.addEventListener('change', () => {
		if (tripType.value === 'round-trip') {
			returnDateGroup.style.display = 'block';
		} else {
			returnDateGroup.style.display = 'none';
		}
	});

	// Process form submission
	flightSearchForm.addEventListener('submit', async (event) => {
		event.preventDefault();

		// Show loading spinner
		searchButton.innerHTML =
			'<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Searching...';
		searchButton.disabled = true;

		const formData = new FormData(flightSearchForm);

		try {
			// Use POST method to send form data
			const response = await fetch('/api/flights/search', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify(Object.fromEntries(formData)),
			});

			// Reset button state
			searchButton.innerHTML =
				'<i class="material-icons align-middle me-1">search</i> Search Flights';
			searchButton.disabled = false;

			if (!response.ok) {
				const error = await response.text();
				showError('Error: ' + error);
				return;
			}

			const flights = await response.json();
			resultsList.innerHTML = '';

			if (flights.length === 0) {
				resultsList.innerHTML =
					'<li class="list-group-item text-center py-4">No flights found matching your criteria.</li>';
			} else {
				flights.forEach((flight) => {
					const listItem = document.createElement('li');
					listItem.className = 'list-group-item';
					listItem.innerHTML = `
						<div class="d-flex justify-content-between align-items-center mb-2">
							<h5 class="mb-0 text-primary">
								<i class="material-icons align-middle me-2" style="font-size: 18px;">flight</i>
								<strong>${flight.airline}</strong> - ${flight.flightNumber}
							</h5>
							<span class="badge bg-primary rounded-pill">$${flight.price}</span>
						</div>
						<div class="row">
							<div class="col-md-6">
								<div class="mb-1"><strong>From:</strong> ${flight.departure}</div>
								<div><strong>Departure:</strong> ${flight.departureTime}</div>
							</div>
							<div class="col-md-6">
								<div class="mb-1"><strong>To:</strong> ${flight.arrival}</div>
								<div><strong>Arrival:</strong> ${flight.arrivalTime}</div>
							</div>
						</div>
					`;
					resultsList.appendChild(listItem);
				});
			}

			flightResults.style.display = 'block';

			// Scroll to results
			flightResults.scrollIntoView({ behavior: 'smooth' });
		} catch (error) {
			showError('Error: ' + error.message);
			// Reset button state
			searchButton.innerHTML =
				'<i class="material-icons align-middle me-1">search</i> Search Flights';
			searchButton.disabled = false;
		}
	});

	// Helper function to show errors
	function showError(message) {
		// Create error element if it doesn't exist
		let errorElement = document.getElementById('searchError');
		if (!errorElement) {
			errorElement = document.createElement('div');
			errorElement.id = 'searchError';
			errorElement.className = 'alert alert-danger mt-3';
			errorElement.role = 'alert';
			flightSearchForm.insertAdjacentElement('afterend', errorElement);
		}

		errorElement.textContent = message;

		// Auto-hide after 5 seconds
		setTimeout(() => {
			errorElement.remove();
		}, 5000);
	}
});

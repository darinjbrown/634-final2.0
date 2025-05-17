/**
 * SkyExplorer Flight Search Application - Saved Flights JavaScript
 *
 * This script handles the saved flights page functionality:
 * - Fetching and displaying saved flights
 * - Deleting saved flights
 * - Booking saved flights
 */
document.addEventListener('DOMContentLoaded', () => {
	// Use the global authentication check from auth.js
	window.checkAuthStatus().then((isAuthenticated) => {
		if (!isAuthenticated) {
			// User is not logged in, show login modal
			const loginModal = new bootstrap.Modal(
				document.getElementById('loginModal')
			);
			loginModal.show();
			document.getElementById('loadingMessage').style.display = 'none';
			return;
		}

		// User is authenticated, fetch saved flights
		fetchSavedFlights();
	});
});

/**
 * Fetch the user's saved flights from the API
 */
async function fetchSavedFlights() {
	try {
		// Using the getAuthHeaders function from auth.js for consistent header handling
		const response = await fetch('/api/saved-flights', {
			method: 'GET',
			headers: window.getAuthHeaders(),
		});

		// Hide loading message
		document.getElementById('loadingMessage').style.display = 'none';

		if (!response.ok) {
			// Handle unauthorized access
			if (response.status === 401) {
				const loginModal = new bootstrap.Modal(
					document.getElementById('loginModal')
				);
				loginModal.show();
				return;
			}
			throw new Error('Failed to fetch saved flights');
		}

		const flights = await response.json();
		const savedFlightsList = document.getElementById('savedFlightsList');

		if (!flights || flights.length === 0) {
			// Show no flights message
			document.getElementById('noFlightsMessage').style.display = 'block';
			return;
		}

		// Display saved flights
		flights.forEach((flight) => {
			const listItem = document.createElement('li');
			listItem.className = 'list-group-item';
			listItem.innerHTML = `
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h5 class="mb-0 text-primary">
                        <i class="material-icons align-middle me-2" style="font-size: 18px;">flight</i>
                        <strong>${flight.airlineName}</strong> - ${
				flight.flightNumber
			}
                    </h5>
                    <span class="badge bg-primary rounded-pill">$${
						flight.price
					}</span>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <div class="mb-1"><strong>From:</strong> ${
							flight.origin
						}</div>
                        <div><strong>Departure:</strong> ${formatDateTime(
							flight.departureTime
						)}</div>
                    </div>
                    <div class="col-md-6">
                        <div class="mb-1"><strong>To:</strong> ${
							flight.destination
						}</div>
                        <div><strong>Arrival:</strong> ${formatDateTime(
							flight.arrivalTime
						)}</div>
                    </div>
                </div>
                <div class="mt-2 text-end">
                    <button class="btn btn-sm btn-danger delete-flight-btn" data-id="${
						flight.id
					}">
                        <i class="material-icons align-middle" style="font-size: 16px;">delete</i> Remove
                    </button>
                    <button class="btn btn-sm btn-success book-flight-btn" data-id="${
						flight.id
					}">
                        <i class="material-icons align-middle" style="font-size: 16px;">confirmation_number</i> Book
                    </button>
                </div>
            `;
			savedFlightsList.appendChild(listItem);
		});

		// Add event listeners for delete buttons
		document.querySelectorAll('.delete-flight-btn').forEach((btn) => {
			btn.addEventListener('click', handleDeleteFlight);
		});

		// Add event listeners for book buttons
		document.querySelectorAll('.book-flight-btn').forEach((btn) => {
			btn.addEventListener('click', handleBookFlight);
		});
	} catch (error) {
		console.error('Error fetching saved flights:', error);
		document.getElementById('noFlightsMessage').style.display = 'block';
		document.getElementById('noFlightsMessage').innerHTML = `
            <i class="material-icons" style="font-size: 48px;">error</i>
            <h4 class="mt-3">Error loading flights</h4>
            <p>There was a problem loading your saved flights. Please try again later.</p>
        `;
	}
}

/**
 * Format a datetime string for display
 *
 * @param {string} dateTimeString - ISO datetime string
 * @returns {string} Formatted date and time
 */
function formatDateTime(dateTimeString) {
	if (!dateTimeString) return 'N/A';

	const date = new Date(dateTimeString);
	return date.toLocaleString('en-US', {
		year: 'numeric',
		month: 'short',
		day: 'numeric',
		hour: '2-digit',
		minute: '2-digit',
	});
}

/**
 * Handle deleting a saved flight
 *
 * @param {Event} event - Click event
 */
async function handleDeleteFlight(event) {
	const flightId = event.currentTarget.getAttribute('data-id');

	if (
		confirm(
			'Are you sure you want to remove this flight from your saved flights?'
		)
	) {
		try {
			const response = await fetch(`/api/saved-flights/${flightId}`, {
				method: 'DELETE',
				headers: window.getAuthHeaders(),
			});

			if (!response.ok) {
				throw new Error('Failed to delete flight');
			}

			// Remove the flight item from the UI
			const listItem = event.currentTarget.closest('li');
			listItem.remove();

			// Check if there are any flights left
			const savedFlightsList =
				document.getElementById('savedFlightsList');
			if (savedFlightsList.children.length === 0) {
				document.getElementById('noFlightsMessage').style.display =
					'block';
			}

			// Show console message
			console.log('Flight removed successfully');
		} catch (error) {
			console.error('Error deleting saved flight:', error);
		}
	}
}

/**
 * Handle booking a saved flight
 *
 * @param {Event} event - Click event
 */
async function handleBookFlight(event) {
	const flightId = event.currentTarget.getAttribute('data-id');
	const bookButton = event.currentTarget;
	
	// Disable button and show loading state
	bookButton.disabled = true;
	const originalText = bookButton.innerHTML;
	bookButton.innerHTML = `
		<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
		Booking...
	`;
	
	try {
		// First, get the saved flight details to create the booking
		const savedFlightResponse = await fetch(`/api/saved-flights/${flightId}`, {
			method: 'GET',
			headers: window.getAuthHeaders()
		});
		
		if (!savedFlightResponse.ok) {
			throw new Error('Failed to fetch flight details');
		}
		
		const savedFlight = await savedFlightResponse.json();
		
		// Create a booking using the saved flight data
		const response = await fetch('/api/bookings', {
			method: 'POST',
			headers: {
				...window.getAuthHeaders(),
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({
				// Map saved flight data to booking DTO
				flightId: savedFlight.id.toString(),
				departureAirport: savedFlight.origin,
				arrivalAirport: savedFlight.destination,
				departureTime: savedFlight.departureTime,
				arrivalTime: savedFlight.arrivalTime,
				airline: savedFlight.airlineCode,
				flightNumber: savedFlight.flightNumber,
				passengerCount: 1,
				totalPrice: savedFlight.price,
				bookingStatus: 'CONFIRMED',
				bookingDate: new Date().toISOString()
			})
		});
		
		if (!response.ok) {
			throw new Error('Failed to book flight');
		}
		
		// Get the button's parent li element
		const listItem = bookButton.closest('li');
		
		// Show success message
		bookButton.classList.remove('btn-success');
		bookButton.classList.add('btn-outline-success');
		bookButton.innerHTML = `
			<i class="material-icons align-middle" style="font-size: 16px;">check_circle</i> Booked
		`;
		bookButton.disabled = true;
		
		// Add a success alert to the list item
		const successAlert = document.createElement('div');
		successAlert.className = 'alert alert-success mt-2';
		successAlert.innerHTML = 'Flight booked successfully! View details in the <a href="/bookings">Bookings page</a>.';
		listItem.appendChild(successAlert);
		
		// Hide alert after 5 seconds
		setTimeout(() => {
			successAlert.remove();
		}, 5000);
		
	} catch (error) {
		console.error('Error booking flight:', error);
		
		// Reset button state
		bookButton.disabled = false;
		bookButton.innerHTML = originalText;
		
		// Show error alert
		alert('Failed to book the flight. Please try again.');
	}
}

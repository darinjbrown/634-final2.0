/**
 * SkyExplorer Flight Search Application - Bookings JavaScript
 *
 * This script handles the bookings page functionality:
 * - Fetching and displaying user bookings
 * - Handling booking creation and management
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
			hideLoadingIndicator();
			return;
		}

		// Check for flightId in URL parameters (new booking flow)
		const urlParams = new URLSearchParams(window.location.search);
		const flightId = urlParams.get('flightId');

		if (flightId) {
			// If flightId is present, show booking form with flight details
			loadFlightForBooking(flightId);
		} else {
			// Otherwise, fetch existing bookings
			fetchUserBookings();
		}
	});

	// Add event listener for booking form if it exists
	const bookingForm = document.getElementById('bookingForm');
	if (bookingForm) {
		bookingForm.addEventListener('submit', handleBookingSubmit);
	}
});

/**
 * Hide the loading indicator
 */
function hideLoadingIndicator() {
	const loadingMessage = document.getElementById('loadingMessage');
	if (loadingMessage) {
		loadingMessage.style.display = 'none';
	}
}

/**
 * Fetch the user's bookings from the API
 */
async function fetchUserBookings() {
	try {
		const response = await fetch('/api/bookings', {
			method: 'GET',
			headers: window.getAuthHeaders(),
		});

		hideLoadingIndicator();

		if (!response.ok) {
			// Handle unauthorized access
			if (response.status === 401) {
				const loginModal = new bootstrap.Modal(
					document.getElementById('loginModal')
				);
				loginModal.show();
				return;
			}
			throw new Error('Failed to fetch bookings');
		}

		const bookings = await response.json();
		const bookingsList = document.getElementById('bookingsList');

		if (!bookings || bookings.length === 0) {
			// Show no bookings message
			document.getElementById('noBookingsMessage').style.display =
				'block';
			return;
		}

		// Display bookings
		bookings.forEach((booking) => {
			const bookingCard = createBookingCard(booking);
			bookingsList.appendChild(bookingCard);
		});
	} catch (error) {
		console.error('Error fetching bookings:', error);
		hideLoadingIndicator();
		document.getElementById('noBookingsMessage').style.display = 'block';
		document.getElementById('noBookingsMessage').innerHTML = `
            <i class="material-icons" style="font-size: 48px;">error</i>
            <h4 class="mt-3">Error loading bookings</h4>
            <p>There was a problem loading your bookings. Please try again later.</p>
        `;
	}
}

/**
 * Create a booking card element
 *
 * @param {Object} booking - The booking data
 * @returns {HTMLElement} The booking card element
 */
function createBookingCard(booking) {
	const card = document.createElement('div');
	card.className = 'card mb-4 shadow-sm';

	// Format the booking date
	const bookingDate = new Date(booking.bookingDate).toLocaleDateString(
		'en-US',
		{
			year: 'numeric',
			month: 'long',
			day: 'numeric',
		}
	);

	// Create HTML for the booking card
	card.innerHTML = `
        <div class="card-header bg-primary text-white">
            <div class="d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="material-icons align-middle me-2">confirmation_number</i>
                    Booking #${booking.id}
                </h5>
                <span class="badge bg-light text-primary">Booked on ${bookingDate}</span>
            </div>
        </div>
        <div class="card-body">
            <div class="row mb-3">
                <div class="col-md-6">                    <h5 class="card-title">
                        <i class="material-icons align-middle me-2">flight</i>
                        ${booking.airline} - ${booking.flightNumber}
                    </h5>
                    <p class="card-text mb-1">
                        <strong>From:</strong> ${booking.departureAirport}
                    </p>
                    <p class="card-text">
                        <strong>To:</strong> ${booking.arrivalAirport}
                    </p>
                </div>
                <div class="col-md-6">
                    <p class="card-text mb-1">
                        <i class="material-icons align-middle me-1">schedule</i>
                        <strong>Departure:</strong> ${formatDateTime(
							booking.departureTime
						)}
                    </p>
                    <p class="card-text mb-1">
                        <i class="material-icons align-middle me-1">schedule</i>
                        <strong>Arrival:</strong> ${formatDateTime(
							booking.arrivalTime
						)}
                    </p>
                    <p class="card-text">
                        <i class="material-icons align-middle me-1">attach_money</i>
                        <strong>Price:</strong> $${booking.totalPrice}
                    </p>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-md-6">
                    <h6><i class="material-icons align-middle me-1">person</i> Passenger Information</h6>
                    <p class="mb-1"><strong>Name:</strong> ${
						booking.passengerName
					}</p>
                    <p><strong>Email:</strong> ${booking.contactEmail}</p>
                </div>
                <div class="col-md-6">
                    <h6><i class="material-icons align-middle me-1">info</i> Status</h6>
                    <div class="d-flex align-items-center">
                        <span class="badge ${getStatusBadgeClass(
							booking.status
						)} me-2">${booking.status}</span>
                        <span>${getStatusMessage(booking.status)}</span>
                    </div>
                </div>
            </div>
            <div class="mt-3 text-end">
                <button class="btn btn-outline-primary btn-sm view-details-btn" data-id="${
					booking.id
				}">
                    <i class="material-icons align-middle" style="font-size: 16px;">visibility</i> View Details
                </button>
            </div>
        </div>
    `;
	// Add event listener to the view details button
	const viewDetailsBtn = card.querySelector('.view-details-btn');
	if (viewDetailsBtn) {
		viewDetailsBtn.addEventListener('click', () => {
			// Show the detailed view of this booking (currently doesn't go to a new page)
			// Instead, we'll just scroll to the booking card and highlight it
			viewDetailsBtn.closest('.card').classList.add('border-primary');
			setTimeout(() => {
				viewDetailsBtn
					.closest('.card')
					.classList.remove('border-primary');
			}, 3000);
		});
	}

	return card;
}

/**
 * Load flight details for creating a new booking
 *
 * @param {string} flightId - The ID of the saved flight to book
 */
async function loadFlightForBooking(flightId) {
	try {
		const response = await fetch(`/api/saved-flights/${flightId}`, {
			method: 'GET',
			headers: window.getAuthHeaders(),
		});

		hideLoadingIndicator();

		if (!response.ok) {
			// Handle errors
			if (response.status === 401) {
				const loginModal = new bootstrap.Modal(
					document.getElementById('loginModal')
				);
				loginModal.show();
				return;
			} else if (response.status === 404) {
				showBookingError('Flight not found. Please try again.');
				return;
			}
			throw new Error('Failed to fetch flight details');
		}

		const flight = await response.json();
		populateBookingForm(flight);

		// Show the booking form section
		document.getElementById('bookingFormSection').style.display = 'block';
		document.getElementById('bookingsListSection').style.display = 'none';
	} catch (error) {
		console.error('Error loading flight for booking:', error);
		showBookingError(
			'Error loading flight details. Please try again later.'
		);
	}
}

/**
 * Populate the booking form with flight details
 *
 * @param {Object} flight - The flight data
 */
function populateBookingForm(flight) {
	// Add flight ID to hidden input
	document.getElementById('flightId').value = flight.id;

	// Populate flight details summary
	const flightDetails = document.getElementById('flightDetails');
	if (flightDetails) {
		flightDetails.innerHTML = `
            <div class="card mb-4">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0">Flight Details</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <h5>
                                <i class="material-icons align-middle me-2">flight</i>
                                ${flight.airlineName || flight.airlineCode} - ${
			flight.flightNumber
		}
                            </h5>
                            <p class="mb-1"><strong>From:</strong> ${
								flight.origin
							}</p>
                            <p><strong>To:</strong> ${flight.destination}</p>
                        </div>
                        <div class="col-md-6">
                            <p class="mb-1">
                                <i class="material-icons align-middle me-1">schedule</i>
                                <strong>Departure:</strong> ${formatDateTime(
									flight.departureTime
								)}
                            </p>
                            <p class="mb-1">
                                <i class="material-icons align-middle me-1">schedule</i>
                                <strong>Arrival:</strong> ${formatDateTime(
									flight.arrivalTime
								)}
                            </p>
                            <p class="mb-1">
                                <i class="material-icons align-middle me-1">attach_money</i>
                                <strong>Price:</strong> <span class="fw-bold text-primary">${
									flight.currency || '$'
								}${flight.price}</span>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        `;
	}

	// Autofill user info if available
	window
		.getCurrentUser()
		.then((user) => {
			if (user) {
				document.getElementById('passengerName').value =
					user.fullName || '';
				document.getElementById('contactEmail').value =
					user.email || '';
			}
		})
		.catch((error) => {
			console.error('Error getting current user:', error);
		});
}

/**
 * Handle booking form submission
 *
 * @param {Event} event - Form submit event
 */
async function handleBookingSubmit(event) {
	event.preventDefault();

	const submitBtn = document.getElementById('submitBookingBtn');
	const originalBtnText = submitBtn.innerHTML;
	submitBtn.innerHTML =
		'<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...';
	submitBtn.disabled = true;
	try {
		// Get the flight ID from the form
		const flightId = document.getElementById('flightId').value;

		// First get the saved flight data to create the booking
		const flightResponse = await fetch(`/api/saved-flights/${flightId}`, {
			method: 'GET',
			headers: window.getAuthHeaders(),
		});

		if (!flightResponse.ok) {
			throw new Error('Failed to get flight details for booking');
		}

		const flight = await flightResponse.json();

		// Create booking data with all required fields
		const formData = {
			flightId: flight.id,
			departureAirport: flight.origin,
			arrivalAirport: flight.destination,
			departureTime: flight.departureTime,
			arrivalTime: flight.arrivalTime,
			airline: flight.airlineCode,
			flightNumber: flight.flightNumber,
			passengerCount: 1,
			totalPrice: flight.price,
			bookingStatus: 'CONFIRMED',
			contactEmail: document.getElementById('contactEmail').value,
			contactPhone: document.getElementById('phoneNumber').value,
			additionalNotes: document.getElementById('specialRequests').value,
		};

		const response = await fetch('/api/bookings', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				...window.getAuthHeaders(),
			},
			body: JSON.stringify(formData),
		});

		if (!response.ok) {
			throw new Error('Failed to create booking');
		}

		const booking = await response.json();

		// Show success message
		document.getElementById('bookingForm').style.display = 'none';
		document.getElementById('bookingSuccess').style.display = 'block';
		document.getElementById('bookingReference').textContent =
			booking.bookingReference || booking.id;
	} catch (error) {
		console.error('Error creating booking:', error);
		document.getElementById('bookingError').textContent =
			'An error occurred while processing your booking. Please try again.';
		document.getElementById('bookingError').style.display = 'block';
	} finally {
		submitBtn.innerHTML = originalBtnText;
		submitBtn.disabled = false;
	}
}

/**
 * Show booking error message
 *
 * @param {string} message - The error message to display
 */
function showBookingError(message) {
	const errorEl = document.getElementById('bookingError');
	if (errorEl) {
		errorEl.textContent = message;
		errorEl.style.display = 'block';
	}

	hideLoadingIndicator();
	document.getElementById('noBookingsMessage').style.display = 'block';
	document.getElementById('noBookingsMessage').innerHTML = `
        <i class="material-icons" style="font-size: 48px;">error</i>
        <h4 class="mt-3">Error</h4>
        <p>${message}</p>
        <a href="/" class="btn btn-primary mt-3">Return to Flight Search</a>
    `;
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
 * Get the appropriate CSS class for booking status badge
 *
 * @param {string} status - Booking status
 * @returns {string} CSS class for the status badge
 */
function getStatusBadgeClass(status) {
	switch (status?.toUpperCase()) {
		case 'CONFIRMED':
			return 'bg-success';
		case 'PENDING':
			return 'bg-warning text-dark';
		case 'CANCELLED':
			return 'bg-danger';
		default:
			return 'bg-secondary';
	}
}

/**
 * Get a descriptive message for the booking status
 *
 * @param {string} status - Booking status
 * @returns {string} Status description
 */
function getStatusMessage(status) {
	switch (status?.toUpperCase()) {
		case 'CONFIRMED':
			return 'Your booking is confirmed and ready to go!';
		case 'PENDING':
			return 'Your booking is being processed.';
		case 'CANCELLED':
			return 'This booking has been cancelled.';
		default:
			return 'Status unknown';
	}
}

/**
 * Get a descriptive message for booking status
 *
 * @param {string} status - Booking status
 * @returns {string} Status message
 */
function getStatusMessage(status) {
	switch (status?.toUpperCase()) {
		case 'CONFIRMED':
			return 'Your booking is confirmed';
		case 'PENDING':
			return 'Awaiting confirmation';
		case 'CANCELLED':
			return 'This booking has been cancelled';
		default:
			return 'Status unknown';
	}
}

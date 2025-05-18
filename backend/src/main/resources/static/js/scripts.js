/**
 * SkyExplorer Flight Search Application - Main JavaScript File
 *
 * This script handles the core frontend functionality of the SkyExplorer application including:
 * - Flight search form submission and result display
 * - Flight saving (individual and batch)
 * - Authentication state checking
 * - UI elements like toasts and modals
 * - Responsive interaction with the backend API
 */
document.addEventListener('DOMContentLoaded', () => {
	// DOM element references for interactive components
	const tripType = document.getElementById('tripType');
	const returnDateGroup = document.getElementById('returnDateGroup');
	const flightSearchForm = document.getElementById('flightSearchForm');
	const flightResults = document.getElementById('flightResults');
	const resultsList = document.getElementById('resultsList');
	const searchButton = document.querySelector('button[type="submit"]');
	const saveAllFlightsBtn = document.getElementById('saveAllFlightsBtn');
	const loginModal = document.getElementById('loginModal');

	// Store search results for save all functionality and sorting
	let currentSearchResults = [];

	/**
	 * Toggle return date field visibility based on one-way or round-trip selection
	 * Hides the return date when one-way is selected, shows it for round-trip
	 */
	tripType.addEventListener('change', () => {
		if (tripType.value === 'round-trip') {
			returnDateGroup.style.display = 'block';
		} else {
			returnDateGroup.style.display = 'none';
		}
	});

	/**
	 * Process flight search form submission using async/await pattern
	 * Sends search criteria to the backend API and displays results
	 */
	flightSearchForm.addEventListener('submit', async (event) => {
		event.preventDefault();

		// Show loading spinner to indicate search in progress
		searchButton.innerHTML =
			'<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Searching...';
		searchButton.disabled = true;

		const formData = new FormData(flightSearchForm);

		try {
			// Send flight search request to backend API
			const response = await fetch('/api/flights/search', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify(Object.fromEntries(formData)),
			});

			// Reset search button to original state
			searchButton.innerHTML =
				'<i class="material-icons align-middle me-1">search</i> Search Flights';
			searchButton.disabled = false;

			if (!response.ok) {
				const error = await response.text();
				return;
			}

			const flights = await response.json();
			resultsList.innerHTML = '';
			currentSearchResults = flights; // Store the search results for later use

			// Check authentication status to show/hide save options
			const isAuthenticated = isUserAuthenticated();
			if (isAuthenticated && flights.length > 0) {
				saveAllFlightsBtn.style.display = 'inline-block';
			} else {
				saveAllFlightsBtn.style.display = 'none';
			}

			if (flights.length === 0) {
				// Handle case when no flights are found
				resultsList.innerHTML =
					'<li class="list-group-item text-center py-4">No flights found matching your criteria.</li>';
			} else {
				// Create flight result cards for each flight returned
				flights.forEach((flight, index) => {
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
						<div class="mt-2 text-end">
							<button class="btn btn-sm btn-outline-primary save-flight-btn" data-index="${index}">
								<i class="material-icons align-middle" style="font-size: 16px;">favorite_border</i> Save Flight
							</button>
						</div>
					`;
					resultsList.appendChild(listItem);
				});

				// Add event listeners to individual flight save buttons - CSP compliant
				attachSaveButtonListeners();
			}

			// Display the results section
			flightResults.style.display = 'block';

			// Smoothly scroll to the results section
			flightResults.scrollIntoView({ behavior: 'smooth' });
		} catch (error) {
			// Reset button state
			searchButton.innerHTML =
				'<i class="material-icons align-middle me-1">search</i> Search Flights';
			searchButton.disabled = false;
		}
	});

	/**
	 * Set up "Save All Flights" button click handler if the button exists
	 */
	if (saveAllFlightsBtn) {
		saveAllFlightsBtn.addEventListener('click', handleSaveAllFlights);
	}

	/**
	 * Attach event listeners to all save flight buttons
	 * This ensures compliance with Content Security Policy
	 */
	function attachSaveButtonListeners() {
		document.querySelectorAll('.save-flight-btn').forEach((btn) => {
			// Remove any existing event listeners to prevent duplicates
			const newBtn = btn.cloneNode(true);
			btn.parentNode.replaceChild(newBtn, btn);

			// Add the click event listener in a CSP-compliant way
			newBtn.addEventListener('click', handleSaveFlight);
		});

		console.log('Save button listeners attached in CSP-compliant way');
	}

	/**
	 * Configure price sorting functionality for flight results
	 * Allows users to toggle between ascending and descending price order
	 */
	const sortPriceBtn = document.getElementById('sortPriceBtn');
	if (sortPriceBtn) {
		sortPriceBtn.addEventListener('click', () => {
			if (currentSearchResults.length === 0) return;

			// Toggle sort order between ascending and descending
			sortPriceBtn.dataset.order =
				sortPriceBtn.dataset.order === 'asc' ? 'desc' : 'asc';
			const order = sortPriceBtn.dataset.order;

			// Sort the current results array based on flight price
			currentSearchResults.sort((a, b) => {
				const priceA = parseFloat(a.price);
				const priceB = parseFloat(b.price);
				return order === 'asc' ? priceA - priceB : priceB - priceA;
			});

			// Update the UI with the sorted results
			updateFlightResults(currentSearchResults);

			// Update button text to indicate current sort order
			sortPriceBtn.innerHTML = `
				<i class="material-icons align-middle">${
					order === 'asc' ? 'arrow_upward' : 'arrow_downward'
				}</i>
				Sort by Price ${order === 'asc' ? '(Low to High)' : '(High to Low)'}
			`;
		});
	}

	/**
	 * Check if the user is currently authenticated based on JWT token presence
	 *
	 * @returns {boolean} True if a JWT token exists in localStorage, false otherwise
	 */
	function isUserAuthenticated() {
		return localStorage.getItem('jwtToken') !== null;
	}

	/**
	 * Prepare authorization headers for API requests
	 * Includes the JWT token if the user is authenticated
	 *
	 * @returns {Object} Headers object with Content-Type and optional Authorization
	 */
	function getAuthHeaders() {
		const headers = {
			'Content-Type': 'application/json',
		};

		const token = localStorage.getItem('jwtToken');
		if (token) {
			headers['Authorization'] = `Bearer ${token}`;
		}

		return headers;
	}

	/**
	 * Handle saving an individual flight
	 * Checks authentication status and shows login modal if not authenticated
	 *
	 * @param {Event} event - The click event from the save button
	 */
	async function handleSaveFlight(event) {
		event.preventDefault();

		// Redirect to login if user is not authenticated
		if (!isUserAuthenticated()) {
			// Show login modal instead of direct page navigation
			const bsLoginModal = new bootstrap.Modal(loginModal);
			bsLoginModal.show();
			return;
		}

		// Get the flight data from the currentSearchResults array
		const index = parseInt(event.currentTarget.getAttribute('data-index'));
		const flight = currentSearchResults[index];

		try {
			// Call the API to save the flight
			const saveResponse = await saveFlight(flight);
			if (saveResponse.success) {
				// Update button appearance to indicate flight is saved
				event.currentTarget.innerHTML = `
					<i class="material-icons align-middle" style="font-size: 16px;">favorite</i> Saved
				`;
				event.currentTarget.disabled = true;
				event.currentTarget.classList.remove('btn-outline-primary');
				event.currentTarget.classList.add('btn-success');
			} else {
			}
		} catch (error) {}
	}

	/**
	 * Handle saving all currently displayed flights
	 * Checks authentication status and processes all flights in parallel
	 *
	 * @param {Event} event - The click event from the "Save All" button
	 */
	async function handleSaveAllFlights(event) {
		event.preventDefault();

		// Check if user is authenticated
		if (!isUserAuthenticated()) {
			// Show login modal
			const bsLoginModal = new bootstrap.Modal(loginModal);
			bsLoginModal.show();
			return;
		}

		if (currentSearchResults.length === 0) {
			return;
		}

		// Disable button during saving
		saveAllFlightsBtn.disabled = true;
		saveAllFlightsBtn.innerHTML = `
			<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Saving...
		`;

		try {
			// Save each flight
			const savePromises = currentSearchResults.map((flight) =>
				saveFlight(flight)
			);
			const results = await Promise.all(savePromises);

			// Count successful saves
			const successful = results.filter((r) => r.success).length;

			// Disable all save buttons
			document.querySelectorAll('.save-flight-btn').forEach((btn) => {
				btn.innerHTML = `
					<i class="material-icons align-middle" style="font-size: 16px;">favorite</i> Saved
				`;
				btn.disabled = true;
				btn.classList.remove('btn-outline-primary');
				btn.classList.add('btn-success');
			});

			// Update Save All button
			saveAllFlightsBtn.innerHTML = `
				<i class="material-icons align-middle">favorite</i> All Saved
			`;
			saveAllFlightsBtn.disabled = true;
		} catch (error) {
			// Reset button
			saveAllFlightsBtn.disabled = false;
			saveAllFlightsBtn.innerHTML = `
				<i class="material-icons align-middle">favorite_border</i> Save All
			`;
		}
	}

	/**
	 * Make API call to save a single flight to the user's saved flights
	 *
	 * @param {Object} flight - Flight data object to save
	 * @returns {Object} Response object with success flag and data or error message
	 */
	async function saveFlight(flight) {
		try {
			// First check if JWT token exists
			const token = localStorage.getItem('jwtToken');
			if (!token) {
				// No token found, show login modal
				const bsLoginModal = new bootstrap.Modal(loginModal);
				bsLoginModal.show();
				return {
					success: false,
					message: 'Please log in to save flights',
				};
			}

			// Get fresh auth headers
			const headers = getAuthHeaders();

			// Debug token being sent
			console.log(
				'Using JWT token for request:',
				token.substring(0, 15) + '...'
			);

			// Convert time-only strings to proper ISO datetime format
			const today = new Date().toISOString().split('T')[0]; // Get today's date in YYYY-MM-DD format
			const departureDateTime = formatDateTimeForAPI(
				flight.departureTimeRaw || flight.departureTime,
				today
			);
			const arrivalDateTime = formatDateTimeForAPI(
				flight.arrivalTimeRaw || flight.arrivalTime,
				today
			);

			console.log('Sending departure time:', departureDateTime);
			console.log('Sending arrival time:', arrivalDateTime);

			// Make the API request with auth headers
			const response = await fetch('/api/saved-flights', {
				method: 'POST',
				headers: headers,
				credentials: 'include', // Include cookies for session validation
				body: JSON.stringify({
					airlineCode:
						flight.airlineCode ||
						extractAirlineCode(flight.flightNumber),
					airlineName: flight.airline,
					flightNumber: flight.flightNumber,
					origin: flight.departure,
					destination: flight.arrival,
					departureTime: departureDateTime,
					arrivalTime: arrivalDateTime,
					price: parseFloat(flight.price),
				}),
			});

			if (response.ok) {
				// HTTP 2xx status codes (including 201 Created)
				console.log(
					'Flight saved successfully with status:',
					response.status
				);

				// Check if response has content before trying to parse as JSON
				const contentType = response.headers.get('content-type');
				let data = {};

				if (
					contentType &&
					contentType.includes('application/json') &&
					response.status !== 204
				) {
					try {
						// Only try to parse JSON if we have JSON content
						const responseText = await response.text();
						if (responseText) {
							data = JSON.parse(responseText);
						}
					} catch (e) {
						console.warn(
							"Couldn't parse response as JSON, but the request was successful",
							e
						);
					}
				}

				return { success: true, data };
			} else {
				if (response.status === 401 || response.status === 403) {
					// Unauthorized or Forbidden - token might be expired or invalid
					console.log('Authentication error:', response.status);

					// Don't automatically remove token during development
					const isLocalhost =
						window.location.hostname === 'localhost' ||
						window.location.hostname === '127.0.0.1';

					if (!isLocalhost) {
						localStorage.removeItem('jwtToken'); // Only clear in production
					} else {
						console.warn(
							'Auth validation failed, but keeping token for development purposes'
						);
					}

					// Capture error message without showing a toast
					let errorMessage = 'Session expired. Please log in again.';
					try {
						const errorData = await response.json();
						if (errorData.message) {
							errorMessage = errorData.message;
						}
					} catch (e) {
						// If not JSON, use default message
					}

					// Show login modal
					const bsLoginModal = new bootstrap.Modal(loginModal);
					bsLoginModal.show();

					return {
						success: false,
						message: errorMessage,
					};
				} else {
					// Other errors
					let message = 'Failed to save flight';
					try {
						const errorData = await response.json();
						message = errorData.message || message;
					} catch (e) {
						// If response is not JSON, use default message
					}
					return {
						success: false,
						message: message,
					};
				}
			}
		} catch (error) {
			console.error('Error saving flight:', error);
			return { success: false, message: error.message };
		}
	}

	/**
	 * Format a time string into a proper ISO datetime format
	 *
	 * @param {string} timeStr - The time string (e.g. "18:30")
	 * @param {string} dateStr - The date string in YYYY-MM-DD format
	 * @returns {string} ISO datetime format (YYYY-MM-DDThh:mm:ss)
	 */
	function formatDateTimeForAPI(timeStr, dateStr) {
		// Check if already in ISO format
		if (timeStr && timeStr.includes('T')) {
			return timeStr;
		}

		// Handle empty values
		if (!timeStr) return null;

		// Extract hours and minutes from time string
		let hours = 0;
		let minutes = 0;

		// Try to parse common time formats
		const timeMatch = timeStr.match(/(\d{1,2}):(\d{2})(?:\s*(AM|PM))?/i);
		if (timeMatch) {
			hours = parseInt(timeMatch[1], 10);
			minutes = parseInt(timeMatch[2], 10);

			// Handle AM/PM if present
			if (
				timeMatch[3] &&
				timeMatch[3].toUpperCase() === 'PM' &&
				hours < 12
			) {
				hours += 12;
			} else if (
				timeMatch[3] &&
				timeMatch[3].toUpperCase() === 'AM' &&
				hours === 12
			) {
				hours = 0;
			}
		}

		// Format hours and minutes with leading zeros
		const formattedHours = hours.toString().padStart(2, '0');
		const formattedMinutes = minutes.toString().padStart(2, '0');

		// Construct ISO datetime
		return `${dateStr}T${formattedHours}:${formattedMinutes}:00`;
	}

	/**
	 * Extract airline code from flight number if not explicitly provided
	 *
	 * @param {string} flightNumber - The flight number (e.g. "AA123")
	 * @returns {string} The airline code (e.g. "AA")
	 */
	function extractAirlineCode(flightNumber) {
		if (!flightNumber) return '';
		// Most airline codes are 2 characters, some are 3
		const match = flightNumber.match(/^([A-Z]{2,3})\d+$/);
		return match ? match[1] : '';
	}

	/**
	 * Update the flight results display in the UI with new or sorted flight data
	 * Recreates all flight cards and reattaches event listeners
	 *
	 * @param {Array} flights - Array of flight objects to display
	 */
	function updateFlightResults(flights) {
		resultsList.innerHTML = '';

		flights.forEach((flight, index) => {
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
				<div class="mt-2 text-end">
					<button class="btn btn-sm btn-outline-primary save-flight-btn" data-index="${index}">
						<i class="material-icons align-middle" style="font-size: 16px;">favorite_border</i> Save Flight
					</button>
				</div>
			`;
			resultsList.appendChild(listItem);
		});

		// Re-attach event listeners in a CSP-compliant way
		attachSaveButtonListeners();
	}

	/**
	 * Create the toast container for notifications if it doesn't already exist
	 * Appends a fixed-position container to the bottom-right corner of the page
	 */
	function createToastContainer() {
		const toastContainer = document.createElement('div');
		toastContainer.className =
			'toast-container position-fixed bottom-0 end-0 p-3';
		toastContainer.id = 'toastContainer';
		document.body.appendChild(toastContainer);
	}
});

/**
 * Global function to handle flight search form submission
 * Used when the form has an onsubmit attribute in HTML
 * Provides alternative submission method to the event listener
 *
 * @param {Event} event - The form submission event
 * @returns {boolean} Always returns false to prevent default form submission
 */
function handleFlightSearch(event) {
	// Prevent default form submission to make this CSP-compliant
	if (event) {
		event.preventDefault();
	}

	const flightSearchForm = document.getElementById('flightSearchForm');
	const searchButton = document.querySelector('button[type="submit"]');

	// Show loading spinner
	searchButton.innerHTML =
		'<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Searching...';
	searchButton.disabled = true;

	const formData = new FormData(flightSearchForm);

	fetch('/api/flights/search', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(Object.fromEntries(formData)),
	})
		.then((response) => {
			// Reset button state
			searchButton.innerHTML =
				'<i class="material-icons align-middle me-1">search</i> Search Flights';
			searchButton.disabled = false;

			if (!response.ok) {
				return response.text().then((error) => {
					if (typeof showToast === 'function') {
						showToast('There was an error retrieving flights');
					} else {
						console.error('Error retrieving flights');
					}
					throw new Error(error);
				});
			}
			return response.json();
		})
		.then((flights) => {
			// Process flight results - reusing the same logic as in the event listener
			const resultsList = document.getElementById('resultsList');
			const flightResults = document.getElementById('flightResults');
			const saveAllFlightsBtn =
				document.getElementById('saveAllFlightsBtn');

			resultsList.innerHTML = '';
			window.currentSearchResults = flights; // Make it globally available

			// Check authentication status using global function
			const isAuthenticated =
				typeof isUserAuthenticated === 'function'
					? isUserAuthenticated()
					: localStorage.getItem('jwtToken') !== null;

			if (isAuthenticated && flights.length > 0) {
				saveAllFlightsBtn.style.display = 'inline-block';
			} else {
				saveAllFlightsBtn.style.display = 'none';
			}

			if (flights.length === 0) {
				resultsList.innerHTML =
					'<li class="list-group-item text-center py-4">No flights found matching your criteria.</li>';
			} else {
				flights.forEach((flight, index) => {
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
					<div class="mt-2 text-end">
						<button class="btn btn-sm btn-outline-primary save-flight-btn" data-index="${index}">
							<i class="material-icons align-middle" style="font-size: 16px;">favorite_border</i> Save Flight
						</button>
					</div>
				`;
					resultsList.appendChild(listItem);
				});

				// Add event listeners to save buttons in a CSP-compliant way
				document.querySelectorAll('.save-flight-btn').forEach((btn) => {
					btn.addEventListener('click', function (e) {
						if (typeof handleSaveFlight === 'function') {
							handleSaveFlight(e);
						} else if (window.handleSaveFlight) {
							window.handleSaveFlight(e);
						}
					});
				});
			}

			flightResults.style.display = 'block';

			// Scroll to results
			flightResults.scrollIntoView({ behavior: 'smooth' });
		})
		.catch((error) => {
			if (typeof showToast === 'function') {
				showToast('Error: ' + error.message, 'error');
			} else {
				console.error('Error:', error.message);
			}
			// Reset button state
			searchButton.innerHTML =
				'<i class="material-icons align-middle me-1">search</i> Search Flights';
			searchButton.disabled = false;
		});

	// Return false to prevent the default form submission
	return false;
}

/**
 * Global version of isUserAuthenticated for use outside the main DOM event listener
 * Needed for the handleFlightSearch function
 *
 * @returns {boolean} True if a JWT token exists in localStorage, false otherwise
 */
function isUserAuthenticated() {
	return localStorage.getItem('jwtToken') !== null;
}

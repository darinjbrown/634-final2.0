document.addEventListener('DOMContentLoaded', () => {
	const tripType = document.getElementById('tripType');
	const returnDateGroup = document.getElementById('returnDateGroup');
	const flightSearchForm = document.getElementById('flightSearchForm');
	const flightResults = document.getElementById('flightResults');
	const resultsList = document.getElementById('resultsList');
	const searchButton = document.querySelector('button[type="submit"]');
	const saveAllFlightsBtn = document.getElementById('saveAllFlightsBtn');
	const loginModal = document.getElementById('loginModal');

	// Store search results for save all functionality
	let currentSearchResults = [];

	// Create toast container if it doesn't exist
	createToastContainer();

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
				showToast('There was an error retrieving flights');
				return;
			}

			const flights = await response.json();
			resultsList.innerHTML = '';
			currentSearchResults = flights; // Store the search results

			// Check if user is logged in to show save options
			const isAuthenticated = isUserAuthenticated();
			if (isAuthenticated && flights.length > 0) {
				saveAllFlightsBtn.style.display = 'inline-block';
			} else {
				saveAllFlightsBtn.style.display = 'none';
			}

			if (flights.length === 0) {
				resultsList.innerHTML =
					'<li class="list-group-item text-center py-4">No flights found matching your criteria.</li>';
				showToast('No flights found matching your criteria.', 'info');
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

				// Add event listeners to save buttons
				document.querySelectorAll('.save-flight-btn').forEach((btn) => {
					btn.addEventListener('click', handleSaveFlight);
				});

				// Show success toast with the number of flights found
				showToast(
					`Found ${flights.length} flights matching your criteria.`,
					'success'
				);
			}

			flightResults.style.display = 'block';

			// Scroll to results
			flightResults.scrollIntoView({ behavior: 'smooth' });
		} catch (error) {
			showToast('Error: ' + error.message, 'error');
			// Reset button state
			searchButton.innerHTML =
				'<i class="material-icons align-middle me-1">search</i> Search Flights';
			searchButton.disabled = false;
		}
	});

	// Add event listener for Save All Flights button
	if (saveAllFlightsBtn) {
		saveAllFlightsBtn.addEventListener('click', handleSaveAllFlights);
	}

	// Sort button functionality
	const sortPriceBtn = document.getElementById('sortPriceBtn');
	if (sortPriceBtn) {
		sortPriceBtn.addEventListener('click', () => {
			if (currentSearchResults.length === 0) return;

			// Toggle sort order
			sortPriceBtn.dataset.order =
				sortPriceBtn.dataset.order === 'asc' ? 'desc' : 'asc';
			const order = sortPriceBtn.dataset.order;

			// Sort the current results
			currentSearchResults.sort((a, b) => {
				const priceA = parseFloat(a.price);
				const priceB = parseFloat(b.price);
				return order === 'asc' ? priceA - priceB : priceB - priceA;
			});

			// Update the UI with sorted results
			updateFlightResults(currentSearchResults);

			// Update button text to indicate sort order
			sortPriceBtn.innerHTML = `
				<i class="material-icons align-middle">${
					order === 'asc' ? 'arrow_upward' : 'arrow_downward'
				}</i>
				Sort by Price ${order === 'asc' ? '(Low to High)' : '(High to Low)'}
			`;
		});
	}

	// Check if user is authenticated
	function isUserAuthenticated() {
		return localStorage.getItem('jwtToken') !== null;
	}

	// Get auth headers for API requests
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

	// Handle saving a single flight
	async function handleSaveFlight(event) {
		event.preventDefault();

		// Check if user is authenticated
		if (!isUserAuthenticated()) {
			// Show login modal
			const bsLoginModal = new bootstrap.Modal(loginModal);
			bsLoginModal.show();
			return;
		}

		const index = parseInt(event.currentTarget.getAttribute('data-index'));
		const flight = currentSearchResults[index];

		try {
			const saveResponse = await saveFlight(flight);
			if (saveResponse.success) {
				showToast('Flight saved successfully!', 'success');
				// Update button to indicate flight is saved
				event.currentTarget.innerHTML = `
					<i class="material-icons align-middle" style="font-size: 16px;">favorite</i> Saved
				`;
				event.currentTarget.disabled = true;
				event.currentTarget.classList.remove('btn-outline-primary');
				event.currentTarget.classList.add('btn-success');
			} else {
				showToast(
					saveResponse.message || 'Failed to save flight',
					'error'
				);
			}
		} catch (error) {
			showToast('Error: ' + error.message, 'error');
		}
	}

	// Handle saving all flights
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
			showToast('No flights to save', 'info');
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

			// Show success message
			showToast(`Successfully saved ${successful} flights!`, 'success');

			// Update Save All button
			saveAllFlightsBtn.innerHTML = `
				<i class="material-icons align-middle">favorite</i> All Saved
			`;
			saveAllFlightsBtn.disabled = true;
		} catch (error) {
			showToast('Error: ' + error.message, 'error');

			// Reset button
			saveAllFlightsBtn.disabled = false;
			saveAllFlightsBtn.innerHTML = `
				<i class="material-icons align-middle">favorite_border</i> Save All
			`;
		}
	}

	// API call to save a flight
	async function saveFlight(flight) {
		try {
			const response = await fetch('/api/saved-flights', {
				method: 'POST',
				headers: getAuthHeaders(),
				body: JSON.stringify({
					airlineCode: flight.airlineCode,
					airlineName: flight.airline,
					flightNumber: flight.flightNumber,
					origin: flight.departure,
					destination: flight.arrival,
					departureTime:
						flight.departureTimeRaw || flight.departureTime,
					arrivalTime: flight.arrivalTimeRaw || flight.arrivalTime,
					price: parseFloat(flight.price),
				}),
			});

			if (response.ok) {
				const data = await response.json();
				return { success: true, data };
			} else {
				if (response.status === 401) {
					// Unauthorized - token might be expired
					localStorage.removeItem('jwtToken');
					const bsLoginModal = new bootstrap.Modal(loginModal);
					bsLoginModal.show();
					return {
						success: false,
						message: 'Please log in to save flights',
					};
				} else {
					const errorData = await response.json();
					return {
						success: false,
						message: errorData.message || 'Failed to save flight',
					};
				}
			}
		} catch (error) {
			console.error('Error saving flight:', error);
			return { success: false, message: error.message };
		}
	}

	// Update flight results in the UI
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

		// Re-attach event listeners
		document.querySelectorAll('.save-flight-btn').forEach((btn) => {
			btn.addEventListener('click', handleSaveFlight);
		});
	}

	// Create toast container
	function createToastContainer() {
		const toastContainer = document.createElement('div');
		toastContainer.className =
			'toast-container position-fixed bottom-0 end-0 p-3';
		toastContainer.id = 'toastContainer';
		document.body.appendChild(toastContainer);
	}

	// Function to show toast messages
	function showToast(message, type = 'info') {
		const toastContainer = document.getElementById('toastContainer');
		const toast = document.createElement('div');
		const id = 'toast-' + Date.now();

		// Set toast classes based on type
		let bgClass, iconName;
		switch (type) {
			case 'success':
				bgClass = 'bg-success text-white';
				iconName = 'check_circle';
				break;
			case 'error':
				bgClass = 'bg-danger text-white';
				iconName = 'error';
				break;
			case 'warning':
				bgClass = 'bg-warning';
				iconName = 'warning';
				break;
			default:
				bgClass = 'bg-info text-white';
				iconName = 'info';
		}

		toast.className = `toast ${bgClass} border-0 show`;
		toast.id = id;
		toast.setAttribute('role', 'alert');
		toast.setAttribute('aria-live', 'assertive');
		toast.setAttribute('aria-atomic', 'true');

		toast.innerHTML = `
			<div class="toast-header ${bgClass} border-0">
				<i class="material-icons me-2">${iconName}</i>
				<strong class="me-auto">Flight Search</strong>
				<button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
			</div>
			<div class="toast-body">
				${message}
			</div>
		`;

		toastContainer.appendChild(toast);

		// Initialize Bootstrap toast
		const bsToast = new bootstrap.Toast(toast, {
			autohide: true,
			delay: 5000,
		});

		bsToast.show();

		// Remove toast from DOM after it's hidden
		toast.addEventListener('hidden.bs.toast', () => {
			toast.remove();
		});
	}
});

document.addEventListener('DOMContentLoaded', () => {
	const tripType = document.getElementById('tripType');
	const returnDateGroup = document.getElementById('returnDateGroup');
	const flightSearchForm = document.getElementById('flightSearchForm');
	const flightResults = document.getElementById('flightResults');
	const resultsList = document.getElementById('resultsList');
	const searchButton = document.querySelector('button[type="submit"]');

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
				showToast('Error: ' + error, 'error');
				return;
			}

			const flights = await response.json();
			resultsList.innerHTML = '';

			if (flights.length === 0) {
				resultsList.innerHTML =
					'<li class="list-group-item text-center py-4">No flights found matching your criteria.</li>';
				showToast('No flights found matching your criteria.', 'info');
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

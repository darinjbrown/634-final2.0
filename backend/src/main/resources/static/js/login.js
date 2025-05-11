/**
 * SkyExplorer Login Form Handler
 *
 * This script manages the login form functionality including:
 * - Form submission via AJAX
 * - JWT token storage
 * - Login error handling
 * - Success/failure notifications
 * - URL parameter processing for login-related messages
 */
document.addEventListener('DOMContentLoaded', function () {
	// Get form elements for interaction
	const loginForm = document.getElementById('loginForm');
	const loginError = document.getElementById('loginError');

	/**
	 * Set up the login form submission handler
	 * Prevents default form submission and uses fetch API instead
	 */
	if (loginForm) {
		loginForm.addEventListener('submit', function (event) {
			event.preventDefault();

			// Collect form data for the login request
			const formData = {
				username: document.getElementById('username').value,
				password: document.getElementById('password').value,
				rememberMe: document.getElementById('rememberMe').checked,
			};

			// Attempt user authentication
			loginUser(formData);
		});
	}

	/**
	 * Authenticate user via the backend API
	 * Sends credentials to the server and handles the JWT token response
	 *
	 * @param {Object} userData - User credentials with username, password and rememberMe flag
	 */
	function loginUser(userData) {
		fetch('/api/auth/login', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				// CSRF token is automatically added by csrf.js
			},
			body: JSON.stringify(userData),
		})
			.then((response) => {
				if (!response.ok) {
					throw new Error('Login failed');
				}
				return response.json();
			})
			.then((data) => {
				if (data.token) {
					// Store JWT token in localStorage for authentication
					localStorage.setItem('jwtToken', data.token);

					// Redirect to home page or the page specified in the redirect parameter
					const redirect = new URLSearchParams(
						window.location.search
					).get('redirect');
					window.location.href = redirect || '/';
				} else {
					showError(
						'Authentication failed. Please check your credentials.'
					);
				}
			})
			.catch((error) => {
				showError('Invalid username or password');
				console.error('Login error:', error);
			});
	}

	/**
	 * Display an error message to the user
	 * Creates or updates the error alert element
	 *
	 * @param {string} message - The error message to display
	 */
	function showError(message) {
		if (loginError) {
			loginError.textContent = message;
			loginError.style.display = 'block';

			// Auto-hide the error message after 5 seconds
			setTimeout(() => {
				loginError.style.display = 'none';
			}, 5000);
		}
	}

	/**
	 * Display a success message to the user
	 * Creates or updates the success alert element
	 *
	 * @param {string} message - The success message to display
	 */
	function showSuccess(message) {
		// Create success alert if it doesn't exist
		let successAlert = document.getElementById('loginSuccess');
		if (!successAlert) {
			successAlert = document.createElement('div');
			successAlert.id = 'loginSuccess';
			successAlert.className = 'alert alert-success';

			// Insert before the form for better visibility
			loginForm.parentNode.insertBefore(successAlert, loginForm);
		}

		successAlert.textContent = message;
		successAlert.style.display = 'block';

		// Auto-hide after 5 seconds
		setTimeout(() => {
			successAlert.style.display = 'none';
		}, 5000);
	}

	/**
	 * Process URL parameters for login-related messages
	 * Handles error messages, registration confirmation, and logout notifications
	 * from redirects to the login page
	 */
	function checkUrlParams() {
		const urlParams = new URLSearchParams(window.location.search);

		// Show error message if present in URL
		if (urlParams.has('error')) {
			showError(urlParams.get('error'));
		}

		// Show success message for completed registration
		if (
			urlParams.has('registered') &&
			urlParams.get('registered') === 'true'
		) {
			showSuccess(
				'Registration successful! You can now log in with your credentials.'
			);
		}

		// Show success message after logout
		if (urlParams.has('logout') && urlParams.get('logout') === 'true') {
			showSuccess('You have been successfully logged out.');
		}
	}

	// Check URL parameters on page load to display appropriate messages
	checkUrlParams();
});

/**
 * Login form handling
 */
document.addEventListener('DOMContentLoaded', function () {
	// Get form elements
	const loginForm = document.getElementById('loginForm');
	const loginError = document.getElementById('loginError');

	// Handle form submission
	if (loginForm) {
		loginForm.addEventListener('submit', function (event) {
			event.preventDefault();

			// Collect form data
			const formData = {
				username: document.getElementById('username').value,
				password: document.getElementById('password').value,
				rememberMe: document.getElementById('rememberMe').checked,
			};

			// Call the API to authenticate the user
			loginUser(formData);
		});
	}

	/**
	 * Login a user via API
	 * @param {Object} userData - The user login data
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
					// Store token in localStorage for subsequent requests
					localStorage.setItem('jwtToken', data.token);

					// Redirect to home page or previous page if available
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
	 * Display an error message
	 * @param {string} message - The error message to display
	 */
	function showError(message) {
		if (loginError) {
			loginError.textContent = message;
			loginError.style.display = 'block';

			// Auto-hide after 5 seconds
			setTimeout(() => {
				loginError.style.display = 'none';
			}, 5000);
		}
	}

	/**
	 * Display a success message
	 * @param {string} message - The success message to display
	 */
	function showSuccess(message) {
		// Create success alert if it doesn't exist
		let successAlert = document.getElementById('loginSuccess');
		if (!successAlert) {
			successAlert = document.createElement('div');
			successAlert.id = 'loginSuccess';
			successAlert.className = 'alert alert-success';

			// Insert before the form
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
	 * Check URL parameters for messages
	 */
	function checkUrlParams() {
		const urlParams = new URLSearchParams(window.location.search);

		if (urlParams.has('error')) {
			showError(urlParams.get('error'));
		}

		if (
			urlParams.has('registered') &&
			urlParams.get('registered') === 'true'
		) {
			showSuccess(
				'Registration successful! You can now log in with your credentials.'
			);
		}

		if (urlParams.has('logout') && urlParams.get('logout') === 'true') {
			showSuccess('You have been successfully logged out.');
		}
	}

	// Check URL parameters on page load
	checkUrlParams();
});

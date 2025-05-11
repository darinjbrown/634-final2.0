/**
 * Registration form handling
 */
document.addEventListener('DOMContentLoaded', function () {
	// Get form elements
	const registerForm = document.getElementById('registerForm');
	const registerError = document.getElementById('registerError');

	// Handle form submission
	if (registerForm) {
		registerForm.addEventListener('submit', function (event) {
			event.preventDefault();

			// Validate form
			const password = document.getElementById('password').value;
			const confirmPassword =
				document.getElementById('confirmPassword').value;

			// Check if passwords match
			if (password !== confirmPassword) {
				showError('Passwords do not match');
				return;
			}

			// Check password strength (at least 8 characters with letters and numbers)
			if (
				password.length < 8 ||
				!/[A-Za-z]/.test(password) ||
				!/[0-9]/.test(password)
			) {
				showError(
					'Password must be at least 8 characters and include letters and numbers'
				);
				return;
			}

			// Collect form data
			const formData = {
				username: document.getElementById('username').value,
				email: document.getElementById('email').value,
				password: password,
				firstName: document.getElementById('firstName').value,
				lastName: document.getElementById('lastName').value,
			};

			// Call the API to register the user
			registerUser(formData);
		});
	}

	/**
	 * Register a new user via API
	 * @param {Object} userData - The user registration data
	 */
	function registerUser(userData) {
		fetch('/api/auth/register', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				// CSRF token is automatically added by csrf.js
			},
			body: JSON.stringify(userData),
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.error) {
					showError(data.error);
				} else {
					// Registration successful, redirect to login page with success message
					window.location.href = '/login?registered=true';
				}
			})
			.catch((error) => {
				showError('Registration failed. Please try again later.');
				console.error('Registration error:', error);
			});
	}

	/**
	 * Display an error message
	 * @param {string} message - The error message to display
	 */
	function showError(message) {
		if (registerError) {
			registerError.textContent = message;
			registerError.style.display = 'block';

			// Auto-hide after 5 seconds
			setTimeout(() => {
				registerError.style.display = 'none';
			}, 5000);
		}
	}

	/**
	 * Check URL parameters for messages
	 */
	function checkUrlParams() {
		const urlParams = new URLSearchParams(window.location.search);

		if (urlParams.has('error')) {
			showError(urlParams.get('error'));
		}
	}

	// Check URL parameters on page load
	checkUrlParams();
});

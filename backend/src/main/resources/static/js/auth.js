/**
 * Authentication script for SkyExplorer
 * Handles login/logout UI state and authentication-related functionality
 */

document.addEventListener('DOMContentLoaded', () => {
	// DOM elements for authentication UI
	const loginNavItem = document.getElementById('loginNavItem');
	const registerNavItem = document.getElementById('registerNavItem');
	const userDropdown = document.getElementById('userDropdown');
	const usernameElement = document.getElementById('username');
	const logoutBtn = document.getElementById('logoutBtn');
	const adminNavItem = document.getElementById('adminNavItem');
	const loginModal = new bootstrap.Modal(
		document.getElementById('loginModal')
	);

	// Auth-protected elements
	const saveAllFlightsBtn = document.getElementById('saveAllFlightsBtn');

	// Check authentication status on page load
	checkAuthStatus();

	// Add event listener for logout button
	if (logoutBtn) {
		logoutBtn.addEventListener('click', (e) => {
			e.preventDefault();
			logout();
		});
	}

	/**
	 * Check if user is authenticated and update UI accordingly
	 */
	async function checkAuthStatus() {
		try {
			const response = await fetch('/api/auth/me', {
				method: 'GET',
				headers: {
					'Content-Type': 'application/json',
				},
				credentials: 'include', // Important for cookies
			});

			if (response.ok) {
				const userData = await response.json();
				// User is authenticated
				showAuthenticatedUI(userData);

				// Check if user is admin
				checkAdminStatus(userData);

				// Show protected elements
				if (saveAllFlightsBtn) {
					saveAllFlightsBtn.style.display = 'inline-block';
				}

				return true;
			} else {
				// User is not authenticated
				showUnauthenticatedUI();
				return false;
			}
		} catch (error) {
			console.error('Error checking authentication status:', error);
			showUnauthenticatedUI();
			return false;
		}
	}

	/**
	 * Update UI for authenticated users
	 * @param {Object} userData - User data from API
	 */
	function showAuthenticatedUI(userData) {
		if (loginNavItem) loginNavItem.classList.add('d-none');
		if (registerNavItem) registerNavItem.classList.add('d-none');
		if (userDropdown) {
			userDropdown.classList.remove('d-none');
			if (usernameElement && userData.username) {
				usernameElement.textContent = userData.username;
			}
		}
	}

	/**
	 * Update UI for unauthenticated users
	 */
	function showUnauthenticatedUI() {
		if (loginNavItem) loginNavItem.classList.remove('d-none');
		if (registerNavItem) registerNavItem.classList.remove('d-none');
		if (userDropdown) userDropdown.classList.add('d-none');
		if (adminNavItem) adminNavItem.classList.add('d-none');

		// Hide protected elements
		if (saveAllFlightsBtn) {
			saveAllFlightsBtn.style.display = 'none';
		}
	}

	/**
	 * Check if user has admin role and update UI accordingly
	 * @param {Object} userData - User data from API
	 */
	async function checkAdminStatus(userData) {
		try {
			const response = await fetch('/api/auth/has-role/ADMIN', {
				method: 'GET',
				headers: {
					'Content-Type': 'application/json',
				},
				credentials: 'include',
			});

			if (response.ok && (await response.json()) === true) {
				// User is admin
				if (adminNavItem) adminNavItem.classList.remove('d-none');
			}
		} catch (error) {
			console.error('Error checking admin status:', error);
		}
	}

	/**
	 * Log out the current user
	 */
	async function logout() {
		try {
			const response = await fetch('/api/auth/logout', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				credentials: 'include',
			});

			if (response.ok) {
				// Redirect to home page after successful logout
				window.location.href = '/';
			} else {
				console.error('Logout failed');
			}
		} catch (error) {
			console.error('Error during logout:', error);
		}
	}

	/**
	 * Show login modal for protected actions
	 */
	window.showLoginRequiredModal = function () {
		loginModal.show();
	};

	/**
	 * Check if user is authenticated, show login modal if not
	 * @returns {boolean} True if authenticated, false otherwise
	 */
	window.requireAuthentication = async function () {
		const isAuthenticated = await checkAuthStatus();
		if (!isAuthenticated) {
			loginModal.show();
		}
		return isAuthenticated;
	};
});

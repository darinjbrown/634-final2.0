/**
 * SkyExplorer Authentication Module
 *
 * This script manages the application's authentication state and UI interactions including:
 * - Checking user authentication status on page load
 * - Updating UI elements based on authentication state
 * - Handling user logout
 * - Checking for admin privileges
 * - Providing authentication utilities for other scripts
 * - Managing protected content visibility
 */

document.addEventListener('DOMContentLoaded', () => {
	// DOM element references for authentication-related UI components
	const loginNavItem = document.getElementById('loginNavItem');
	const registerNavItem = document.getElementById('registerNavItem');
	const userDropdown = document.getElementById('userDropdown');
	const usernameElement = document.getElementById('username');
	const logoutBtn = document.getElementById('logoutBtn');
	const adminNavItem = document.getElementById('adminNavItem');
	const loginModal = document.getElementById('loginModal')
		? new bootstrap.Modal(document.getElementById('loginModal'))
		: null;

	// Elements that should only be visible to authenticated users
	const saveAllFlightsBtn = document.getElementById('saveAllFlightsBtn');
	
	// Save flight buttons may be added dynamically, but the container is present
	const flightResultsList = document.getElementById('resultsList');

	/**
	 * Verify authentication status when page loads
	 * This ensures UI elements are properly shown/hidden based on current auth state
	 */
	checkAuthStatus();

	/**
	 * Set up event handler for logout button click
	 * Prevents default anchor behavior and calls the logout function
	 */
	if (logoutBtn) {
		logoutBtn.addEventListener('click', (e) => {
			e.preventDefault();
			logout();
		});
	}

	/**
	 * Prepare authentication headers for API requests
	 * Automatically includes JWT token from localStorage if available
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
	 * Verify if the current user is authenticated by checking JWT token
	 * and validating it with the server
	 *
	 * This function:
	 * 1. Checks for token in localStorage
	 * 2. Validates token with backend API
	 * 3. Updates UI based on authentication status
	 * 4. Checks for admin privileges if authenticated
	 *
	 * @returns {Promise<boolean>} True if user is authenticated, false otherwise
	 */
	async function checkAuthStatus() {
		try {
			const token = localStorage.getItem('jwtToken');
			if (!token) {
				// No token found, user is not authenticated
				showUnauthenticatedUI();
				return false;
			}

			// Validate token with backend API
			const response = await fetch('/api/auth/me', {
				method: 'GET',
				headers: getAuthHeaders(),
				credentials: 'include', // Include cookies for remember-me functionality
			});

			if (response.ok) {
				const userData = await response.json();
				// User is authenticated, update UI accordingly
				showAuthenticatedUI(userData);

				// Check if user has admin privileges
				checkAdminStatus(userData);

				// Show elements restricted to authenticated users
				if (saveAllFlightsBtn) {
					saveAllFlightsBtn.style.display = 'inline-block';
				}

				// Enable any save flight buttons that may be present
				enableSaveButtons();
				
				// Store validated user data for other components to use
				window.currentUser = userData;

				return true;
			} else {
				// For development/debugging - don't automatically clear token on localhost
				const isLocalhost = window.location.hostname === 'localhost' || 
				                    window.location.hostname === '127.0.0.1';
				
				if (!isLocalhost) {
					// Only remove the token in production environment
					localStorage.removeItem('jwtToken');
				} else {
					// In development, keep the token but log the error
					console.warn("Auth validation failed, but keeping token for development purposes");
				}
				
				// Still show unauthenticated UI
				showUnauthenticatedUI();
				
				// If response was 401 or 403, the token is definitely invalid
				if (response.status === 401 || response.status === 403) {
					console.warn("Authentication token validation failed with status:", response.status);
					
					// Optionally show a message, but don't in development mode
					if (!isLocalhost && typeof showToast === 'function') {
						showToast('Your session has expired. Please log in again.', 'warning');
					}
				}
				
				return false;
			}
		} catch (error) {
			console.error('Error checking authentication status:', error);
			showUnauthenticatedUI();
			return false;
		}
	}

	/**
	 * Update UI elements to reflect authenticated user state
	 * Shows user-specific elements and hides login/register options
	 *
	 * @param {Object} userData - User data object containing username, email, etc.
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
	 * Update UI elements to reflect unauthenticated state
	 * Shows login/register options and hides user-specific elements
	 */
	function showUnauthenticatedUI() {
		if (loginNavItem) loginNavItem.classList.remove('d-none');
		if (registerNavItem) registerNavItem.classList.remove('d-none');
		if (userDropdown) userDropdown.classList.add('d-none');
		if (adminNavItem) adminNavItem.classList.add('d-none');

		// Hide elements restricted to authenticated users
		if (saveAllFlightsBtn) {
			saveAllFlightsBtn.style.display = 'none';
		}
		
		// Disable any save buttons that may exist
		disableSaveButtons();
		
		// Clear the user data
		window.currentUser = null;
	}
	
	/**
	 * Enable save buttons for authenticated users
	 */
	function enableSaveButtons() {
		// If flight results exist, ensure save buttons are enabled
		if (flightResultsList) {
			document.querySelectorAll('.save-flight-btn').forEach(btn => {
				btn.disabled = false;
				btn.title = 'Save this flight to your account';
			});
		}
	}
	
	/**
	 * Disable save buttons for unauthenticated users
	 */
	function disableSaveButtons() {
		// If flight results exist, disable all save buttons
		if (flightResultsList) {
			document.querySelectorAll('.save-flight-btn:not(.btn-success)').forEach(btn => {
				// Don't disable buttons that are already saved (have btn-success class)
				if (!btn.classList.contains('btn-success')) {
					btn.disabled = false; // We actually want to keep them enabled so they can trigger the login modal
					btn.title = 'Log in to save flights';
				}
			});
		}
	}

	/**
	 * Check if the current user has admin role permissions
	 * Makes API call to role-check endpoint and updates UI accordingly
	 *
	 * @param {Object} userData - User data from authentication check
	 */
	async function checkAdminStatus(userData) {
		try {
			const response = await fetch('/api/auth/has-role/ADMIN', {
				method: 'GET',
				headers: getAuthHeaders(),
				credentials: 'include',
			});

			if (response.ok) {
				const result = await response.json();
				if (result && (result === true || result.hasRole === true)) {
					// User has admin role, show admin navigation
					if (adminNavItem) adminNavItem.classList.remove('d-none');
					
					// Store admin status
					window.isCurrentUserAdmin = true;
					return true;
				}
			}
			window.isCurrentUserAdmin = false;
			return false;
		} catch (error) {
			console.error('Error checking admin status:', error);
			window.isCurrentUserAdmin = false;
			return false;
		}
	}

	/**
	 * Log out the current user
	 * Calls logout API endpoint, clears JWT token, and redirects to home page
	 */
	async function logout() {
		try {
			const response = await fetch('/api/auth/logout', {
				method: 'POST',
				headers: getAuthHeaders(),
				credentials: 'include',
			});

			// Always clear local storage token and user data, even if server logout fails
			localStorage.removeItem('jwtToken');
			window.currentUser = null;
			window.isCurrentUserAdmin = false;

			if (response.ok) {
				// Redirect to home page after successful logout
				window.location.href = '/';
			} else {
				console.error('Logout failed');
				// Still redirect to home page
				window.location.href = '/';
			}
		} catch (error) {
			console.error('Error during logout:', error);
			// Clear token and redirect even if there's an error
			localStorage.removeItem('jwtToken');
			window.location.href = '/';
		}
	}

	/**
	 * Show login modal for protected actions
	 * Exported as global function for use by other scripts
	 */
	window.showLoginRequiredModal = function () {
		if (loginModal) {
			loginModal.show();
		} else {
			// Fallback if modal not available - redirect to login page
			window.location.href = '/login';
		}
	};

	/**
	 * Check if user is authenticated and show login modal if not
	 * Exported as global function for guarding protected features
	 *
	 * @returns {Promise<boolean} True if authenticated, false otherwise
	 */
	window.requireAuthentication = async function () {
		const isAuthenticated = await checkAuthStatus();
		if (!isAuthenticated) {
			if (loginModal) {
				loginModal.show();
			} else {
				// Fallback if modal not available
				window.location.href = '/login';
			}
		}
		return isAuthenticated;
	};
	
	/**
	 * Export important authentication functions to global scope
	 * This allows other scripts to access these functions
	 */
	window.checkAuthStatus = checkAuthStatus;
	window.getAuthHeaders = getAuthHeaders;
	
	/**
	 * Allow manual setting of JWT token for development/debugging purposes
	 * This function is only intended for development use
	 * 
	 * @param {string} token - The JWT token to set
	 */
	window.setAuthToken = function(token) {
		if (token && typeof token === 'string') {
			localStorage.setItem('jwtToken', token);
			console.log("JWT token manually set. Refreshing authentication status...");
			checkAuthStatus();
			return true;
		}
		return false;
	};
});

/**
 * CSRF Protection Utility
 *
 * This script provides utilities for handling CSRF tokens in forms and AJAX requests.
 * It automatically extracts CSRF tokens from cookies and adds them to requests.
 */

// Self-executing function to avoid global scope pollution
(function () {
	'use strict';

	/**
	 * Get CSRF token from cookies
	 * @returns {string|null} CSRF token if found, null otherwise
	 */
	function getCsrfToken() {
		const cookies = document.cookie.split(';');
		for (let i = 0; i < cookies.length; i++) {
			let cookie = cookies[i].trim();
			if (cookie.startsWith('XSRF-TOKEN=')) {
				return decodeURIComponent(cookie.substring(11));
			}
		}
		return null;
	}

	/**
	 * Add CSRF token to headers for fetch API requests
	 * @param {Object} headers - Headers object to add the CSRF token to
	 * @returns {Object} Headers object with CSRF token added
	 */
	function addCsrfToHeaders(headers = {}) {
		const token = getCsrfToken();
		if (token) {
			headers['X-CSRF-TOKEN'] = token;
		}
		return headers;
	}

	/**
	 * Returns the header object with CSRF token
	 * @returns {Object} Header object with X-CSRF-TOKEN
	 */
	function getCsrfHeader() {
		const token = getCsrfToken();
		return token ? { 'X-CSRF-TOKEN': token } : {};
	}

	/**
	 * Initialize CSRF protection for all forms and AJAX requests
	 */
	function initializeCsrfProtection() {
		// Add CSRF tokens to all forms
		document.addEventListener('DOMContentLoaded', function () {
			// Handle standard forms
			const forms = document.querySelectorAll('form');
			forms.forEach(function (form) {
				// Don't add token to forms with method GET
				if (form.method.toLowerCase() !== 'get') {
					const token = getCsrfToken();
					if (token) {
						// Check if CSRF field already exists
						let csrfInput = form.querySelector(
							'input[name="_csrf"]'
						);
						if (!csrfInput) {
							csrfInput = document.createElement('input');
							csrfInput.type = 'hidden';
							csrfInput.name = '_csrf';
							form.appendChild(csrfInput);
						}
						csrfInput.value = token;
					}
				}
			});
		});

		// Override fetch API to include CSRF token
		const originalFetch = window.fetch;
		window.fetch = function (url, options = {}) {
			// Only modify same-origin requests
			if (url.startsWith('/') || url.startsWith(window.location.origin)) {
				if (!options.headers) options.headers = {};
				options.headers = addCsrfToHeaders(options.headers);
			}
			return originalFetch.call(this, url, options);
		};

		// Override XMLHttpRequest to include CSRF token
		const originalOpen = XMLHttpRequest.prototype.open;
		XMLHttpRequest.prototype.open = function (method, url) {
			const xhr = this;
			originalOpen.apply(this, arguments);

			if (method.toLowerCase() !== 'get') {
				xhr.addEventListener('readystatechange', function () {
					if (xhr.readyState === 1) {
						// OPENED
						const token = getCsrfToken();
						if (token) {
							xhr.setRequestHeader('X-CSRF-TOKEN', token);
						}
					}
				});
			}
		};
	}

	// Initialize CSRF protection
	initializeCsrfProtection();

	// Expose utility functions to window object
	window.csrfUtils = {
		getCsrfToken,
		addCsrfToHeaders,
		getCsrfHeader,
	};
})();

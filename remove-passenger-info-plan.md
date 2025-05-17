# Plan to Remove Passenger Information Section from Booking Card

## Overview
This plan details how to remove the passenger information section from the booking cards displayed in the "My Bookings" page.

## Current Implementation Analysis
- In the `createBookingCard()` function in bookings.js, there is a section that adds passenger information to each booking card
- This shows the passenger name and email in the card UI

## Implementation Steps

### Step 1: Identify and Remove Passenger Information HTML
1. **Locate code**: Find the passenger information section in the `createBookingCard()` function in bookings.js
2. **Remove HTML**: Remove the HTML that displays the passenger information column
3. **Adjust layout**: Make sure the remaining content (status information) spreads across the full width
4. **Verify changes**: Load the bookings page and confirm the passenger information no longer appears

## Verification Plan
After making the change:
1. Load the bookings page 
2. Check that booking cards no longer show passenger information
3. Verify that the status information takes up the full width
4. Check browser console for any JavaScript errors

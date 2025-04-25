### Testing the /api/flights/search Endpoint

You can test the `/api/flights/search` endpoint using Postman or cURL.

#### Using Postman
1. Open Postman and create a new POST request.
2. Set the URL to `http://localhost:8081/api/flights/search`.
3. In the Headers tab, add a header with `Key: Content-Type` and `Value: application/json`.
4. In the Body tab, select `raw` and enter the following JSON:
   ```json
   {
       "startingLocation": "JFK",
       "endingLocation": "LAX",
       "travelDate": "2025-05-01",
       "returnDate": "2025-05-10",
       "numberOfTravelers": 1,
       "tripType": "round-trip"
   }
   ```
5. Click Send to test the endpoint.

#### Using cURL
Run the following command in your terminal:
```bash
curl -X POST http://localhost:8081/api/flights/search \
     -H "Content-Type: application/json" \
     -d '{
         "startingLocation": "JFK",
         "endingLocation": "LAX",
         "travelDate": "2025-05-01",
         "returnDate": "2025-05-10",
         "numberOfTravelers": 1,
         "tripType": "round-trip"
     }'
```
// src/services/ApiService.js

const API_URL = 'http://localhost:8082';

const ApiService = {
  getResource: async () => {
    try {
      const response = await fetch(`${API_URL}/api/status/check`, {
        method: 'GET',
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching resource:', error);
      throw error;
    }
  },

  // Get Keycloak token using client credentials flow
  getToken: async () => {
    try {
      const response = await fetch(`${API_URL}/api/auth/token`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`Failed to obtain token. Status: ${response.status}`);
      }

      const data = await response.json();
      console.log('Token obtained successfully');
      
      // Make sure we return just the access_token string
      return data.access_token;
    } catch (error) {
      console.error('Error getting token:', error);
      throw error;
    }
  },

  // Protected resource - requires authentication
  getProtectedResource: async () => {
    try {
      // First get the token
      const token = await ApiService.getToken();
      
      if (!token) {
        throw new Error('No token received from server');
      }
      
      console.log(`Using token: ${token.substring(0, 20)}... (${token.length} chars)`);
      
      // Then use it to access the protected resource
      const response = await fetch(`${API_URL}/api/protected-resource`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        // Try to get more information about the error
        try {
          const errorBody = await response.json();
          console.error('Protected resource error details:', errorBody);
        } catch (e) {
          console.error('Could not parse error response');
        }
        
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching protected resource:', error);
      throw error;
    }
  }
};

export default ApiService;
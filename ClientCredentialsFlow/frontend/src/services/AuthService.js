// src/services/AuthService.js
import axios from 'axios';

const KEYCLOAK_URL = 'http://localhost:8080';
const REALM = 'app-realm';
const CLIENT_ID = 'react-client';
const CLIENT_SECRET = 'Rp9Q05iuxUdNxf2t76OqFUUJkIi5TNpG';

class AuthService {
  getAccessToken = async () => {
    try {
      const tokenEndpoint = `${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token`;
      
      const params = new URLSearchParams();
      params.append('grant_type', 'client_credentials');
      params.append('client_id', CLIENT_ID);
      params.append('client_secret', CLIENT_SECRET);
      
      const response = await axios.post(tokenEndpoint, params, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      });
      
      return response.data.access_token;
    } catch (error) {
      console.error('Error getting access token:', error);
      throw error;
    }
  };
}

// eslint-disable-next-line import/no-anonymous-default-export
export default new AuthService();
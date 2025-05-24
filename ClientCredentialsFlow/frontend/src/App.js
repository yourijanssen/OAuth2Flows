import React, { useState } from 'react';
import ApiService from './services/ApiService';
import './App.css';

function App() {
  const [resource, setResource] = useState(null);
  const [protectedResource, setProtectedResource] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchResource = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await ApiService.getResource();
      console.log('Fetched resource:', data);
      setResource(data);
    } catch (err) {
      setError('Failed to fetch resource: ' + (err.message || 'Unknown error'));
    } finally {
      setLoading(false);
    }
  };

  const fetchProtectedResource = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await ApiService.getProtectedResource();
      console.log('Fetched protected resource:', data);
      setProtectedResource(data);
    } catch (err) {
      setError('Failed to fetch protected resource: ' + (err.message || 'Unknown error'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>OAuth2 Client Credentials Demo</h1>
        <div className="description">
        <p>This demo uses the OAuth2 Client Credentials flow.</p>
        <p>No user login is required - authentication happens between services.</p>
        </div>
        
        <div className="button-container">
          <button 
            onClick={fetchResource} 
            disabled={loading}
            className="fetch-button"
          >
            {loading ? 'Loading...' : 'Fetch Open Resource'}
          </button>
          
          <button 
            onClick={fetchProtectedResource} 
            disabled={loading}
            className="fetch-button"
          >
            {loading ? 'Loading...' : 'Fetch Protected Resource'}
          </button>
        </div>
        
        {error && <div className="error-message">{error}</div>}
        
        {resource && (
          <div className="resource-container">
            <h2>Open Resource Data:</h2>
            <div className="resource-data">
              <pre>{JSON.stringify(resource, null, 2)}</pre>
            </div>
          </div>
        )}
        
        {protectedResource && (
          <div className="resource-container">
            <h2>Protected Resource Data:</h2>
            <div className="resource-data">
              <pre>{JSON.stringify(protectedResource, null, 2)}</pre>
            </div>
          </div>
        )}
      </header>
    </div>
  );
}

export default App;
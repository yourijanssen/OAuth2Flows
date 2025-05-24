import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Check if user is authenticated
    fetchUserDetails();
  }, []);

  const fetchUserDetails = async () => {
    try {
      const response = await axios.get('/api/google/user', { withCredentials: true });
      setUser(response.data);
      setError(null);
    } catch (err) {
      if (err.response && err.response.status === 401) {
        // User is not authenticated, this is normal
        setUser(null);
      } else {
        setError('Error fetching user details');
        console.error(err);
      }
    } finally {
      setLoading(false);
    }
  };

// In App.js, modify the handleLogin function:
const handleLogin = () => {
  console.log("Using client ID: 1096562824511-5hl...");
  
  // Use the full backend URL instead of a relative path
  window.location.href = 'http://localhost:8080/oauth2/authorization/google?client_registration_id=authorizationCodeFlow';
};

  const handleLogout = async () => {
    try {
      await axios.post('/logout', {}, { withCredentials: true });
      setUser(null);
    } catch (err) {
      console.error('Logout error:', err);
    }
  };

  if (loading) {
    return <div className="App">Loading...</div>;
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1>Google OAuth 2.0 Authorization Code Flow Demo</h1>
        
        {error && <div className="error-message">{error}</div>}
        
        {user ? (
          <div className="user-profile">
            <h2>Welcome, {user.name}!</h2>
            {user.picture && (
              <img 
                src={user.picture} 
                alt="Profile" 
                className="profile-image" 
              />
            )}
            <p>Email: {user.email}</p>
            <button onClick={handleLogout} className="logout-button">
              Logout
            </button>
          </div>
        ) : (
          <div className="login-container">
            <p>You are not logged in.</p>
            <button onClick={handleLogin} className="login-button">
              Login with Google
            </button>
          </div>
        )}
      </header>
    </div>
  );
}

export default App;
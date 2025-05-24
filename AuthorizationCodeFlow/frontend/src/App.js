import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [provider, setProvider] = useState(null);

  useEffect(() => {
    // Check if user is authenticated when component mounts
    fetchUserDetails();
  }, []);

  const fetchUserDetails = async () => {
    setLoading(true);
    
    // First, check if user is authenticated with either provider
    try {
      // Try a universal endpoint first (if available)
      const unifiedResponse = await axios.get('/api/user/current', { withCredentials: true });
      if (unifiedResponse.data && unifiedResponse.data.name) {
        setUser(unifiedResponse.data);
        // Determine provider based on response data
        setProvider(unifiedResponse.data.provider || determineProvider(unifiedResponse.data));
        setError(null);
        setLoading(false);
        return;
      }
    } catch (unifiedErr) {
      // Unified endpoint failed or doesn't exist, try individual endpoints
      console.log("Unified endpoint failed, trying individual providers");
    }
    
    // Try Google
    try {
      const googleResponse = await axios.get('/api/google/user', { withCredentials: true });
      setUser(googleResponse.data);
      setProvider('Google');
      setError(null);
      setLoading(false);
      return;
    } catch (googleErr) {
      // Not authenticated with Google or endpoint error
      console.log("Google auth check failed:", googleErr.response?.status);
    }
    
    // Try Facebook
    try {
      const facebookResponse = await axios.get('/api/facebook/user', { withCredentials: true });
      setUser(facebookResponse.data);
      setProvider('Facebook');
      setError(null);
      setLoading(false);
      return;
    } catch (facebookErr) {
      // Not authenticated with Facebook either
      console.log("Facebook auth check failed:", facebookErr.response?.status);
    }
    
    // If we got here, user is not authenticated with any provider
    setUser(null);
    setProvider(null);
    setError(null);
    setLoading(false);
  };
  
  // Helper function to determine provider from user data
  const determineProvider = (userData) => {
    // This is a fallback if the backend doesn't specify the provider
    if (userData.id && userData.id.startsWith('facebook')) return 'Facebook';
    return 'Google'; // Default to Google if we can't determine
  };

  const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  const handleFacebookLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/facebook';
  };

  const handleLogout = async () => {
    try {
      await axios.post('/logout', {}, { withCredentials: true });
      setUser(null);
      setProvider(null);
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
        <h1>OAuth 2.0 Authorization Code Flow Demo</h1>
        
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
            {provider && (
              <p className="provider-info">Logged in with: 
                <span className={`provider ${provider.toLowerCase()}`}>
                  {provider}
                </span>
              </p>
            )}
            <button onClick={handleLogout} className="logout-button">
              Logout
            </button>
          </div>
        ) : (
          <div className="login-container">
            <p>You are not logged in.</p>
            <div className="login-buttons">
              <button onClick={handleGoogleLogin} className="login-button google-button">
                <svg width="18" height="18" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48">
                  <path fill="#FFC107" d="M43.611,20.083H42V20H24v8h11.303c-1.649,4.657-6.08,8-11.303,8c-6.627,0-12-5.373-12-12c0-6.627,5.373-12,12-12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C12.955,4,4,12.955,4,24c0,11.045,8.955,20,20,20c11.045,0,20-8.955,20-20C44,22.659,43.862,21.35,43.611,20.083z"></path>
                  <path fill="#FF3D00" d="M6.306,14.691l6.571,4.819C14.655,15.108,18.961,12,24,12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C16.318,4,9.656,8.337,6.306,14.691z"></path>
                  <path fill="#4CAF50" d="M24,44c5.166,0,9.86-1.977,13.409-5.192l-6.19-5.238C29.211,35.091,26.715,36,24,36c-5.202,0-9.619-3.317-11.283-7.946l-6.522,5.025C9.505,39.556,16.227,44,24,44z"></path>
                  <path fill="#1976D2" d="M43.611,20.083H42V20H24v8h11.303c-0.792,2.237-2.231,4.166-4.087,5.571c0.001-0.001,0.002-0.001,0.003-0.002l6.19,5.238C36.971,39.205,44,34,44,24C44,22.659,43.862,21.35,43.611,20.083z"></path>
                </svg>
                Login with Google
              </button>
              <button onClick={handleFacebookLogin} className="login-button facebook-button">
                <svg width="18" height="18" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48">
                  <path fill="#3F51B5" d="M42,37c0,2.762-2.238,5-5,5H11c-2.761,0-5-2.238-5-5V11c0-2.762,2.239-5,5-5h26c2.762,0,5,2.238,5,5V37z"></path>
                  <path fill="#FFFFFF" d="M34.368,25H31v13h-5V25h-3v-4h3v-2.41c0.002-3.508,1.459-5.59,5.592-5.59H35v4h-2.287C31.104,17,31,17.6,31,18.723V21h4L34.368,25z"></path>
                </svg>
                Login with Facebook
              </button>
            </div>
          </div>
        )}
      </header>
    </div>
  );
}

export default App;
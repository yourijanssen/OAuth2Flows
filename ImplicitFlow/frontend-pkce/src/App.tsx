import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import userManager from './auth/AuthProvider';
import CallbackPage from './auth/CallbackPage';

const HomePage: React.FC = () => {
  const [user, setUser] = useState<any>(null);

  useEffect(() => {
    userManager.getUser().then(loadedUser => {
      if (loadedUser && !loadedUser.expired) {
        setUser(loadedUser);
      }
    });
  }, []);

  const login = () => userManager.signinRedirect();
  const logout = () => userManager.signoutRedirect();

  return (
    <div style={{ padding: '1rem' }}>
      <h1>PKCE Login (React + TSX + Keycloak)</h1>
      {user ? (
        <>
          <p>Welcome, <strong>{user.profile.preferred_username}</strong></p>
          <pre>{JSON.stringify(user.profile, null, 2)}</pre>
          <button onClick={logout}>Logout</button>
        </>
      ) : (
        <button onClick={login}>Login</button>
      )}
    </div>
  );
};

const App: React.FC = () => (
  <Router>
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/callback" element={<CallbackPage />} />
    </Routes>
  </Router>
);

export default App;

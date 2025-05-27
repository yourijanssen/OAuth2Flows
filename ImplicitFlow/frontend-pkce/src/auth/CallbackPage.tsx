import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import userManager from './AuthProvider';

const CallbackPage: React.FC = () => {
  const navigate = useNavigate();

  useEffect(() => {
    userManager.signinRedirectCallback()
      .then(() => navigate('/'))
      .catch(err => console.error('Redirect error:', err));
  }, [navigate]);

  return <div>Processing login callback...</div>;
};

export default CallbackPage;

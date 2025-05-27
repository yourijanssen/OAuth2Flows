import React, { useEffect, useState } from "react";
import { userManager } from "./auth";

function App() {
  const [user, setUser] = useState<any>(null);

  useEffect(() => {
    userManager.getUser().then((usr) => {
      if (usr) {
        setUser(usr);
      }
    });
  }, []);

  const login = () => userManager.signinRedirect();
  const logout = () => userManager.signoutRedirect();

  return (
    <div style={{ padding: "2rem", fontFamily: "Arial" }}>
      <h1>🔐 OAuth2 Implicit Flow Demo</h1>

      {!user && <button onClick={login}>Login</button>}
      {user && (
        <>
          <p>Welcome, {user.profile.name}</p>
          <pre>{JSON.stringify(user, null, 2)}</pre>
          <button onClick={logout}>Logout</button>
        </>
      )}
    </div>
  );
}

export default App;

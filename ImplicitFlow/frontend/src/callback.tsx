import React, { useEffect } from "react";
import { userManager } from "./auth";

export const Callback = () => {
  useEffect(() => {
    userManager.signinRedirectCallback().then(() => {
      window.location.href = "/";
    });
  }, []);

  return <p>Logging in...</p>;
};

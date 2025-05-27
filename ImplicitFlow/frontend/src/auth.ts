import { UserManager, WebStorageStateStore } from "oidc-client";


const oidcConfig = {
  authority: "http://localhost:8080/realms/implicit-flow-demo",
  client_id: "react-client",
  redirect_uri: "http://localhost:3000/callback",
  response_type: "id_token token", // Implicit Flow
  scope: "openid profile email",
  post_logout_redirect_uri: "http://localhost:3000",
  userStore: new WebStorageStateStore({ store: window.localStorage }),
};

export const userManager = new UserManager(oidcConfig);

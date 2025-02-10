"use strict";

class AuthManager {
  constructor() {
    this.domain = "https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com";
    this.clientId = "5oncoq9mddhbmluooq6kpib2kj";
    this.redirectUri =
      "http://localhost:5500/src/main/resources/static/index.html";
    this.scope = "email openid profile";
    this.authBtn = document.getElementById("authButton");
  }

  // Redirects directly to Cognito's login page
  redirectToCognito() {
    const authUrl = `${
      this.domain
    }/oauth2/authorize?response_type=code&client_id=${
      this.clientId
    }&scope=${encodeURIComponent(this.scope)}&redirect_uri=${encodeURIComponent(
      this.redirectUri
    )}`;
    console.log("Redirecting to Cognito login at:", authUrl);
    window.location.href = authUrl;
  }

  // Detects the authorization code and redirects it to the backend for processing
  processAuthorizationCode() {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get("code");

    if (code) {
      console.log(
        "Authorization code detected, sending to the server for processing."
      );
      this.exchangeCodeForToken(code);
    } else {
      this.updateUI(localStorage.getItem("access_token") != null);
    }
  }

  // Sends the authorization code to the backend
  exchangeCodeForToken(code) {
    fetch(`http://localhost:8080/auth/exchange`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ code: code }),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(
            `HTTP status ${response.status}: ${response.statusText}`
          );
        }
        return response.json();
      })
      .then((data) => {
        console.log("Token exchange successful, updating UI.");
        localStorage.setItem("access_token", data.access_token); // Assuming the token is returned here
        this.updateUI(true);
      })
      .catch((error) => {
        console.error("Error during token exchange:", error);
        this.updateUI(false);
      });
  }

  // Updates the UI based on authentication status
  updateUI(loggedIn) {
    if (loggedIn) {
      this.authBtn.innerText = "Logout";
      this.authBtn.onclick = this.logout.bind(this);
    } else {
      this.authBtn.innerText = "Login";
      this.authBtn.onclick = this.redirectToCognito.bind(this);
    }
  }

  // Log out the user
  logout() {
    fetch("http://localhost:8080/auth/logout", { method: "POST" })
      .then((response) => {
        if (response.ok) {
          console.log("Logged out successfully.");
          localStorage.removeItem("access_token");
          window.location.reload();
        } else {
          throw new Error("Failed to log out.");
        }
      })
      .catch((error) => console.error("Logout failed:", error));
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const authManager = new AuthManager();
  authManager.processAuthorizationCode();
});

class ProfileManager {
  constructor(authManager) {
    this.authManager = authManager;
    this.profileEndpoint = "http://localhost:8080/api/profile";
  }

  fetchUserProfile() {
    const token = localStorage.getItem("access_token");
    if (!token) {
      console.error("No access token available.");
      return;
    }

    fetch(this.profileEndpoint, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP status ${response.status}`);
        }
        return response.json();
      })
      .then((profileData) => {
        console.log("Profile data received:", profileData);
      })
      .catch((error) => {
        console.error("Error fetching profile data:", error);
      });
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const authManager = new AuthManager();
  const profileManager = new ProfileManager(authManager);

  // document.getElementById("profileButton").addEventListener("click", () => {
  //   profileManager.fetchUserProfile();
  // });

  authManager.updateUI(!!localStorage.getItem("access_token"));
});

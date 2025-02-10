"use strict";

class AuthManager {
  constructor() {
    this.authBtn = document.getElementById("authButton");
    this.initEventListeners();
  }

  // Redirects directly to Cognito's login page
  redirectToCognito() {
    window.location.href = "/auth/redirect-to-cognito";
  }

  initEventListeners() {
    document.addEventListener("DOMContentLoaded", () => {
      const authManager = new AuthManager();
      this.processAuthorizationCode();
    });
  }

  // Handles redirection based on the presence of an authorization code
  processAuthorizationCode() {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get("code");

    if (code) {
      this.sendCodeToServer(code);
    } else {
      this.updateUI(!!localStorage.getItem("access_token"));
    }
  }

  // Sends the authorization code to the backend for token exchange
  sendCodeToServer(code) {
    fetch(`http://localhost:8080/auth/exchange`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ code }),
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
        console.log("Token exchange successful.");
        localStorage.setItem("access_token", data.access_token);
        this.updateUI(true);
      })
      .catch((error) => {
        console.error("Error during token exchange:", error);
        this.updateUI(false);
      });
  }

  // Updates the UI based on authentication status
  updateUI(isLoggedIn) {
    this.authBtn.innerText = isLoggedIn ? "Logout" : "Login";
    this.authBtn.onclick = isLoggedIn
      ? this.logout.bind(this)
      : this.redirectToCognito.bind(this);
  }

  // Handles user logout
  logout() {
    fetch("http://localhost:8080/auth/logout", { method: "POST" })
      .then((response) => {
        if (!response.ok) throw new Error("Failed to log out.");
        localStorage.removeItem("access_token");
        window.location.reload();
      })
      .catch((error) => {
        console.error("Logout failed:", error);
        alert("Logout failed, please try again!");
      });
  }
}

document.addEventListener("DOMContentLoaded", () => {
  authManager.processAuthorizationCode();
});

// class ProfileManager {
//   constructor(authManager) {
//     this.authManager = authManager;
//     this.profileEndpoint = "http://localhost:8080/api/profile";
//   }

//   fetchUserProfile() {
//     const token = localStorage.getItem("access_token");
//     if (!token) {
//       console.error("No access token available.");
//       return;
//     }

//     fetch(this.profileEndpoint, {
//       method: "GET",
//       headers: {
//         Authorization: `Bearer ${token}`,
//         "Content-Type": "application/json",
//       },
//     })
//       .then((response) => {
//         if (!response.ok) {
//           throw new Error(`HTTP status ${response.status}`);
//         }
//         return response.json();
//       })
//       .then((profileData) => {
//         console.log("Profile data received:", profileData);
//       })
//       .catch((error) => {
//         console.error("Error fetching profile data:", error);
//       });
//   }
// }

// document.addEventListener("DOMContentLoaded", () => {
// const profileManager = new ProfileManager(authManager);

// document.getElementById("profileButton").addEventListener("click", () => {
//   profileManager.fetchUserProfile();
// });

// authManager.updateUI(!!localStorage.getItem("access_token"));
// });

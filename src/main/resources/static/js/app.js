"use strict";

class AuthManager {
  constructor() {
    this.authBtn = document.getElementById("authButton");
    this.initEventListeners();
    this.checkLoginStatus();
  }

  // Initialize event listeners
  initEventListeners() {
    this.authBtn.addEventListener("click", () => {
      if (localStorage.getItem("access_token")) {
        this.logout();
      } else {
        this.redirectToCognito();
      }
    });
  }

  // Redirects user directly to Cognito login via Spring Security
  redirectToCognito() {
    window.location.href = "http://localhost:8080/oauth2/authorization/cognito";
  }

  // Checks if user is logged in
  checkLoginStatus() {
    fetch("http://localhost:8080/user-info", {
      method: "GET",
      credentials: "include", // Ensures cookies (session) are sent
    })
      .then((response) => {
        if (!response.ok) throw new Error("User not authenticated");
        return response.json();
      })
      .then((data) => {
        console.log("User Info:", data);
        localStorage.setItem("access_token", "session"); // Store as a dummy value
        this.updateUI(true);
      })
      .catch((error) => {
        console.error("Not logged in:", error);
        localStorage.removeItem("access_token");
        this.updateUI(false);
      });
  }

  // Updates the UI based on authentication status
  updateUI(isLoggedIn) {
    this.authBtn.innerText = isLoggedIn ? "Logout" : "Login/Register";
  }

  // Handles user logout
  logout() {
    localStorage.removeItem("access_token"); // Clear token
    window.location.href = "http://localhost:8080/logout"; // Redirect to Cognito logout
  }
}

// Wait for DOM to load
document.addEventListener("DOMContentLoaded", () => {
  new AuthManager();
  new ContentManager();
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

class ContentManager {
  constructor() {
    const article = document.getElementById("article");
    article.addEventListener("click", (ev) => {
      console.log(ev.target, ev.currentTarget);
      console.log(article.q);
    });
  }
}

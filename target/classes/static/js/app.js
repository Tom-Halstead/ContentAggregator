"use strict";

class AuthManager {
  constructor() {
    this.authBtn = document.getElementById("authButton");
    this.authTitle = document.getElementById("auth-title");
    this.initEventListeners();
    this.checkLoginStatus();
  }

  // Initialize event listeners
  initEventListeners() {
    this.authBtn.addEventListener("click", () => {
      if (sessionStorage.getItem("access_token")) {
        this.logout();
      } else {
        this.redirectToCognito();
      }
    });
  }

  // Redirects user to Cognito login via Spring Security
  redirectToCognito() {
    window.location.href = "http://localhost:8080/oauth2/authorization/cognito";
  }

  // Checks if user is logged in
  checkLoginStatus() {
    fetch("http://localhost:8080/user-info", {
      method: "GET",
      credentials: "include", // ✅ Ensures session cookies are included
    })
      .then((response) => {
        if (response.status === 401) throw new Error("User not authenticated"); // ✅ Prevents crashing
        return response.json();
      })
      .then((user) => {
        console.log("User Info:", user);
        sessionStorage.setItem("access_token", "session"); // ✅ Dummy session token

        this.updateUI(true, user);
      })
      .catch((error) => {
        console.error("Not logged in:", error);
        sessionStorage.removeItem("access_token");
        this.updateUI(false);
      });
  }

  // Updates the UI based on authentication status
  updateUI(isLoggedIn, user = null) {
    this.authBtn.innerText = isLoggedIn ? "Logout" : "Login/Register";
    if (user) {
      this.authTitle.innerText = `Hello, ${user.email || "Guest"}`;
    } else {
      this.authTitle.innerText = "Welcome, Guest";
    }
  }

  // Handles user logout
  logout() {
    sessionStorage.removeItem("access_token"); // ✅ Clears session storage
    window.location.href = "http://localhost:8080/logout"; // ✅ Redirects to backend logout
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

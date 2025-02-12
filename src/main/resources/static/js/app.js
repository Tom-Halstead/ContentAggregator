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
      if (this.isAuthenticated()) {
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

  // Checks if the user is authenticated
  isAuthenticated() {
    const token = sessionStorage.getItem("access_token");
    if (!token) return false;

    // Verify token expiration
    const payload = this.parseJwt(token);
    if (!payload || Date.now() >= payload.exp * 1000) {
      console.warn("Token expired, logging out...");
      this.logout();
      return false;
    }

    return true;
  }

  // Fetch user info if logged in
  checkLoginStatus() {
    const token = sessionStorage.getItem("access_token");

    if (!token) {
      console.warn("No valid session token found. User is not logged in.");
      this.updateUI(false);
      return;
    }

    fetch("http://localhost:8080/user-info", {
      method: "GET",
      credentials: "include", // ✅ Ensures session cookies are included
      headers: {
        Authorization: `Bearer ${token}`, // ✅ Send JWT token
      },
    })
      .then((response) => {
        if (!response.ok) throw new Error("User not authenticated");
        return response.json();
      })
      .then((user) => {
        console.log("User Info:", user);

        // ✅ Store actual access token
        if (user.access_token) {
          sessionStorage.setItem("access_token", user.access_token);
        }

        this.updateUI(true, user);
      })
      .catch((error) => {
        console.error("Not logged in:", error);
        sessionStorage.removeItem("access_token");
        this.updateUI(false);
      });
  }

  // // Checks if user is logged in
  // checkLoginStatus() {
  //   // Step 1: Check if a valid session token or JWT exists (from localStorage or sessionStorage)
  //   const token = sessionStorage.getItem("access_token");

  //   if (!token) {
  //     console.error("No session token found. User is not logged in.");
  //     this.updateUI(false);
  //     return;
  //   }

  //   // Step 2: If a valid token exists, proceed to check the user info
  //   fetch("http://localhost:8080/user-info", {
  //     method: "GET",
  //     credentials: "include", // Ensures session cookies are included
  //     headers: {
  //       Authorization: `Bearer ${token}`, // If using JWT, send it in the Authorization header
  //     },
  //   })
  //     .then((response) => {
  //       if (response.status === 401) {
  //         throw new Error("User not authenticated");
  //       }
  //       return response.json();
  //     })
  //     .then((user) => {
  //       console.log("User Info:", user);
  //       sessionStorage.setItem("access_token", "session"); // Dummy session token for demo purposes
  //       this.updateUI(true, user);
  //     })
  //     .catch((error) => {
  //       console.error("Not logged in:", error);
  //       sessionStorage.removeItem("access_token");
  //       this.updateUI(false);
  //     });
  // }

  // Updates the UI based on authentication status
  updateUI(isLoggedIn, user = null) {
    this.authBtn.innerText = isLoggedIn ? "Logout" : "Login/Register";
    this.authTitle.innerText = isLoggedIn
      ? `Hello, ${user?.username || "User"}`
      : "Welcome, Guest";
  }

  // Handles user logout
  logout() {
    sessionStorage.removeItem("access_token"); // ✅ Clears session storage
    window.location.href =
      "https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com/logout?client_id=5oncoq9mddhbmluooq6kpib2kj&logout_uri=http://localhost:8080/logout";
  }

  parseJwt(token) {
    try {
      return JSON.parse(atob(token.split(".")[1])); // Decodes Base64 JWT payload
    } catch (e) {
      console.error("Invalid JWT token", e);
      return null;
    }
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

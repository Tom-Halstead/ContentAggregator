"use strict";

class AuthManager {
  constructor() {
    this.authBtn = document.getElementById("authButton");
    this.authTitle = document.getElementById("auth-title");
    this.initEventListeners();
    this.processLoginFromUrl(); // NEW: Process login from URL if redirected after OAuth2 login
    this.checkLoginStatus();
  }

  /**
   * Initializes event listeners for authentication button.
   */
  initEventListeners() {
    this.authBtn.addEventListener("click", () => {
      if (this.isAuthenticated()) {
        this.logout();
      } else {
        this.redirectToCognito();
      }
    });
  }

  /**
   * Redirects the user to the Cognito authentication page.
   */
  redirectToCognito() {
    window.location.href = "http://localhost:8080/oauth2/authorization/cognito";
  }

  /**
   * Checks if the user is authenticated by looking for an access token in sessionStorage.
   * @returns {boolean} True if authenticated, otherwise false.
   */
  isAuthenticated() {
    const token = sessionStorage.getItem("access_token");
    console.log("Checking authentication. Found token:", token);
    return token !== null;
  }

  /**
   * Extracts query parameters from the URL.
   * @returns {Object} Query parameters as key-value pairs.
   */
  getQueryParams() {
    let params = {};
    window.location.search
      .substring(1)
      .split("&")
      .forEach((param) => {
        let pair = param.split("=");
        params[decodeURIComponent(pair[0])] = decodeURIComponent(pair[1] || "");
      });
    return params;
  }

  /**
   * ✅ NEW: Handles login from URL query parameters after redirect.
   * Stores credentials in sessionStorage & updates UI.
   */
  processLoginFromUrl() {
    console.log("Processing login from URL...");
    const params = this.getQueryParams();

    if (params.access_token) {
      console.log("✅ Found login details in URL. Storing in sessionStorage.");
      sessionStorage.setItem("access_token", params.access_token);
      sessionStorage.setItem("username", params.username || "User");
      sessionStorage.setItem("email", params.email || "");

      // Remove login parameters from URL to keep it clean
      window.history.replaceState({}, document.title, window.location.pathname);
    }
  }

  /**
   * ✅ UPDATED: Checks login status using sessionStorage instead of calling `/post-login`
   */
  checkLoginStatus() {
    console.log("Checking login status...");

    // Retrieve token from sessionStorage
    const accessToken = sessionStorage.getItem("access_token");
    if (!accessToken) {
      console.warn("No access token found in sessionStorage.");
      this.updateUI(false);
      return;
    }

    // Use stored data to update UI
    this.updateUI(true, {
      username: sessionStorage.getItem("username"),
      email: sessionStorage.getItem("email"),
    });
  }

  /**
   * ✅ Loads user data from an API if necessary.
   */
  async loadUserData() {
    console.log("Loading user data...");
    const accessToken = sessionStorage.getItem("access_token");

    if (!accessToken) {
      console.error("User is not logged in. No token found in sessionStorage.");
      this.updateUI(false);
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/user-info", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${accessToken}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("User not authenticated");
      }

      const user = await response.json();
      console.log("User Info received from API:", user);

      sessionStorage.setItem("username", user.username);
      sessionStorage.setItem("email", user.email);
      sessionStorage.setItem("access_token", user.accessToken);

      this.updateUI(true, user);
    } catch (error) {
      console.error("Error fetching user info:", error);
      this.updateUI(false);
    }
  }

  /**
   * ✅ Updates the UI based on the user's login status.
   * @param {boolean} isLoggedIn - Whether the user is logged in.
   * @param {Object|null} user - User information containing username and email.
   */
  updateUI(isLoggedIn, user = null) {
    console.log("Updating UI. User logged in:", isLoggedIn);

    // Capitalize first letter of the username
    const formattedUsername = user?.username
      ? user.username.charAt(0).toUpperCase() + user.username.slice(1)
      : "User";

    this.authBtn.innerText = isLoggedIn ? "Logout" : "Login/Register";
    this.authTitle.innerText = isLoggedIn
      ? `Hello, ${formattedUsername}!`
      : "Welcome!";
  }

  /**
   * ✅ Logs the user out by clearing sessionStorage and redirecting to Cognito logout.
   */
  logout() {
    console.log("Logging out...");
    this.clearSession();

    window.location.href =
      "https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com/logout?client_id=5oncoq9mddhbmluooq6kpib2kj&logout_uri=http://127.0.0.1:5500/index.html";
  }

  /**
   * ✅ Clears session storage data on logout or login failure.
   */
  clearSession() {
    sessionStorage.removeItem("access_token");
    sessionStorage.removeItem("username");
    sessionStorage.removeItem("email");
    this.updateUI(false);
  }
}

// Wait for DOM to load
window.onload = function () {
  console.log("Page Loaded, Checking Auth...");
  new AuthManager();
  new ProfileManager();
  new ContentManager();
};

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

class ContentManager {
  constructor() {
    const article = document.getElementById("article");
    article.addEventListener("click", (ev) => {
      console.log(ev.target, ev.currentTarget);
      console.log(article.q);
    });
  }
}

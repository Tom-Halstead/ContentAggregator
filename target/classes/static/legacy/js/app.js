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
   * Checks if the user is authenticated by looking for an access token in localStorage.
   * @returns {boolean} True if authenticated, otherwise false.
   */
  isAuthenticated() {
    const token = localStorage.getItem("access_token");
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
   * Stores credentials in localStorage & updates UI.
   */
  processLoginFromUrl() {
    console.log("Processing login from URL...");
    const params = this.getQueryParams();

    if (params.access_token) {
      console.log("✅ Found login details in URL. Storing in localStorage.");
      localStorage.setItem("access_token", params.access_token);
      localStorage.setItem("username", params.username || "User");
      localStorage.setItem("email", params.email || "");

      // Remove login parameters from URL to keep it clean
      window.history.replaceState({}, document.title, window.location.pathname);
    }
  }

  /**
   * ✅ UPDATED: Checks login status using localStorage instead of calling `/post-login`
   */
  checkLoginStatus() {
    console.log("Checking login status...");

    // Retrieve token from localStorage
    const accessToken = localStorage.getItem("access_token");
    if (!accessToken) {
      console.warn("No access token found in localStorage.");
      this.updateUI(false);
      return;
    }

    // Use stored data to update UI
    this.updateUI(true, {
      username: localStorage.getItem("username"),
      email: localStorage.getItem("email"),
    });
  }

  /**
   * ✅ Loads user data from an API if necessary.
   */
  async loadUserData() {
    console.log("Loading user data...");
    const accessToken = localStorage.getItem("access_token");

    if (!accessToken) {
      console.error("User is not logged in. No token found in localStorage.");
      this.updateUI(false);
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/auth/user-info", {
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

      localStorage.setItem("username", user.username);
      localStorage.setItem("email", user.email);
      localStorage.setItem("access_token", user.accessToken);

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

    // Format username if available
    const formattedUsername = user?.username
      ? user.username.charAt(0).toUpperCase() + user.username.slice(1)
      : null;

    // Update authentication button text
    this.authBtn.innerText = isLoggedIn ? "Logout" : "Login/Register";

    // Update the greeting title with the welcome message
    this.displayWelcomeMessage(formattedUsername);
  }

  /**
   * Displays a welcome message based on the time of day.
   * If a user is logged in, their name is included.
   */
  displayWelcomeMessage(username = null) {
    const hour = new Date().getHours();
    let message = username
      ? `Hope you're having a great night, ${username}!`
      : `Hope you're having a great night!`;

    if (hour >= 5 && hour < 12) {
      username ? (message = `Good morning, ${username}!`) : `Good Morning!`;
    } else if (hour >= 12 && hour < 18) {
      username ? (message = `Good afternoon, ${username}!`) : `Good Afternoon!`;
    } else if (hour >= 18 && hour < 22) {
      username ? (message = `Good evening, ${username}!`) : `Good Evening!`;
    }

    // Update the welcome message in the UI
    this.authTitle.innerText = message;
  }

  /**
   * ✅ Logs the user out by clearing localStorage and redirecting to Cognito logout.
   */
  logout() {
    console.log("Logging out...");
    this.clearSession();
    this.authTitle.innerText = "Welcome!";

    const logoutUrl = `https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com/logout?client_id=5oncoq9mddhbmluooq6kpib2kj&logout_uri=${encodeURIComponent(
      "http://localhost:8080/legacy/index.html"
    )}`;

    console.log("Redirecting to:", logoutUrl);
    window.location.href = logoutUrl;
  }

  /**
   * ✅ Clears session storage data on logout or login failure.
   */
  clearSession() {
    localStorage.removeItem("access_token");
    localStorage.removeItem("username");
    localStorage.removeItem("email");
    this.updateUI(false);
  }
}

// Wait for DOM to load
window.onload = function () {
  console.log("Page Loaded, Checking Auth...");
  new AuthManager();
};

class AuthManager {
  constructor() {
    this.authBtn = document.getElementById("authButton");
    this.authTitle = document.getElementById("auth-title");
    this.initEventListeners();
    this.loadUserData();
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
   * Checks if the user is authenticated by looking for the access token cookie.
   * @returns {boolean} True if authenticated, otherwise false.
   */
  isAuthenticated() {
    return this.getCookie("access_token") !== null;
  }

  /**
   * Loads user data from cookies and updates the UI.
   */
  loadUserData() {
    if (!this.isAuthenticated()) {
      console.error("User is not logged in.");
      this.updateUI(false);
      return;
    }

    const username = this.getCookie("username") || "User";
    const email = this.getCookie("email");

    console.log("User Info:", { username, email });
    this.updateUI(true, { username, email });
  }

  /**
   * Updates the UI based on the user's login status.
   * @param {boolean} isLoggedIn - Whether the user is logged in.
   * @param {Object|null} user - User information containing username and email.
   */
  updateUI(isLoggedIn, user = null) {
    this.authBtn.innerText = isLoggedIn ? "Logout" : "Login/Register";
    this.authTitle.innerText = isLoggedIn
      ? `Hello, ${user?.username || "User"}!`
      : "Welcome!";
  }

  /**
   * Logs the user out by clearing authentication cookies and redirecting to Cognito logout.
   */
  logout() {
    this.clearCookie("access_token");
    this.clearCookie("username");
    this.clearCookie("email");

    window.location.href =
      "https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com/logout?client_id=5oncoq9mddhbmluooq6kpib2kj&logout_uri=http://127.0.0.1:5500/src/main/resources/static/index.html";
  }

  /**
   * Retrieves the value of a specified cookie.
   * @param {string} name - The name of the cookie to retrieve.
   * @returns {string|null} The cookie value or null if not found.
   */
  getCookie(name) {
    const cookieString = document.cookie
      .split("; ")
      .find((row) => row.startsWith(name + "="));
    return cookieString ? decodeURIComponent(cookieString.split("=")[1]) : null;
  }

  /**
   * Clears a specified cookie by setting its expiration to the past.
   * @param {string} name - The name of the cookie to clear.
   */
  clearCookie(name) {
    document.cookie = `${name}=; Max-Age=0; path=/`;
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

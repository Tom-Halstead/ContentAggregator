class AuthManager {
  constructor() {
    this.authBtn = document.getElementById("authButton");
    this.authTitle = document.getElementById("auth-title");
    this.initEventListeners();
    this.checkLoginStatus();
  }

  initEventListeners() {
    this.authBtn.addEventListener("click", () => {
      if (this.isAuthenticated()) {
        this.logout();
      } else {
        this.redirectToCognito();
      }
    });
  }

  redirectToCognito() {
    window.location.href = "http://localhost:8080/oauth2/authorization/cognito";
  }

  isAuthenticated() {
    return document.cookie.includes("access_token=");
  }

  checkLoginStatus() {
    if (!this.isAuthenticated()) {
      console.error("User is not logged in.");
      this.updateUI(false);
      return;
    }

    fetch("http://localhost:8080/user-info", {
      method: "GET",
      credentials: "include", // Ensures cookies are included
    })
      .then((response) => {
        if (response.status === 401) {
          throw new Error("User not authenticated");
        }
        return response.json();
      })
      .then((user) => {
        console.log("User Info:", user);
        this.updateUI(true, user);
      })
      .catch((error) => {
        console.error("Not logged in:", error);
        this.updateUI(false);
      });
  }

  updateUI(isLoggedIn, user = null) {
    this.authBtn.innerText = isLoggedIn ? "Logout" : "Login/Register";
    this.authTitle.innerText = isLoggedIn
      ? `Hello, ${user?.username || "User"}`
      : "Welcome!";
  }

  logout() {
    document.cookie = "access_token=; Max-Age=0; path=/"; // Delete token
    window.location.href =
      "https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com/logout?client_id=5oncoq9mddhbmluooq6kpib2kj&logout_uri=http://127.0.0.1:5500/index.html";
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

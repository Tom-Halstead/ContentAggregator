"use strict";
/////////////////////
////////////////////
////////////////////
///////////////////

function redirectToCognito() {
  const domain = "https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com";
  const clientId = "5oncoq9mddhbmluooq6kpib2kj";
  const redirectUri = encodeURIComponent(
    "http://localhost:5500/src/main/resources/static/index.html"
  );
  const scope = encodeURIComponent("email openid profile");
  const authUrl = `${domain}/oauth2/authorize?client_id=${clientId}&response_type=code&scope=${scope}&redirect_uri=${redirectUri}`;

  console.log("Redirecting to Cognito login at:", authUrl); // Debugging
  window.location.href = authUrl; // Redirect the browser to Cognito
}

window.onload = function () {
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get("code");

  if (code) {
    fetch(`/auth/login?code=${encodeURIComponent(code)}`, {
      method: "GET", // No need to set Content-Type or use Authorization header
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to authenticate");
        }
        return response.json();
      })
      .then((data) => {
        console.log("Token:", data); // Handle the data as needed
      })
      .catch((error) => {
        console.error("Error", error);
      });
  }
};

window.onload = function () {
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get("code");
  const authBtn = document.getElementById("authButton");

  if (code) {
    authBtn.classList.add("hidden");
  }
};

document.getElementById("authButton").addEventListener("click", () => {
  console.log("Logged in");
  redirectToCognito();
});

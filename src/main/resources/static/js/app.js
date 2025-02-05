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
  const authBtn = document.getElementById("authButton");
  const authTitle = document.getElementById("auth-title");

  // Check for token in localStorage
  const token = localStorage.getItem("access_token");

  if (token) {
    authBtn.innerText = "Logout";
    authBtn.onclick = logout; // Assign the logout function
    authTitle.classList.add("hidden");
  } else if (code) {
    // Handle the code exchange process
    handleCodeExchange(code, authBtn);
  } else {
    authBtn.innerText = "Login";
    authBtn.onclick = redirectToCognito; // Assign the login function
  }
};

document.getElementById("authButton").addEventListener("click", () => {
  console.log("Logged in");
  redirectToCognito();
});

function logout() {
  localStorage.removeItem("access_token"); // Clear the token
  window.location.reload(); // Reload the page to reset state
}

function handleCodeExchange(code, authBtn) {
  fetch(`/auth/login?code=${encodeURIComponent(code)}`, {
    method: "GET",
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to authenticate");
      }
      return response.json();
    })
    .then((data) => {
      console.log("Token:", data); // Debugging token data
      localStorage.setItem("access_token", data.access_token); // Store the token
      authBtn.innerText = "Logout";
      authBtn.removeEventListener("click", redirectToCognito);
      authBtn.addEventListener("click", logout); // Update button to logout
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

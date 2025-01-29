function openCognitoPopup() {
  const domain = "https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com";
  const clientId = "5oncoq9mddhbmluooq6kpib2kj";
  const redirectUri = encodeURIComponent(
    "http://localhost:5500/src/main/resources/static/index.html"
  );

  const scope = encodeURIComponent("email openid profile");
  const authUrl = `${domain}/oauth2/authorize?client_id=${clientId}&response_type=code&scope=${scope}&redirect_uri=${redirectUri}`;

  console.log("Opening Cognito login at:", authUrl); // Debugging

  // Open the Cognito login page
  const authWindow = window.open(
    authUrl,
    "CognitoLogin",
    "width=500, height=600"
  );

  if (!authWindow) {
    alert("Popup blocked. Please allow popups to continue.");
    return;
  }

  // Poll for Cognito redirect
  const interval = setInterval(() => {
    try {
      if (authWindow.location.href.includes("code=")) {
        const urlParams = new URLSearchParams(authWindow.location.search);
        const authCode = urlParams.get("code");

        if (authCode) {
          authWindow.close();
          clearInterval(interval);
          exchangeAuthCodeForTokens(authCode);
        }
      }
    } catch (error) {
      // Ignore cross-origin access errors
    }
  }, 1000);
}

document.getElementById("loginBtn").addEventListener("click", () => {
  console.log("Logged in");
  openCognitoPopup();
});
document.getElementById("registerBtn").addEventListener("click", () => {
  console.log("Registered");
});

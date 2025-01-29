document.getElementById("loginBtn").addEventListener("click", () => {
  console.log("Logged in");
});
document.getElementById("registerBtn").addEventListener("click", () => {
  console.log("Registered");
});

function openCognitoPopup(action) {
  const clientId = "5oncoq9mddhbmluooq6kpib2kj";
  const redirectUri = "";
  const domain = "https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com";
  const scope = "";

  let authUrl = "";

  const authWindow = window.open(
    authUrl,
    "CognitoLogin",
    "width=500, height=600"
  );

  if (!authWindow) {
    alert("Popup blocked. Please allow popups to continue.");
    return;
  }

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
    } catch (error) {}
  }, 1000);
}

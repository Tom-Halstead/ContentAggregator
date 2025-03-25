"use strict";

class ContentManager {
  constructor() {
    // Select containers using data attributes
    this.newsContainerReddit = document.querySelector(
      '[data-category="Reddit"] .news-container'
    );
    this.newsContainerLocal = document.querySelector(
      '[data-category="Local"] .news-container'
    );
    this.newsContainerWorld = document.querySelector(
      '[data-category="World"] .news-container'
    );

    // Bind the Random News button event
    this.randomNewsButton = document.querySelector("#randomize");
    if (this.randomNewsButton) {
      this.randomNewsButton.addEventListener("click", () =>
        this.handleRandomNews()
      );
    }

    // Wait for access token and fetch initial news on DOM load
    this.waitForAccessToken();
  }

  /**
   * Polls localStorage for the access token and fetches articles upon detection.
   */
  waitForAccessToken() {
    const interval = setInterval(() => {
      const accessToken = localStorage.getItem("access_token");
      if (accessToken) {
        this.fetchWorldNews(); // initial world news (default page 1)
        this.fetchRedditStories(); // initial reddit posts
        this.fetchLocalNews(); // local news
        clearInterval(interval);
      }
    }, 500);
  }

  // Helper method to extract the country code from the browser locale
  getUserCountry() {
    const locale = navigator.language || navigator.userLanguage;
    if (locale && locale.includes("-")) {
      return locale.split("-")[1].toUpperCase();
    }
    return "US";
  }

  // Modular method to clear a given container
  clearContainer(container) {
    while (container.firstChild) {
      container.removeChild(container.firstChild);
    }
  }

  // Utility to remove all children (alias of clearContainer)
  removeAllChildren(container) {
    this.clearContainer(container);
  }

  /**
   * Fetch local news using the user's browser country code.
   */
  fetchLocalNews() {
    const accessToken = localStorage.getItem("access_token");
    if (!accessToken) {
      console.error("Access token not found. Please log in.");
      return;
    }
    const userCountry = this.getUserCountry();
    axios
      .get(
        `http://localhost:8080/api/news/articles?country=${encodeURIComponent(
          userCountry
        )}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      )
      .then((response) => {
        const articles = response.data;
        if (!Array.isArray(articles)) {
          throw new Error(
            "Invalid data format: Expected an array of articles."
          );
        }
        this.removeAllChildren(this.newsContainerLocal);
        const validArticles = articles
          .filter((article) => article.urlToImage)
          .slice(0, 5);
        validArticles.forEach((article) => {
          const row = this.createArticleElement(
            article,
            article.urlToImage,
            article.url
          );
          this.newsContainerLocal.appendChild(row);
        });
      })
      .catch((error) => {
        if (error.response) {
          console.error(
            "Request was made, external news API returned a 4xx or 5xx error."
          );
        } else if (error.request) {
          console.error(
            "Request sent to external news API, no response received."
          );
        } else {
          console.error("Unknown error fetching local news:", error.message);
        }
      });
  }

  /**
   * Fetch world news.
   * @param {string} selectedCountry - Country code (default "US").
   * @param {number} page - Page number for pagination (default 1).
   */
  fetchWorldNews(selectedCountry = "US", page = 1) {
    const accessToken = localStorage.getItem("access_token");
    if (!accessToken) {
      console.error("Access token not found. Please log in.");
      return;
    }
    axios
      .get(
        `http://localhost:8080/api/news/articles?country=${encodeURIComponent(
          selectedCountry
        )}&page=${page}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      )
      .then((response) => {
        const articles = response.data;
        console.log(articles);
        if (!Array.isArray(articles) || articles.length === 0) {
          throw new Error(
            "Invalid data format: Expected an array of articles."
          );
        }
        this.removeAllChildren(this.newsContainerWorld);
        const validArticles = articles
          .filter((article) => article.urlToImage)
          .slice(0, 5);
        validArticles.forEach((article) => {
          const row = this.createArticleElement(
            article,
            article.urlToImage,
            article.url
          );
          this.newsContainerWorld.appendChild(row);
        });
      })
      .catch((error) => {
        if (error.response) {
          console.error(
            "Request was made, external news API returned a 4xx or 5xx error."
          );
        } else if (error.request) {
          console.error(
            "Request sent to external news API, no response received."
          );
        } else {
          console.error("Unknown error fetching world news:", error.message);
        }
      });
  }

  /**
   * Fetches Reddit stories from the backend and updates the DOM.
   */
  fetchRedditStories() {
    const accessToken = localStorage.getItem("access_token");
    if (!accessToken) {
      console.error("Access token not found. Please log in.");
      return;
    }
    axios
      .get("http://localhost:8080/api/reddit/posts", {
        headers: {
          Authorization: `Bearer ${accessToken}`,
          "Content-Type": "application/json",
        },
      })
      .then((response) => {
        const posts = response.data;
        if (!Array.isArray(posts)) {
          throw new Error(
            "Invalid data format: Expected an array of Reddit posts."
          );
        }
        this.removeAllChildren(this.newsContainerReddit);
        posts.forEach((post) => {
          console.log(post);
          let displayImage =
            post.is_video || post.thumbnail === "nsfw"
              ? "images/reddit-placeholder.jpg"
              : post.thumbnail;
          const row = this.createArticleElement(post, displayImage, post.url);
          this.newsContainerReddit.appendChild(row);
        });
      })
      .catch((error) => {
        if (error.response) {
          console.error(
            "Request was made, external reddit API returned a 4xx or 5xx error."
          );
        } else if (error.request) {
          console.error(
            "Request sent to external reddit API, no response received."
          );
        } else {
          console.error(
            "Unknown error fetching Reddit stories:",
            error.message
          );
        }
      });
  }

  /**
   * Handles the Random News button click.
   * Randomizes the world news page parameter and refreshes Reddit posts.
   */
  handleRandomNews() {
    const accessToken = localStorage.getItem("access_token");
    if (!accessToken) {
      console.error("Access token not found. Please log in.");
      return;
    }
    // Generate a random page number between 1 and 10 (adjust range as needed)
    const randomPage = Math.floor(Math.random() * 10) + 1;
    // Fetch new world news using the random page number.
    this.fetchWorldNews("US", randomPage);
    // Also refresh Reddit posts (the backend randomizes via its time filter)
    this.fetchRedditStories();
  }

  /**
   * Creates a DOM element representing an article or Reddit post.
   * @param {Object} article - The article/post data.
   * @param {string} imageUrl - URL to the image or thumbnail.
   * @param {string} postUrl - URL to the full article/post.
   * @returns {HTMLElement} - The created row element.
   */
  createArticleElement(article, imageUrl, postUrl) {
    const row = document.createElement("div");
    row.classList.add("row");
    const link = document.createElement("a");
    link.href = postUrl || "#";
    link.target = "_blank";
    link.style.textDecoration = "none";
    link.style.color = "inherit";
    const title = document.createElement("h3");
    title.textContent = article.title || "No Title";
    link.appendChild(title);
    if (imageUrl) {
      const img = document.createElement("img");
      img.src = imageUrl;
      img.alt = article.title || "Article image";
      img.loading = "lazy";
      link.appendChild(img);
    }
    row.appendChild(link);
    return row;
  }
}

document.addEventListener("DOMContentLoaded", () => {
  new ContentManager();
  const header = document.querySelector(".header");
  const windowHeightSetpoint = window.innerHeight / 4;
  window.addEventListener("scroll", () => {
    if (window.scrollY >= windowHeightSetpoint) {
      header.style.position = "fixed";
      header.style.top = 0;
    } else {
      header.style.position = "relative";
      header.style.top = "";
    }
  });
});

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

    // Wait for access token and fetch news on DOM load
    this.waitForAccessToken();
  }

  /**
   * Polls localStorage for the access token and fetches articles upon detection.
   */
  waitForAccessToken() {
    const interval = setInterval(() => {
      const accessToken = localStorage.getItem("access_token");

      if (accessToken) {
        this.fetchWorldNews();
        this.fetchRedditStories(); // Fetch Reddit stories after login
        clearInterval(interval);
      }
    }, 500);
  }

  /**
   * Fetches world news articles from the backend and updates the DOM.
   */
  fetchWorldNews() {
    const accessToken = localStorage.getItem("access_token");
    if (!accessToken) {
      console.error("Access token not found. Please log in.");
      return;
    }

    axios
      .get("http://localhost:8080/api/news/articles", {
        headers: {
          Authorization: `Bearer ${accessToken}`,
          "Content-Type": "application/json",
        },
      })
      .then((response) => {
        const articles = response.data;
        if (!Array.isArray(articles)) {
          throw new Error(
            "Invalid data format: Expected an array of articles."
          );
        }

        this.clearContainers();

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
            "Request sent to exernal news API, no response received."
          );
        } else {
          console.error("Unknown error fetching news stories:", error.message);
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
        posts.forEach((post) => {
          // Compute display image using the DTO logic:
          // If it's a video, show a placeholder image;
          // otherwise, use thumbnail if valid, or fallback to the main url.
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
   * Clears all news containers.
   */
  clearContainers() {
    this.removeAllChildren(this.newsContainerReddit);
    this.removeAllChildren(this.newsContainerLocal);
    this.removeAllChildren(this.newsContainerWorld);
  }

  /**
   * Removes all child elements from a given container.
   * @param {HTMLElement} container - The container to clear.
   */
  removeAllChildren(container) {
    while (container.firstChild) {
      container.removeChild(container.firstChild);
    }
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

// Initialize ContentManager on DOM load
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

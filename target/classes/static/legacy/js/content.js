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
    }, 500); // Check every 500ms
  }

  /**
   * Fetches world news articles from the backend and updates the DOM.
   */
  async fetchWorldNews() {
    try {
      const accessToken = localStorage.getItem("access_token");
      if (!accessToken)
        throw new Error("Access token not found. Please log in.");

      const response = await fetch("http://localhost:8080/api/news/articles", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${accessToken}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok)
        throw new Error(`Failed to fetch articles. Status: ${response.status}`);

      const articles = await response.json();
      if (!Array.isArray(articles))
        throw new Error("Invalid data format: Expected an array of articles.");

      this.clearContainers(); // Clear previous articles

      // Filter out articles without images and limit to 5 articles
      const validArticles = articles
        .filter((article) => article.urlToImage)
        .slice(0, 5);

      // Add each valid article to the World news container
      validArticles.forEach((article) => {
        const row = this.createArticleElement(
          article,
          article.urlToImage,
          article.url
        );
        this.newsContainerWorld.appendChild(row);
      });
    } catch (error) {
      console.error("Error fetching world news:", error.message);
    }
  }

  /**
   * Fetches Reddit stories from the backend and updates the DOM.
   */
  async fetchRedditStories() {
    try {
      const accessToken = localStorage.getItem("access_token");
      if (!accessToken)
        throw new Error("Access token not found. Please log in.");

      const response = await fetch("http://localhost:8080/api/reddit/posts", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${accessToken}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok)
        throw new Error(
          `Failed to fetch Reddit stories. Status: ${response.status}`
        );

      const posts = await response.json();
      if (!Array.isArray(posts))
        throw new Error(
          "Invalid data format: Expected an array of Reddit posts."
        );

      posts.forEach((post) => {
        console.log(post);
        const row = this.createArticleElement(
          post,
          post.preferredImageUrl || post.thumbnail,
          post.fullPostUrl || post.permalink
        );
        this.newsContainerReddit.appendChild(row);
      });
    } catch (error) {
      console.error("Error fetching Reddit stories:", error.message);
    }
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

    // Create clickable link wrapping the entire content
    const link = document.createElement("a");
    link.href = postUrl || "#";
    link.target = "_blank";
    link.style.textDecoration = "none";
    link.style.color = "inherit";

    // Article title
    const title = document.createElement("h3");
    title.textContent = article.title || "No Title";
    link.appendChild(title);

    // Image or description
    if (imageUrl) {
      const img = document.createElement("img");
      img.src = imageUrl;
      img.alt = article.title || "Article image";
      img.loading = "lazy";
      link.appendChild(img);
    }
    // else if (article.description) {
    //   const description = document.createElement("p");
    //   description.textContent = article.description;
    //   description.style.fontFamily = "Noto Sans Display, sans, serif";
    //   description.style.lineHeight = "22px";
    //   link.appendChild(description);
    // }

    row.appendChild(link); // Add link to the row
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

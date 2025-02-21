"use strict";

class ContentManager {
  constructor() {
    this.newsContainerReddit = document.querySelector(
      '[data-category="Reddit"] .news-container'
    );
    this.newsContainerLocal = document.querySelector(
      '[data-category="Local"] .news-container'
    );
    this.newsContainerWorld = document.querySelector(
      '[data-category="World"] .news-container'
    );

    // Fetch the initial user news sources and display random news on DOM load
    this.waitForAccessToken();
  }

  // Check for access_token in intervals in order to correctly load data without requiring the user to manually reload the page

  waitForAccessToken() {
    const interval = setInterval(() => {
      const accessToken = localStorage.getItem("access_token");

      if (accessToken) {
        clearInterval(interval);
        this.fetchWorldNews();
      }
    }, 500);
  }

  /**
   * Fetches random news articles from the back-end API and updates the DOM.
   */
  async fetchWorldNews() {
    try {
      const accessToken = localStorage.getItem("access_token");

      // Check if the access token is present
      if (!accessToken) {
        throw new Error("Access token not found. Please log in.");
      }

      const response = await fetch("http://localhost:8080/api/news/articles", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${accessToken}`,
          "Content-Type": "application/json",
        },
      });

      // Handle unauthorized access (401 error)
      if (response.status === 401) {
        throw new Error("Unauthorized. Please log in to access the articles.");
      }

      if (!response.ok) {
        throw new Error(`Failed to fetch articles. Status: ${response.status}`);
      }

      const articles = await response.json();
      console.log(articles);

      // Ensure articles is an array
      if (!Array.isArray(articles)) {
        throw new Error("Invalid data format: Expected an array of articles.");
      }

      // Clear current articles before adding new ones
      this.clearNewsContainers();

      // Add all articles to all categories (no filtering by category)
      this.addNewsArticlesToCategory(articles);
    } catch (error) {
      console.error("Error fetching articles:", error.message);
    }
  }

  async fetchRedditStories() {
    try {
      const response = await fetch(
        "https://www.reddit.com/r/wtf/top.json?t=day&limit=5"
      );
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const data = await response.json();

      console.log(data);
      data.data.children.forEach((post) => {
        console.log(`Title: ${post.data.title}, Upvotes: ${post.data.ups}`);
      });
    } catch (error) {
      console.error("Error fetching Reddit stories:", error);
    }
  }

  /**
   * Clears all news containers before adding new content.
   */
  clearNewsContainers() {
    // Remove all child nodes from each container using removeChild
    this.removeAllChildren(this.newsContainerReddit);
    this.removeAllChildren(this.newsContainerLocal);
    this.removeAllChildren(this.newsContainerWorld);
  }

  /**
   * Removes all child elements from a given container.
   * @param {HTMLElement} container The container to clear
   */
  removeAllChildren(container) {
    while (container.firstChild) {
      container.removeChild(container.firstChild);
    }
  }

  /**
   * Adds all articles to each category container.
   * @param {Array} articles The articles data
   */
  addNewsArticlesToCategory(articles) {
    articles.slice(0, 5).forEach((article) => {
      const row = this.createRowElement(article);
      this.newsContainerWorld.appendChild(row.cloneNode(true));
    });
  }

  /**
   * Creates a DOM element for a row with article data.
   * @param {Object} article The article data
   * @returns {HTMLElement} The created row DOM element
   */
  createRowElement(article) {
    const row = document.createElement("div");
    row.classList.add("row");

    // ✅ Wrap the entire row inside an anchor tag to make everything clickable
    const link = document.createElement("a");
    link.href = article.url;
    link.target = "_blank"; // Open in a new tab
    link.style.textDecoration = "none"; // Optional: remove underline from the title
    link.style.color = "inherit"; // Optional: preserve original text color

    // Create and append the article title inside the link
    const title = document.createElement("h3");
    title.textContent = article.title;
    link.appendChild(title);

    // Create and append the clickable image or description inside the link
    if (article.urlToImage) {
      const img = document.createElement("img");
      img.src = article.urlToImage;
      img.alt = article.title;
      img.style.cursor = "pointer";
      link.appendChild(img);
    } else if (article.description) {
      const description = document.createElement("p");
      description.textContent = article.description;
      link.appendChild(description);
    }

    row.appendChild(link); // Append the link (containing all elements) to the row
    return row;
  }
}

// Initialize the ContentManager object
document.addEventListener("DOMContentLoaded", () => {
  new ContentManager();
});

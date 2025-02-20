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
    this.fetchRandomNews();
  }

  /**
   * Fetches random news articles from the back-end API and updates the DOM.
   */
  async fetchRandomNews() {
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
      this.addArticlesToCategory(articles);
    } catch (error) {
      console.error("Error fetching articles:", error.message);
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
  addArticlesToCategory(articles) {
    articles.forEach((article) => {
      const row = this.createRowElement(article);

      // Append the article to all category containers
      this.newsContainerReddit.appendChild(row);
      this.newsContainerLocal.appendChild(row.cloneNode(true)); // Use cloneNode to append the same article to multiple containers
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

    // Create and append the article title
    const title = document.createElement("h3");
    title.textContent = article.title;
    row.appendChild(title);

    // Create and append the article description
    const description = document.createElement("p");
    description.textContent = article.description;
    row.appendChild(description);

    // Create and append the "Read More" link
    const link = document.createElement("a");
    link.href = article.url;
    link.textContent = "Read More";
    link.target = "_blank"; // Open in a new tab
    row.appendChild(link);

    return row;
  }
}

// Initialize the ContentManager object
document.addEventListener("DOMContentLoaded", () => {
  new ContentManager();
});

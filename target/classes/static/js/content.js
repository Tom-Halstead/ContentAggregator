"use strict";

class ContentManager {
  constructor() {
    this.newsContainer = document.getElementById("article");
    this.randomizeBtn = document.getElementById("randomize");

    this.initEventListeners();
    this.fetchUserNewsSources();
  }

  /**
   * Initializes event listeners
   */
  initEventListeners() {
    this.randomizeBtn.addEventListener("click", () => this.fetchRandomNews());
  }

  /**
   * Fetches news sources from the back-end API.
   */
  async fetchUserNewsSources() {
    try {
      // Get the access token (assuming it's stored in localStorage)
      const accessToken = localStorage.getItem("access_token");

      if (!accessToken) {
        throw new Error("No access token found. Please log in.");
      }

      // Send the token in the Authorization header
      const response = await fetch("http://localhost:8080/api/news-sources", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${accessToken}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Failed to fetch news sources.");
      }

      const newsSources = await response.json();
      console.log(newsSources);
      this.displayNewsSources(newsSources);
    } catch (error) {
      console.error("Error loading news sources:", error);
    }
  }

  /**
   * Fetches random news and updates UI.
   */
  async fetchRandomNews() {
    try {
      const response = await fetch("http://localhost:8080/api/news/random");
      if (!response.ok) {
        throw new Error("Failed to fetch random news.");
      }
      const randomNews = await response.json();
      this.displayNewsSources(randomNews);
    } catch (error) {
      console.error("Error fetching random news:", error);
    }
  }

  /**
   * Displays news sources dynamically in the UI.
   */
  displayNewsSources(newsSources) {
    document.querySelectorAll(".news-container").forEach((container) => {
      container.innerHTML = ""; // Clear previous content
    });

    newsSources.forEach((source) => {
      const category = source.category || "General";
      const section = document.querySelector(
        `section[data-category="${category}"] .news-container`
      );

      if (section) {
        const articleElement = document.createElement("div");
        articleElement.classList.add("news-card");
        articleElement.innerHTML = `
          <h3>${source.name}</h3>
          <p><strong>Category:</strong> ${category}</p>
          <a href="${source.url}" target="_blank">Read More</a>
        `;
        section.appendChild(articleElement);
      }
    });
  }
}

// Initialize ContentManager when the page loads
document.addEventListener("DOMContentLoaded", () => {
  new ContentManager();
});

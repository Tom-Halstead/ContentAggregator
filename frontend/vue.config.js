module.exports = {
  outputDir: "../src/main/resources/static/vue", // Output Vue build to a subfolder
  devServer: {
    proxy: {
      "/api": {
        target: "http://localhost:8081", // Spring Boot backend
        changeOrigin: true,
      },
    },
  },
};

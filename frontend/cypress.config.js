import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
<<<<<<< HEAD
    specPattern: "cypress/e2e/**/*.{cy,spec}.{js,jsx,ts,tsx}",
    baseUrl: "http://ip24kk2.sit.kmutt.ac.th",
    baseAPI: "http://ip24kk2.sit.kmutt.ac.th/itb-mshop",
=======
    specPattern: 'cypress/e2e/**/*.{cy,spec}.{js,jsx,ts,tsx}',
    // baseUrl: 'http://localhost:5173',
    // baseAPI: 'http://locahost:8080/itb-mshop',
    baseUrl: 'http://ip24kk2.sit.kmutt.ac.th',
    baseAPI: 'http://ip24kk2.sit.kmutt.ac.th/itb-mshop',
>>>>>>> 472ce501d5d2921a962351998a84f22e52752f24
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },  
});

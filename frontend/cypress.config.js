import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    specPattern: 'cypress/e2e/**/*.{cy,spec}.{js,jsx,ts,tsx}',
    baseUrl: 'http://localhost:5173/kk2',
    baseAPI: 'http://locahost:8080/itb-mshop',
    // baseUrl: 'http://ip24kk2.sit.kmutt.ac.th',
    // baseAPI: 'http://ip24kk2.sit.kmutt.ac.th/itb-mshop',

    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },  
});

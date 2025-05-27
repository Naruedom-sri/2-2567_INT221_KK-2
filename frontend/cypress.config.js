import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    specPattern: 'cypress/e2e/**/*.{cy,spec}.{js,jsx,ts,tsx}',
    baseUrl: 'http://localhost:5173/kk2',
    baseAPI: 'http://locahost:8080/itb-mshop',
    // baseUrl: 'http://intproj24.sit.kmutt.ac.th/kk2',
    // baseAPI: 'http://intproj24.sit.kmutt.ac.th/kk2/itb-mshop',

    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },  
});

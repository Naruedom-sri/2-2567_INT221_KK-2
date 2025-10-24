// import { fileURLToPath, URL } from "node:url";

// import { defineConfig } from "vite";
// import vue from "@vitejs/plugin-vue";
// import vueDevTools from "vite-plugin-vue-devtools";
// import tailwindcss from "@tailwindcss/vite";

// // https://vite.dev/config/
// export default defineConfig({
//   base: "/kk2/",
//   plugins: [vue(), vueDevTools(), tailwindcss()],
//   resolve: {
//     alias: {
//       "@": fileURLToPath(new URL("./src", import.meta.url)),
//     },
//   },

  
// });
import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import tailwindcss from "@tailwindcss/vite";

export default defineConfig(async ({ mode }) => {
  const plugins = [vue(), tailwindcss()];

  if (mode === "development") {
    const { default: vueDevTools } = await import("vite-plugin-vue-devtools");
    plugins.push(vueDevTools());
  }

  return {
    base: "/kk2/",
    plugins,
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url)),
      },
    },
  };
});

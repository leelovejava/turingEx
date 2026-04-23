import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";
import Icons from "unplugin-icons/vite";
import AutoImport from "unplugin-auto-import/vite"; 
import Components from "unplugin-vue-components/vite"; 
import { ElementPlusResolver } from "unplugin-vue-components/resolvers"; 
import DefineOptions from "unplugin-vue-define-options/vite"; 
import { visualizer } from "rollup-plugin-visualizer";
import legacy from "@vitejs/plugin-legacy";

const isVisualizer = process.env.VISUALIZER === "show";
export default defineConfig({
  base: "/",
  plugins: [
    vue(),
    Components({
      resolvers: [
        ElementPlusResolver(),
      ],
    }),
    AutoImport({
      imports: ["vue"], 
      resolvers: [
        ElementPlusResolver(),
      ],
    }),
    Icons({
      autoInstall: true,
    }),
    DefineOptions(),
    isVisualizer && visualizer({ gzipSize: true }),
    legacy({
      targets: ["defaults", "not IE 11"],
    }),
  ],
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: '@import "@/assets/css/global.scss";', 
      },
    },
  },
  server: {
  },
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src"),
      "@comCommon": path.resolve(__dirname, "src/components/common"),
      "@comCompositeHome": path.resolve(
        __dirname,
        "src/components/compositeHome"
      ),
      "@comTrade": path.resolve(__dirname, "src/components/commonTrade"),
      "@comConstract": path.resolve(__dirname, "src/components/constract"),
      "@comSpot": path.resolve(__dirname, "src/components/spot"),
      "vue-i18n": "vue-i18n/dist/vue-i18n.cjs.js",
    },
  },
  build: {
    assetsDir: "static",
    rollupOptions: {
      input: {
        index: path.resolve(__dirname, "index.html"),
      },
      output: {
        chunkFileNames: "js/[name]-[hash].js",
        entryFileNames: "js/[name]-[hash].js",
        assetFileNames: "[ext]/name-[hash].[ext]",
      },
    },
  },
});

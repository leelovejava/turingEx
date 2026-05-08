import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import Components from 'unplugin-vue-components/vite';
import { VantResolver } from 'unplugin-vue-components/resolvers';
import DefineOptions from 'unplugin-vue-define-options/vite';
import { visualizer } from 'rollup-plugin-visualizer'
import legacy from '@vitejs/plugin-legacy';

const isVisualizer = process.env.VISUALIZER === 'show'
const useLegacy = process.env.BUILD_LEGACY === 'true'
export default defineConfig({
  base: '/', 
  plugins: [
    vue(),
    Components({
      resolvers: [VantResolver()],
    }),
    DefineOptions(),
    isVisualizer && visualizer({ gzipSize: true }),
    useLegacy && legacy({
      targets: ['defaults', 'not IE 11'],
      renderLegacyChunks: false,
    }),
  ].filter(Boolean),
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/assets/css/variables.scss" as *;`
      },
    }
  },
  server: {
    open: true,
    port: 333,
    hmr: true,
    host: '0.0.0.0',
    proxy: {
      "/api": {
        target: "https://api.helixcapital.net",   //填写反向代理8085后的域名
        changeOrigin: true,
        secure: false
      },
    },
  },
  resolve: {
    dedupe: [
      'vue'
    ],
    alias: {
      'vue-i18n': 'vue-i18n/dist/vue-i18n.cjs.js',
      "~": path.resolve(__dirname, './'),
      "@": path.resolve(__dirname, 'src'),
    }
  },
  build: {
    target: 'esnext',
    assetsDir: "static",
    reportCompressedSize: false,
    rollupOptions: {
      input: {
        index: path.resolve(__dirname, "index.html"),
      },
      output: {
        chunkFileNames: 'js/[name]-[hash].js',
        entryFileNames: "js/[name]-[hash].js",
        assetFileNames: "[ext]/[name]-[hash].[ext]"
      },
    },
  },
  externals: ['vue']
})

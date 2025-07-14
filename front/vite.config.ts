import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'
import mkcert from 'vite-plugin-mkcert'

// https://vite.dev/config/
export default defineConfig(({ mode}) => {

  const isLocal = process.env.NODE_ENV == 'development';
  const apiServer = 'https://localhost:8443'
  return  {
        server: {
          proxy: isLocal? {
            '/api':{
              target: apiServer,
              changeOrigin:true,
              secure:false,

            }
          } : undefined,
          watch: {
            usePolling: false
          }
        ,
        }
      , plugins: [
          react({
              include: "**/*.tsx"
            }
        ),
          mkcert()
      ],
      esbuild : {
          loader: "tsx", // OR "jsx"
              include : [
            // Add this for business-as-usual behaviour for .jsx and .tsx files
            "src/**/*.jsx",
            "src/**/*.tsx",
            "src/*.tsx",
            "node_modules/**/*.jsx",
            "node_modules/**/*.tsx",

            // Add these lines to allow all .js files to contain JSX
            "src/**/*.js",
            "node_modules/**/*.js",

            // Add these lines to allow all .ts files to contain JSX
            "src/**/*.ts",
            "node_modules/**/*.ts",
          ],
        }
      ,
        build: {
          outDir: '../src/main/resources/static/',
              emptyOutDir: true,
              sourcemap:  process.env.NODE_ENV == 'development'
        }
      }
  }
)

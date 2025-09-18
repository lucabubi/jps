# Step 1: Build the application
FROM node:18 AS build
WORKDIR /app

# Copia package.json e package-lock.json
COPY package*.json ./

# Installazione delle dipendenze
RUN npm install

# Copia del codice sorgente e build
COPY . .
RUN npm run build

# Step 2: Serve the build with Nginx
FROM nginx:1.25
WORKDIR /usr/share/nginx/html

COPY ./nginx/nginx.conf /etc/nginx/nginx.conf
# Copia i file statici generati dalla build
COPY --from=build /app/dist .


# Espone la porta 80
EXPOSE 80

# Avvia il server
CMD ["nginx", "-g", "daemon off;"]
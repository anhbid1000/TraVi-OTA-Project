FROM node:20-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

EXPOSE 5173

# Reinstall deps in the mounted /app/node_modules volume when packages are missing or invalid.
CMD ["sh", "-c", "npm ls antd leaflet react-leaflet --depth=0 >/dev/null 2>&1 || npm ci; npm run dev -- --host"]
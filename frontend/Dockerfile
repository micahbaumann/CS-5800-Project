FROM node:20-alpine

WORKDIR /app

# Install dependencies
COPY package.json package-lock.json* ./
RUN npm ci || npm install

# Copy source
COPY . .

# Build the Next.js app
ENV NODE_ENV=production
RUN npm run build

EXPOSE 3000

# Run the production server
CMD ["npm", "run", "start"]
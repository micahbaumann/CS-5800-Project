FROM node:20-alpine

WORKDIR /app

COPY package.json package-lock.json* /app/
RUN npm ci || npm install

COPY . /app

EXPOSE 3000

# Default command is overridden in compose for dev server
CMD ["npm", "run", "start"]

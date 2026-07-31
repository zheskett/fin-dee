FROM node:lts-bookworm-slim
WORKDIR /app

COPY package-lock.json package.json ./
RUN npm install

COPY . .
ENV NODE_ENV=production

EXPOSE 5008

ENTRYPOINT [ "npm", "start"]

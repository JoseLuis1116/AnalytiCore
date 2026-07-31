FROM node:22-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm ci

COPY . .

RUN npm run build


FROM nginxinc/nginx-unprivileged:stable-alpine

ENV API_PROXY_TARGET=http://python-service:8000
ENV NGINX_ENVSUBST_FILTER=API_PROXY_TARGET

COPY --chown=nginx:nginx nginx.conf /etc/nginx/templates/default.conf.template

COPY --chown=nginx:nginx --from=build /app/dist /usr/share/nginx/html

USER nginx

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --retries=10 \
  CMD wget -qO- http://127.0.0.1:8080/ > /dev/null || exit 1

CMD ["nginx", "-g", "daemon off;"]
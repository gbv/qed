FROM node:16
WORKDIR /home/node/app
COPY . ./
RUN yarn install && \
    yarn build
CMD ["node", ".output/server/index.mjs"]
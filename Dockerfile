FROM clojure
COPY . /usr/src/app
WORKDIR /usr/src/app
CMD ["lein", "run"]
EXPOSE 80
EXPOSE 7888
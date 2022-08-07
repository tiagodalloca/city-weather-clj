# city-weather-clj

Small web application which consumes the Open Weather API and makes use of Clojure's `atom` construct as cache.

## Config

Make sure you have both `java` and `clj` installed.

```bash
$ git clone https://github.com/tiagodalloca/city-weather-clj.git
$ cd city-weather-clj
$ chmod +x config
$ ./config
```

`./config` will ask you for you Open Weather API key and store it under `resources/secrests`

## Build

To build, simply:

```bash
clj -T:build uber
```

## Running

```
java -jar target/city-weather-clj-0.1.0-standalone.jar
```

# city-weather-clj

Small web application which consumes the Open Weather API and makes use of Clojure's `atom` construct as cache.

- [Config](#config)
- [Build](#build)
- [Running](#running)
- [About](#about)

## Config

Make sure you have both `java` and `clj` installed.

```bash
$ git clone https://github.com/tiagodalloca/city-weather-clj.git
$ cd city-weather-clj
$ chmod +x config
$ ./config
```

`./config` will ask for your Open Weather API key and store it under `resources/secrests`

## Build

```bash
clj -T:build uber
```

## Running

```
java -jar target/city-weather-clj-0.1.0-standalone.jar
```

You should be able to interact with `localhost:8989/weather/:city`, where `:city` is supposed to be a city name.

## About

Although simple in functionality, this small web application makes use of fundamental design principles that makes it easy to [test](./tests/city_weather_clj/http/handler/tests.clj), maintain and extend.

System state is managed explicitly and passed on to components via a dependency map with qualified keywords (take a look at [`get-handler`](./src/city_weather_clj/http/handler.clj) to see this in practice). It's easy to see how these dependencies are wired, thanks to [Integrant](https://github.com/weavejester/integrant) and [Aero](https://github.com/juxt/aero) which make system configuration declarative. Not only that, system-wide [start and stop](./src/city_weather_clj/system.clj) are easily implemented, and the system state is easy to track and debug, as it is unified in an atom containing a map. This principled approach reduces system components entanglement and instead favors composability.

API interaction is modelled making use of `defprotocol`, so that mocking is trivial and facilitating `get-city-weather-handler`'s tests.

The [cache](./src/city_weather_clj/cache.clj) is basically an atom and a map for the sake of brevity, but, because it is wrapped in a record implementing `StatefulWeatherCache`, adopting a more permanent solution such as [DataScript](https://github.com/tonsky/datascript) would be just a protocol implementation away.

This project was implemented making use of:
- [Integrant](https://github.com/weavejester/integrant) 
- [Aero](https://github.com/juxt/aero)
- [Malli](https://github.com/metosin/malli)
- [Reitit](https://github.com/metosin/reitit)
- [ring-jetty-adapter](https://clojars.org/ring/ring-jetty-adapter)

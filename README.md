# tag-data-relay

Provides a simple server to forward game tag data (`id`, `x`, `y`) to subscribed games. Built with Clojure, hosted on EC2.

The server provides the following endpoints:

- `/health`: Supports health checks, for example to check if server is running after deploy.
- `/api/subscribe?ip=<ip>`: For subscribing to tag updates. The caller must provide their IP address. The server uses UDP packets to send to updates and calling this API endpoint gives an UDP port in the response that the client should listen to. 
- `/api/unsubscribe?ip=<ip>`: For unsubscribing from tag updates. The caller must provide their IP address.
- `/api/update?id=<id>&x=<x>&y=<y>`: For pushing updates to subscribers. `id` is the tag id, `x` is the x-coordinate and `y` is the y-coordinate. The updates are sent to all the subscribers as UDP packets.

## Prerequisites

Development uses [Leiningen](https://leiningen.org/). 

## Development

### Install dependencies

`lein deps`

### Run development server + REPL

I personally use VSCode + Calva, so I just do `ctrl+alt+c ctrl+alt+j`. 
Also, `lein repl` works if you're more comfortable with that.

### Run tests

`lein test` 

## Build

`lein uberjar`

### Run the standalone build

`java -jar tag-data-relay/target/tag-data-relay-0.1.0-SNAPSHOT-standalone.jar`
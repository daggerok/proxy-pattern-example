# Proxy [![Build Status](https://travis-ci.org/daggerok/proxy-pattern-example.svg?branch=master)](https://travis-ci.org/daggerok/proxy-pattern-example)
Proxy Design Pattern java implementation

```bash
./mvnw clean ; ./mvnw test -U
```

<!--
```bash
./mvnw clean ; ./mvnw test -U ; ./mvnw versions:display-property-updates
```
-->

One of proxy pattern usage can be for example cached implementation:

```
                   +----------------------+     +--------+
                   | Gateway (shared API) |<----| Client |
                   +----------------------+     +--------+
                              ^
                              |
             +----------------+---------------+
             |                                |
+-------------------------+      +----------------------------+
| GatewayImpl (immutable) |      | GatewayCachedProxy (proxy) |
+-------------------------+      +----------------------------+
```

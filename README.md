# Proxy [![Build Status](https://travis-ci.org/daggerok/annotate-me.svg?branch=master)](https://travis-ci.org/daggerok/annotate-me)
Proxy Design Pattern java implementation

```bash
./mvnw clean ; ./mvnw test -U
```

<!--
```bash
./mvnw clean ; ./mvnw test -U ; ./mvnw versions:display-property-updates
```
-->

```
                 +----------------------+     +--------+
                 | Gateway (shared API) |<----| Client |
                 +----------------------+     +--------+
                            ^
                            |
            +---------------+---------------+
            |                               |
+-------------------------+   +----------------------------+
| GatewayImpl (immutable) |   | GatewayCachedProxy (proxy) |
+-------------------------+   +----------------------------+
```

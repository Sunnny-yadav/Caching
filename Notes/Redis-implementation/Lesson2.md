# Redis Caching in Spring Boot

### 1. Run a Redis server

Spin up a Redis container (e.g., via Docker Compose), so there's an actual server for the application to connect to.

### 2. Add the dependency

`spring-boot-starter-data-redis` in `pom.xml`. This pulls in Lettuce (the Redis client library for Java, enabling the application to communicate with the Redis server) and Spring Data Redis, which provides `RedisTemplate` for direct, low-level Redis operations, and also integrates with the Spring Cache abstraction.

### 3. Configure the connection explicitly

Add `spring.data.redis.host` and `spring.data.redis.port` in `application.properties`. Even though Spring Boot defaults to `localhost:6379` if these are omitted — which is why a local setup connects successfully without them — this default breaks once the Spring Boot application itself is containerized: `localhost` inside the app's own container no longer points to the separate Redis container, since each container is isolated, causing connection failure. Configuring these explicitly from the start means the values can simply be replaced later (e.g., with the Redis service's container name) without hunting for a hidden default.

### 4. Reuse existing Spring Cache annotations

`@Cacheable`, `@CachePut`, `@CacheEvict` require no code changes. Once Spring Data Redis is on the classpath, Spring Boot's auto-configuration automatically swaps the default in-memory `CacheManager` for a `RedisCacheManager`.

### 5. Serialization problem

By default, Spring Data Redis uses JDK's built-in serialization, which requires the entity to implement `Serializable`. Implementing it resolves the initial `IllegalStateException: Cannot serialize value ... without a serializer` and allows caching to function — but the stored value in Redis (verifiable via `redis-cli GET`) is an unreadable, garbled binary string, since JDK serialization isn't human-readable or interoperable with non-Java systems.

The correct fix is to not rely on `Serializable`, and instead configure a `RedisCacheConfiguration` bean using `GenericJacksonJsonRedisSerializer`, which uses Jackson to serialize values as readable JSON.

### 6. Class-cast/type problem on read

Plain JSON serialization doesn't embed any information about which Java class the JSON represents. On deserialization, without that information, Jackson defaults to a generic `LinkedHashMap`, causing a `ClassCastException` when the code expects the actual entity type.

The fix is `activateDefaultTyping(ptv, DefaultTyping.NON_FINAL)` on the `ObjectMapper`:

- `DefaultTyping.NON_FINAL` controls which values get type metadata embedded during serialization — non-final, custom classes (like entities) get it; simple built-in types do not, since there's no ambiguity about their type.
- `ptv` (a `PolymorphicTypeValidator`) is a security allowlist checked at deserialization time, restricting which class names Jackson is permitted to actually instantiate from embedded JSON type metadata — preventing a polymorphic deserialization attack, where an attacker could otherwise embed an arbitrary, malicious class name into JSON and have the server instantiate it.
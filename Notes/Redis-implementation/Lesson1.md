# Spring Cache Abstraction

Spring Cache abstraction eliminates the cross-cutting concern of caching logic (check cache → fetch on miss → store → return) by letting it be declared via annotations instead of written manually in every method.

**@Cacheable** — applied to read methods. On invocation, the request first goes through the cache. If the data exists (cache hit), it's returned immediately and the actual method body is never executed. If not (cache miss), the real method runs, fetches the data (e.g., from the database), and the result is stored in the cache before being returned. This is the cache-aside pattern, applied declaratively.

**@CachePut** — applied to update methods. The method body always executes (since the underlying data is being modified), and the cache is updated synchronously with the new result afterward. This implements write-through behavior — cache and database stay in sync immediately.

**@CacheEvict** — applied to delete methods. Once the underlying data is deleted, the corresponding entry is removed from the cache, preventing stale data from being served afterward.

**@EnableCaching** — must be added to a `@Configuration` class to activate all of the above; without it, these annotations have no effect.

## Where the cache lives by default

Unless configured otherwise, Spring Cache abstraction stores cached data in-memory (a simple `ConcurrentHashMap`) — not in Redis. Connecting it to Redis specifically is the next step (Lesson 9).

## Self-invocation gotcha

When a `@Cacheable` method is called from another method within the same class using "this", the caching logic does not trigger — the call goes directly to the real method body instead. This happens because Spring's caching behavior is implemented via a proxy object that wraps the real class instance, and it is this proxy that's registered in the IoC container and injected wherever the service is used. "this", inside the class, always refers to the real underlying object — not the proxy — since the code is executing from within that real object itself. The proxy, which holds all the cache-check logic, is therefore never invoked. Critically, this fails silently: no exception is thrown, and the method still returns correct data, but caching simply never occurs for that call path — leading to unnoticed, slower-than-expected response times rather than an obvious error.
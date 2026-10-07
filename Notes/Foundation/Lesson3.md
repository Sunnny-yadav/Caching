# Caching Strategies

Caching strategies define when data enters or updates in the cache, and who — the application or the cache itself — is responsible for keeping it synchronized with the database.

## Read strategies

### 1. Cache-aside (lazy loading)

The application checks the cache first. On a hit, data is returned directly. On a miss, the application queries the database itself, stores the result in the cache, then returns it. The application owns the synchronization logic (to keep cached data updated). Only data that has actually been requested gets cached — nothing is cached speculatively.

**Disadvantage:** the first request for any given key is always a cache miss, and if the DB changes outside this flow, the cache has no way of knowing unless paired with an invalidation or write strategy.

### 2. Read-through

Structurally similar to cache-aside, but the cache service itself is responsible for fetching from and synchronizing with the database. The application only ever talks to the cache and never queries the database directly.

### 3. Refresh-ahead

The cache proactively refreshes its data at a defined time interval, so it stays relatively current without waiting for a miss.

**Disadvantage:** infrequently accessed data may still get refreshed unnecessarily, wasting effort, and there remains a staleness window between writes and the next refresh cycle.

## Write strategies

### 1. Write-through

A write updates the cache and the database synchronously, as a single logical operation, so the cache is always immediately consistent with the database.

**Disadvantage:** higher write latency (two operations instead of one), and if the DB write fails, the cache write must be rolled back to avoid inconsistency.

### 2. Write-behind (write-back)

The write goes to the cache first, and the response is returned to the user immediately from there. The cache asynchronously flushes the write to the database later, in batches. This solves the latency problem of write-through.

**Disadvantage:** if the caching service fails before the flush completes, the data is lost and the database update never happens.

## Conclusion

Cache-aside is the default choice for reads in most real systems — simple, safe, and demand-driven. When a system needs the cache to always reflect the latest write with no staleness window, cache-aside is paired with write-through, not refresh-ahead — refresh-ahead only reduces staleness on an interval, it doesn't eliminate it.

## So, after studying this strategies we can say that

A CDN follows the read-through caching pattern. In read-through, the client communicates only with the cache — never directly with the source of truth — and the cache itself is responsible for fetching and populating data from the source on a miss.

This maps exactly onto CDN behavior: the user's browser only ever talks to the CDN edge server (a caching reverse proxy), never to the origin server directly. On a cache hit, the edge server serves the content itself. On a cache miss, the edge server internally fetches from the origin, caches the response, and returns it — all transparent to the client.

This is distinct from cache-aside (e.g., Redis in a Spring Boot app), where the application code explicitly owns the check-cache → query-DB-on-miss → populate-cache logic. With a CDN, that same responsibility is handled internally by the CDN infrastructure, not by application code.

**Conclusion:** CDN = read-through pattern, implemented via a geographically distributed caching reverse proxy.
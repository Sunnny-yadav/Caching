# Eviction and Expiry

This topic deals with the fact that cache memory is finite, and covers two separate concerns: when should an entry expire even if there's still room in the cache, and which entry should be evicted when the cache is full and something has to go.

## TTL (Time To Live)

TTL handles the first concern. Every cache entry is given a defined duration after which it's considered invalid and removed, freeing up space — regardless of whether memory pressure exists. TTL is essential for bounding staleness, especially in patterns like cache-aside where nothing else guarantees the cache stays in sync with the database. The TTL duration is chosen based on how tolerant the data is to being outdated:

- **Short TTL** — data that changes frequently and must stay current, e.g., live vote counts in an election tracker.
- **Long TTL** — data that's largely static and independent of fast-changing values, e.g., a candidate's biography or party manifesto.

## Eviction Policies

Eviction policies handle the second concern — deciding what to remove once the cache hits its memory limit, even if TTLs haven't expired yet.

- **LRU (Least Recently Used)** — evicts the entry that hasn't been accessed in the longest time. Used as the default in most real systems, including Redis.
- **LFU (Least Frequently Used)** — evicts the entry with the lowest total access count, ignoring recency entirely. Example: Product A accessed 1000 times total survives, while Product B — accessed only 10 times, even if one of those accesses was very recent — gets evicted, because LFU only cares about cumulative frequency, not how recently it was touched.

## Question — LRU vs. LFU, when to use which

**Use LRU when access patterns are recency-driven** — recent activity is the best predictor of near-future activity.

Real-world example: a news website's article cache, or a job portal's "recently posted jobs" feed. What's trending right now matters more than historical total views. An article that went viral yesterday but nobody's reading today should be evicted in favor of what's hot now. This is why LRU is Redis's default and the most common production choice — most workloads are recency-biased.

**Use LFU when you have a stable "hot set"** that shouldn't be evicted just because of a temporary access lull.

Real-world example: an e-commerce product catalog cache for a company's top-selling, evergreen products (e.g., a bestselling phone model). If there's a brief gap in traffic to that product (say, overnight), LRU might evict it just because it wasn't accessed recently — even though it will spike again during the day. LFU protects it because its cumulative access count remains high, correctly recognizing it as consistently valuable rather than momentarily cold.

**The trade-off to be aware of:** LFU requires tracking and maintaining access counts per key, which adds slight memory and computation overhead compared to LRU's simpler "last accessed timestamp" tracking. Also, LFU has a known weakness — a newly cached item starts with a low count and can get evicted quickly even if it's about to become popular, unless the implementation has some ramp-up protection (Redis's LFU implementation, for instance, uses a probabilistic counter with decay to address this).

**In practice:** most production systems default to LRU unless they've specifically identified a workload where a known hot set needs protection from short-term traffic dips — LFU is the deliberate exception, not the default.
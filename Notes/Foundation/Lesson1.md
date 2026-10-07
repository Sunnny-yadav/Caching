# What is caching, and why does it exist?

Caching is a mechanism for storing data that is frequently accessed, or the result of an expensive computation, in a faster storage layer — so that repeated requests don't have to pay the cost of fetching or recomputing it every time.

It exists because, without it, every request for the same data hits the database directly. The database is a finite resource: it has a limited connection pool. Repeated identical reads consume connection pool capacity and add query load, degrading both latency and overall system efficiency under load.

## Core terminology

- **Cache hit** — when a request checks the cache first and the data is found there; it's returned immediately without touching the database.
- **Cache miss** — when the data isn't in the cache; the application falls back to the database, then (in cache-aside) populates the cache for next time.
- **Hit ratio** — hits ÷ total requests. Since cache memory is limited, this ratio tells you whether the data occupying that space is actually earning its keep. A low hit ratio means you're spending memory on data that isn't being reused.

## The trade-off caching introduces

Caching creates two copies of the same data — the source of truth (DB) and the cached copy. This trade-off shows up in two distinct ways:

- **Staleness from lag** — if the DB is updated but the cache isn't invalidated or refreshed, the cache keeps serving the old value until it expires or is explicitly evicted.
- **Staleness from ordering** — even with an invalidation strategy in place, if cache and DB writes happen in the wrong order (e.g., cache is repopulated before a DB write commits, or two concurrent requests race to update cache and DB in different sequences), you can still end up serving incorrect data despite "having" an invalidation mechanism.

So the fundamental trade-off is: caching buys speed and reduces database load, at the cost of a consistency risk.
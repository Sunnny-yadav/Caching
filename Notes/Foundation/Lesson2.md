# Caching Placement / Layers

Caching exists at multiple layers between the user and the database, each solving a different problem:

### 1. Browser cache

Data cached locally on the individual user's device, controlled by the backend via HTTP cache headers (Cache-Control, ETag). Scope: one user, one device.

### 2. CDN

A network of geographically distributed edge servers sitting between the user and the origin server. Caches static assets and sometimes cacheable API responses. When a user requests a static asset (image, JS, CSS) or sometimes a full cacheable API response, the request first hits the nearest CDN edge server (e.g., a data center in Mumbai if the user is in India), not your origin server directly.

- If that edge server already has the content cached → it's served from there, and your origin server (and your backend code) never even sees the request.
- If not → the CDN fetches it from your origin once, caches it at that edge location, and serves all future nearby users from there.

Scope: many users, grouped by geography.

### 3. Application-level (local/in-memory) cache

Data cached inside a single application instance's own memory (e.g., Caffeine in a JVM). Fastest possible access, no network hop — but scoped only to that one instance.

### 4. Distributed cache

A separate, dedicated caching server (e.g., Redis) that all instances of the application connect to over the network. This solves three specific problems that local/in-memory caching creates once you run multiple instances:

- **False cache misses** — data cached on one instance isn't visible to another instance, so a request landing on instance B misses even though instance A already cached it.
- **Memory duplication** — the same data gets cached redundantly across every instance, wasting memory that scales with instance count instead of being shared.
- **Inconsistency across instances** — different instances can end up holding different versions of the same data, since each updates its own local copy independently.

### 5. Database-level cache

Caching internal to the database or persistence framework itself (e.g., Postgres's buffer cache, Hibernate's first- and second-level cache), used to optimize repeated query execution and parsing.
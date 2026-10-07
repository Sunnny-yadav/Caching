# Data Types

Redis stores data as key-value pairs. The key is always a string, and the value can be one of several data structures. Choosing the structure is a design decision, because each one comes with its own native operations and access patterns.

**String** — for a single value (number, text, flag). Redis has no separate number or boolean type, so everything simple is a String. It supports atomic INCR/DECR. Example: rate limiting. Count hits per user per API, and once the limit is reached, throttle the user (HTTP 429).

**Hash** — for structured data with multiple named fields. Fields can be read or updated individually (HSET, HGETALL). It is flat, with no nested objects. Example: a user profile fetched from the DB and cached.

**List** — for ordered data where insertion order matters and you process from one end (FIFO queue or LIFO stack). Example: an email-sending queue.

**Set** — for unique values where you only care about membership (present or not) and order doesn't matter. Membership checks are O(1). Example: preventing duplicate applications by the same user to the same job.

**Sorted Set** — unique members, each with a score, kept automatically ordered by score. It lets you fetch the top N without sorting in application code. Example: a leaderboard of the top 5 most-viewed jobs.

Notice the pattern: the key is always a plain string (`user:501`, `job:1024:applicants`) — it's the value's shape that changes. This is also why Redis key naming conventions matter (`entity:id:field` style, as shown above) — it keeps related keys grouped and readable even though Redis has no folders/tables like a relational DB.

## Note: JSON in a String vs. a Hash

You can store a JSON object as a String, but you must serialize it before writing and deserialize it after reading. To update one field, you would deserialize the whole object, modify it, re-serialize it, and write it back. A Hash provides native per-field operations, so you update just that field. This is why Hash is preferred for structured data with frequent partial updates. For flat data it avoids the serialization overhead. For nested data or whole-object reads and writes, String + JSON remains a valid choice.
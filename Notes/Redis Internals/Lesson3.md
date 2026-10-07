# Persistence

Redis stores its entire dataset in RAM, which is volatile — a server crash or restart normally means total data loss. To prevent this, Redis persists data to disk using two mechanisms:

**RDB (Redis Database)** — Redis takes a snapshot of the entire dataset everytime at defined intervals and writes it to a disk file. On restart, Redis loads this file to restore the data, which is fast since it's just one file. The drawback: any writes made after the last snapshot but before a crash are permanently lost, since they were never captured. This introduces a consistency/data-loss risk.

**AOF (Append-Only File)** — every write operation is logged, in order, to a file as it happens. On restart, Redis replays this log to reconstruct the dataset. This minimizes data loss compared to RDB, since even writes made moments before a crash can be recovered by replaying the log.

In production, both are typically used together: RDB provides fast, compact full backups and quick restarts, while AOF fills the gap by recovering writes made since the last snapshot. This reflects the underlying trade-off between consistency (data-loss risk) and performance (speed of writes/restarts).

## When persistence is and isn't needed

- If Redis is used purely as a cache for data that's reproducible from the database (cache-aside pattern), persistence can be relaxed or disabled — a crash just means the next request is a cache miss, and the data repopulates naturally from the DB.
- If Redis stores data with no other source of truth — sessions, OTPs, rate-limiting counters, queue contents — persistence becomes essential, since losing that data means losing it permanently, not just taking a performance hit.
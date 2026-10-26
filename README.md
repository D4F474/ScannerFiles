# ScanFiles

A desktop disk space analyzer written in Java (Swing). It shows what takes up space inside a chosen folder: every item in the top level is one row, and folders show their total size, including everything inside them.

I built it to practice recursion and concurrency, and to measure what parallelism actually gives you on a real file system.

## Features
- Choose any folder with a folder picker and scan it
- One row per top-level item (name, size, modified date, folder flag, path)
- Folder size calculated with a recursive depth-first search (DFS)
- Human-readable sizes (B / KB / MB / GB)
- Parallel scanning with a configurable number of worker threads
- Unreadable folders (no permissions) are skipped instead of crashing the scan

## How it works
- **DFS:** the size of a folder is the sum of the sizes of its children. Files return their length, folders recurse. Results travel back up the tree, so a folder's size is ready the moment the recursion leaves it.
- **Thread pool:** each top-level item is submitted as a separate task to a `ThreadPoolExecutor`. Results are collected through `Future`s, and the pool is shut down in a `finally` block.
- **No shared mutable state:** every task builds its own `ScanItem`, so there are no race conditions and no locks are needed.

## Benchmarks
Same machine, same folders, different thread counts:

| Folder | 1 thread | 4 threads | 8 threads |
|---|---|---|---|
| `C:\Users` | 53.6 s | 53.7 s | 54.3 s |
| `C:\Program Files` | 27 s | 15 s | 15 s |
| `C:\` (whole disk) | 200 s | 119 s | 113 s |

### What the numbers show
- Going from 1 to 4 threads helped a lot on `Program Files` (about 1.8x) and on the whole disk (about 1.7x).
- Past 4 threads there is almost no gain.
- On `C:\Users` extra threads did nothing. One folder (`power`, about 150 GB) dominates, and since a folder is a single task, the other threads finish early and wait for it. This is **load imbalance**: total time is set by the slowest task, not by the number of threads.
- The disk itself is a second limit, because many threads reading the same drive eventually hit its throughput.

## Known limitations
- Symlinks and junctions are not handled yet, so scanning `C:\` can count data twice.
- Folders without read access show 0 B instead of a clear "no access" marker.
- Parallelism works only at the top level. A single huge folder is still scanned by one thread.
- Dates use last-modified time, not creation time.

## Roadmap
- [ ] Fork/Join scanning, so one huge folder is split across all threads
- [ ] Symlink and junction protection
- [ ] Background scanning with `SwingWorker` and a progress indicator, plus cancel
- [ ] Sort by size, enter folders by double click, "up" button, open in Explorer
- [ ] Bar chart of space by category (video, images, archives, documents, other)
- [ ] Benchmarks for the Fork/Join version

## Tech
Java, Swing, `ThreadPoolExecutor`, `Future`, recursive DFS, `java.io.File`

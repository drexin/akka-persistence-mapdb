# MapDB Journal for Akka Persistence
A journal plugin for akka-persistence using [MapDB](http://www.mapdb.org).

## Requirements
Akka 2.3.0-RC1 or higher

## Installation
There have no artifacts been published to a public repository, yet. To
use this journal you have to compile and publish it to your local
repository with `sbt publishLocal` and include it in your project:

    libraryDependencies += "io.github.drexin" %% "akka-persistence-mapdb" % "0.1-SNAPSHOT"

## Configuration

Add to your application.conf

    akka.persistence.journal.plugin = "mapdb-journal"

### Additional Settings

```
mapdb-journal {
    dir = "journal"
    async-writes = false
}
```

Setting `async-writes = true` drastically improves performance.
With synchronous writes the journal has an average number of around
10,000 writes per second, while asynchronous writes achieve up to
200,000 writes per second on a 4-core machine. Please consult the MapDB
documentation for more information about this setting.


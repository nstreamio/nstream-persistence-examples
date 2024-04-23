# Running the Examples

In all of the following examples, the server can be terminated from the terminal with `Ctrl-C`.

## Cassandra

The Cassandra example applications expect a Cassandra instance to be running at `localhost` on the  default ports. The
Cassandra store uses the Datastax Cassandra driver which can be configured by placing a suitable `applicaiton.conf`
client configuration file onto the classpath.

To run the examples with gradle, run the following from within `cassandra-example` or `cassandra-kv-example`:

```shell
../gradlew run
```

The `cassandra-example` project uses the direct Cassandra store implementation (changes to the state are written into
Cassandra immediately and asynchronously). Alternatively, the `cassandra-kv-example` uses Cassandra via the key-value
store adapter. Here, changes are batched up in memory and periodically flushed to Cassandra, synchronously.

The Swim configuration files for these examples are found in [cassandra-example](cassandra-example/src/main/resources/server.recon)
and [cassandra-kv-example](cassandra-kv-example/src/main/resources/server.recon).

## Ignite

There are two Ignite example projects, `ignite-example` and `ignite-standalone-example`. The `ignite-example` project
is fully configurable (and requires you to set up your own `IGNITE_HOME` directory and Swim configuration files).
Alternatively, the `ignite-standalone-example` will create its own `IGNITE_HOME` in a temporary folder and create a Swim
configuration file that points at this.

For the stand-alone example, run the following in the `ignite-standalone-example` directory:

```shell
../gradlew run
```

The `ignite-example` project can be run in two ways. First, one can instruct it to initialize the Ignite cluster
(without starting the Swim server). Subsequently, it can be run in such as way as to start the Ignite cluster and then
connect the Swim sever to it.

To initialize the cluster, run the following in the `ignite-example` directory.

```shell
../gradlew run --args="--init-only --ignite /path/to/ignite.xml --home /path/to/ignite-home"
```

Additionally, `--wait n` can be passed to control how long the application will wait for the cluster to start. This is
in seconds and the default value is `10`.

To run the swim server, then run:

```shell
../gradlew run --args="--home /path/to/ignite-home --swim /path/to/server.recon"
```

See the [Apache Ignite documentation](https://ignite.apache.org/docs/latest/) for information on configuring Ignite.

## RocksDB

The RocksDB example will create a database in the local filesystem (at `/tmp/example-app/`). If you need to alter this,
it is configured in the [Swim configuration file](rocksdb-example/src/main/resources/server.recon).

To start it, run the following in the `rocksdb-example` directory:

```shell
../gradlew run
```

# OrientDB CSV graph data loader

OrientDB provides several options, when it comes to loading larger amounts of graph data (check [1] for importing large amounts of JSON documents).
We aim for loading CSV data, but the discussed options can be applied to other formats.

### Approaches
First, if the data consists of simple values like `long` data types, the [graph batch insert](http://orientdb.com/docs/3.0.x/java/Graph-Batch-Insert.html) can be used.

Secondly, in case of more complex-value property graphs, the multi-model or SQL APIs should be used.
The SQL interface requires a query for vertex class entries like

```CREATE EDGE Foo FROM (select from VertexClass WHERE id = 'foobar') TO (SELECT FROM OtherVertexClass WHERE somethinElse > 100)```,

which requires the careful creation of indices to gain performance due to the required selection of the source and target vertices.
(cf. `db.getMetadata().getIndexManager().getIndex("indexName").get(id)`)

Similarly, in the new multi-model API, OrientDB requires the vertex instances to create edges (cf. `OEdge newEdge(OVertex var1, OVertex var2, String var3);`.
Consequently, the vertices could be queried everytime a new edge is created (requires a corresponding index) or the vertices could be kept in memory. While the first option is increasingly slow due to repeated scans over the vertex index to find the correct entry, the second option requires extra memory, which could become critical as well.

To relax the latter concern, we recall that OrientDB works efficiently with record identifiers `ORID`. Those ORIDs are smaller in memory footprint, are returned as a records identity (`vertex.getIdentity()`), and can be used to load vertices on-demand by an O(1) lookup by `db.load(ORID)`, when needed (compared to a slower index scan for the first option). (vertices contain all outgoing edges, which increases the memory consumption)

Further, there exist disk-based maps in Java that allow for the storage of the `value` on disk, but keeping the `key` in memory for fast lookups to reduce the memory footprint even further by offloading the ORIDs to disk.

With those considerations, we tried the following load mechanism:

The idea that we tryout is to do the following:
```
1. stream vertex data from file
2. create vertices by setting the key only and store vertex identifiers in persistent map with ORIDs offloaded to disk
3. stream edge data from file
4. create edges by lookup of ORIDs, load corresponding vertices and set properties
5. stream vertex data from file
6. update OVertex instances
7. verify result (query)
```

There are at least two variants on how to load the data: `remote` vs. `embedded`. While `remote` is rather straight forward, `embedded` runs the server within the `data loader` (and thus does not require IPCs). The data is stored to disk and can be read from disk by any ODB instance that has access to the database in its `databases` folder. While the data can be seamlessly used in the ODB studio in both cases, the `embedded` version has faster load times.

```
                                                  Frontend
                                                 +------+
                                                 |Studio|
                                                 +---+--+
                                                     ^
           data loader                        DB     |
          +----------------+   remote        +-------+-------+
Variant A |orientdb|client +----------------->orientdb|server|
(remote)  |orientdb|graphdb|                 +-----+-+-------+
          +----------------+                       ^ |
                                         from disk | | to disk
           data loader                             | v
Variant B +-----------------+                    +-+-++
(embedded)| DB              |              Disk  |Data|
          +-----------------+                    +----+
    +---->-|orientdb|server|| +--------------------^
    |     +-----------------+         to disk
    |     |                 |
    +<----+orientdb-client  |
  embedded|orientdb|graphdb |
          +-----------------+

```

**Usage**:
```
java -jar odbgraphdataloader.jar -host [plocal:/<path>|remote:<host>] \
-user <user> -password <passwd> -dbname <dbname> -vertexFileName <path-vertex>.csv \
-edgeFileName <path-edge>.csv -batchSize 1000 [-numberVertices <numberVertices>] [-numberEdges <numberEdges>]
```

### Summary

There are several different approaches to store large amounts of graph data into OrientDB.
We tried out one, which worked for a real-world data set.
If you try out others, which worked well or not, feel free to share them here.

Alternative design approaches from ground up with **batch processing** framework include but are not limited to:
- Apache Samza: http://samza.apache.org/
- Spring-batch: https://spring.io/projects/spring-batch
- Easy batch: https://github.com/j-easy/easy-batch

or CSV libraries

- univocity-parsers

or misc

- external sorting: https://github.com/lemire/externalsortinginjava

TODOs:
- multi-threading
- add stats: `SummaryStatistics`

### References
[1] iiBench: https://github.com/dritter-sap/iibench-mongodb

### Acks

Thanks go to 
- MemoryUtils http://www.java2s.com/Code/Java/Development-Class/ReturnsusedmaxmemoryinMB.htm
- FileMap: https://github.com/dkpro/jweb1t/blob/master/src/main/java/com/googlecode/jweb1t/FileMap.java
that inspired parts of the code.

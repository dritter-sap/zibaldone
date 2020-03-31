# OrientDB CSV data loader

OrientDB provides several options, when it comes to loading larger amounts of graph data.
We aim for loading CSV data, but the discussed options can be applied and the source code can be adapted to other formats.

### Approaches
First, if the data consists of simple values like `long` data types, the [graph batch insert](http://orientdb.com/docs/3.0.x/java/Graph-Batch-Insert.html) can be used.

Secondly, in case of more complex-value property graphs, the multi-model or SQL APIs should be used.
The SQL interface requires a query for vertex class entries like

```CREATE EDGE Foo FROM (select from VertexClass WHERE id = 'foobar') TO (SELECT FROM OtherVertexClass WHERE somethinElse > 100)```,

which requires the careful creation of indices to gain some performance.

In the new multi-model API, OrientDB requires the vertex instances to create edges (cf. `OEdge newEdge(OVertex var1, OVertex var2, String var3);`.
The idea that we tryout is to do the following:

```
1. stream vertex data from file
2. create vertices, set properties and store OVertex instances in memory
3. stream edge data from file
4. create edges by lookup of OVertex instances and set properties
```
Alternatively, consider keeping smaller OVertex instances in memory at a time (key only):

```
1. stream vertex data from file
2. create vertices by setting the key only and store OVertex instances in memory
3. stream edge data from file
4. create edges by lookup of OVertex instances and set properties
5. stream vertex data from file
6. update OVertex instances
```

### Evaluation

Evaluate on real-world data with 3.9 GB of node and 39.5 GB of edge data.
OrientDB v3.0.24, Java 1.8:

|                   | Time                    | Space-tool(mem)|
|-------------------|-------------------------|----------------|
| Nodes (23876665)  | 782771 ms (~13.02 min)  | 8,193.05 MB    |
| Edges (123953512) |                         |                |

**Usage**:
```
java -jar odbgraphdataloader.jar -host [plocal:/<path>|remote:<host>] \
-user <user> -password <passwd> -dbname <dbname> -vertexFileName <path-vertex>.csv \
-edgeFileName <path-edge>.csv -batchSize 1000 [-numberVertices <numberVertices>] [-numberEdges <numberEdges>]
```

### Summary

There are several different approaches to store large amounts of graph data into OrientDB.
We tried out one, which worked for a real-world data set.
If you tried out others, which worked or not, feel free to share them here.

Alternative design approaches from ground up with batch processing framework include but are not limited to:
- Apache Samza: http://samza.apache.org/
- Spring-batch: https://spring.io/projects/spring-batch
- Easy batch: https://github.com/j-easy/easy-batch

Further interesting libraries?
- launch4j: http://launch4j.sourceforge.net/index.html

TODOs:
- add stats: `SummaryStatistics`
- `db.getMetadata().getIndexManager().getIndex("indexName").get(id)`
- document node contains all outgoing edges, which increases the memory consumption

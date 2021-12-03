**Overview of queries, properties and indexes**

```
# Query 1
SELECT * FROM series WHERE iid = "GSE105766" LIMIT -1

create index iid_idx on series (iid) NOTUNIQUE

# Query 2
SELECT characteristics FROM (SELECT expand(channels) FROM (SELECT expand(outE("has_sample").inV()) FROM series WHERE iid = "GSE105766")) LIMIT -1


# Query 3
SELECT characteristics FROM (SELECT expand(channels) FROM sample WHERE iid = "GSM4521284") LIMIT -1

create index sample_iid on sample (iid) NOTUNIQUE



# Query 4
SELECT * FROM sample where channels containsAny (select * from channel where characteristics containsKey "treatment_raw") LIMIT -1

create index sample_channels on sample (channels) NOTUNIQUE


# Query 5
SELECT * FROM sample WHERE channels containsAny (select * from channel where characteristics.treatment_raw = 'H1') LIMIT -1

# Query 6
SELECT * FROM series WHERE platforms containsAny (select * from platform where iid = "GPL24995") LIMIT -1

create property series.platforms LINKLIST platform
create index series_platforms on series (platforms) NOTUNIQUE
create index platform_iid on platform (iid) NOTUNIQUE
```
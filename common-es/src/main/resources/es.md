es搜索

1.query string search 参数拼在url后面
GET /index/type/_search
GET /inde/type/_search?q=name:xxx&sort=xxx:desc

2.query dsl 参数在body体内

GET /index/type/_search

```json
{
"query":{"match_all":{}}
}
```
```json
{
"query":{
"match":{
"name": "xxx"
},
"sort": [{"age": "desc"}]
}
}
```
```json
{
"from": 1,
"size": 100,
"query":{
"match":{
"name": "xxx"
}},
"sort": [{"age": "desc"}],
"_source": ["xxx","xxx"]

}

```

3.query filter 查询过滤
```json
{
"query": {
"bool": {
"must": {
"match": {"name": "xxx"},
"filter": {"range": {
"age": {
"gt": 10
}
}}
}
}
}

}
```

4.full-text search 全文检索
会将xxx xxx进行分词,只要这个name匹配到其中一个就算有
``json
{
  "query": {
    "match": {
      "name": "xxxx xxx"
    }
  }
}
``

5.phrase search 短语搜索
和全文搜索刚好相反,必须全部匹配
```json
{
  "query": {
    "match_phrase": {
      "name": "xxxx xxx"
    }
  }
}
```

6.highlight search 高亮
```json
{
  "query": {
    "match_phrase": {
      "name": "xxxx xxx"
    }
  },
  "highlight": 
  {"fields": 
    {"name":{
    }}}
}
```



聚合

```json
{
 "aggs": {
   "counts": {
       "terms": {"filed": "xxx"}
         }
    }
}

```
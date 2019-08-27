package cn.pmj.common.es.code;

import cn.pmj.common.es.EsBaseTest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.Test;

public class SearchTest extends EsBaseTest {

    @Test
    public void test(){
        QueryBuilder matchQuery = QueryBuilders.matchQuery("title","python").operator(Operator.AND);
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("title").preTags("<span stype = \"color:red\"")
                .postTags("</span>");

        System.out.println(matchQuery.toString());
        SearchResponse books = client.prepareSearch("books")
                .setQuery(matchQuery)
                .highlighter(highlightBuilder).setSize(100).get();
        SearchHits hits = books.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    @Test
    public void search(){
        //全文...
        //MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        //MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery();
        //MatchPhrasePrefixQueryBuilder matchPhrasePrefixQueryBuilder = QueryBuilders.matchPhrasePrefixQuery();

        //词项
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", "java");
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("title", "java", "python");
        QueryBuilders.rangeQuery("price").from(50).to(100).includeLower(true).includeUpper(false);
        ExistsQueryBuilder language = QueryBuilders.existsQuery("language");
        //ids query
        IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery("3");
    }

    @Test
    public void aggreationMax(){
        MaxAggregationBuilder field = AggregationBuilders.max("agg").field("price");
        SearchResponse books = client.prepareSearch("books").addAggregation(field).get();
        Max agg = books.getAggregations().get("agg");
        double value = agg.getValue();
        System.out.println(value);
    }

    @Test
    public void aggreationBucket(){
        TermsAggregationBuilder field = AggregationBuilders.terms("per_count").field("language");
        SearchResponse books = client.prepareSearch("books").addAggregation(field).execute().actionGet();
        Terms perCount = books.getAggregations().get("per_count");
        for (Terms.Bucket bucket : perCount.getBuckets()) {
            System.out.println(bucket.getKey()+":"+bucket.getDocCount());
        }
    }

    @Test
    public void filter(){
        FilterAggregationBuilder filter = AggregationBuilders.filter("agg", QueryBuilders.termQuery("title", "java"));
        SearchResponse books = client.prepareSearch("books").addAggregation(filter).execute().actionGet();
        Filter agg = books.getAggregations().get("agg");
        System.out.println(agg.getDocCount());
    }
}

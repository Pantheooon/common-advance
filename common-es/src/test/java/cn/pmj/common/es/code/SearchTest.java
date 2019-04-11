package cn.pmj.common.es.code;

import cn.pmj.common.es.EsBaseTest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
}

package cn.pmj.common.es;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author: Pantheon
 * @date: 2019/4/3 14:45
 * https://blog.csdn.net/ydwyyy/article/details/79487995
 */
public class AggreationTest extends EsBaseTest {


    /**
     * GET /cars/transactions/_search
     * {
     * "size" : 0,//不需要返回文档,所以直接设置为0.可以提高查询速度
     * "aggs" : { //这个是aggregations的缩写,这边用户随意,可以写全称也可以缩写
     * "popular_colors" : { //定义一个聚合的名字,与java的方法命名类似,建议用'_'线来分隔单词
     * "terms" : { //定义单个桶(集合)的类型为 terms
     * "field" : "color"(字段颜色进行分类,类似于sql中的group by color)
     * }
     * }
     * }
     * }
     */
    @Test
    public void test1() {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("cars").
                setTypes("transactions").
                addAggregation(AggregationBuilders
                        .terms("popular_colors")
                        .field("color"));
        SearchResponse response = searchRequestBuilder.get();
//                .setSize(5)

        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        //获取命中的文档
        for (SearchHit hit : searchHits) {
            Map<String, Object> source = hit.getSourceAsMap();
            Object color = source.get("color");
            System.out.println(color);
        }
        //获取聚合结果
        Terms terms = response.getAggregations().get("popular_colors");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println(bucket.getKey());
            System.out.println(bucket.getDocCount());
        }
    }

    /**
     * GET /cars/transactions/_search
     * {
     * "size" : 0,
     * "aggs": {
     * "colors": {
     * "terms": {
     * "field": "color"
     * },
     * "aggs": { //为指标新增aggs层
     * "avg_price": { //指定指标的名字,在返回的结果中也是用这个变量名来储存数值的
     * "avg": {//指标参数:平均值
     * "field": "price" //明确求平均值的字段为'price'
     * }
     * }
     * }
     * }
     * }
     * }
     */
    @Test
    public void test2() {
        SearchResponse searchResponse = client.prepareSearch("cars")
                .setTypes("transactions")
                .addAggregation(AggregationBuilders.terms("colors").field("color")
                        .subAggregation(AggregationBuilders.avg("avg_price").field("price"))).get();
        Aggregations aggregations = searchResponse.getAggregations();
        System.out.println(aggregations);
    }




    /*----------------buckets-------------------------*/

    /**
     * {
     * "size" : 0,
     * "aggs": {
     * "colors": {
     * "terms": {
     * "field": "color"
     * },
     * "aggs": {
     * "avg_price": {
     * "avg": {
     * "field": "price"
     * }
     * },
     * "make": { //命名子集合的名字
     * "terms": {
     * "field": "make" //按照字段'make'再次进行分类
     * }
     * }
     * }
     * }
     * }
     * }
     */
    @Test
    public void test3() {
        SearchResponse searchResponse = client.prepareSearch("cars")
                .setTypes("transactions")
                .addAggregation(AggregationBuilders.terms("colors").field("color")
                        .subAggregation(AggregationBuilders.avg("prices").field("price"))
                        .subAggregation(AggregationBuilders.terms("makes").field("make"))).get();
        Terms colors = searchResponse.getAggregations().get("colors");
        List<? extends Terms.Bucket> buckets = colors.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            Object key = bucket.getKey();
            Aggregations aggregations = bucket.getAggregations();
            InternalAvg aggregation = aggregations.get("prices");
            System.out.println(key + ":" + aggregation.getValue());
            Terms makes = aggregations.get("makes");
            List<? extends Terms.Bucket> buckets1 = makes.getBuckets();
            for (Terms.Bucket bucket1 : buckets1) {
                System.out.println(bucket1.getKey() + ":" + bucket1.getDocCount());
            }
            System.out.println("----------------------------------------");
        }
    }

    /**
     * {
     * "size" : 0,
     * "aggs": {
     * "colors": {
     * "terms": {
     * "field": "color"
     * },
     * "aggs": {
     * "avg_price": { "avg": { "field": "price" }
     * },
     * "make" : {
     * "terms" : {
     * "field" : "make"
     * },
     * "aggs" : {
     * "min_price" : { //自定义变量名字
     * "min": { //参数-最小值
     * "field": "price"
     * }
     * },
     * "max_price" : {
     * "max": { //参数-最大值
     * "field": "price"
     * }
     * }
     * }
     * }
     * }
     * }
     * }
     * }
     */
    @Test
    public void test4() {
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("colors").field("color")
                .subAggregation(AggregationBuilders.avg("avg_price").field("price"))
                .subAggregation(AggregationBuilders.terms("makes").field("make")
                        .subAggregation(AggregationBuilders.min("min_price").field("price"))
                        .subAggregation(AggregationBuilders.max("max_price").field("price")));
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("cars")
                .setTypes("transactions")
                .addAggregation(termsAggregationBuilder);
        SearchResponse searchResponse = searchRequestBuilder.get();
        Aggregation colors = searchResponse.getAggregations().get("colors");
    }

}

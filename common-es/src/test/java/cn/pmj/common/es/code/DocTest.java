package cn.pmj.common.es.code;

import cn.pmj.common.es.EsBaseTest;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.junit.Test;
import sun.plugin.ClassLoaderInfo;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class DocTest extends EsBaseTest {

    @Test
    public void updateDoc() {
        Map<String, Object> source = new HashMap<>();
        source.put("title", "python从入门到跑路");
        UpdateRequest request = new UpdateRequest("books", "IT", "4");
        request.doc(source);
        ActionFuture<UpdateResponse> update = client.update(request);
        System.out.println(update.actionGet().status().getStatus());
    }

    @Test
    public void upsert() {
        IndexRequest indexRequest = new IndexRequest("books", "IT", "9");
        Map<String, Object> indexMap = new HashMap<>();
        indexMap.put("title", "python沙雕");
        indexRequest.source(indexMap);

        UpdateRequest updateRequest = new UpdateRequest("books", "IT", "9");
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("title", "java沙雕");
        updateRequest.doc(updateMap).upsert(indexRequest);
        System.out.println(client.update(updateRequest).actionGet().status().getStatus());
    }

    @Test
    public void deleteByQuery() {
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                //查询的參數
                .filter(QueryBuilders.matchQuery("title", "java"))
                //索引名称
                .source("books").get();
//        刪除个数
        System.out.println(response.getDeleted());
    }


    //    批量获取
    @Test
    public void batchGet() {
        MultiGetRequestBuilder add = client.prepareMultiGet().add("books", "IT", "1", "2", "3")
                .add("cars", "transactions", "Homz42kBHZoQo7AUW8WK");
        MultiGetResponse multiGetItemResponses = add.get();
        for (MultiGetItemResponse multiGetItemRespons : multiGetItemResponses) {
            GetResponse response = multiGetItemRespons.getResponse();
            if (response.isExists()) {
                System.out.println(response.getSourceAsString());
            }
        }
    }

    @Test
    public void bulkRequest() throws IOException {
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        IndexRequestBuilder indexRequest = client.prepareIndex("twitter", "tweet", "5").
                setSource(jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "anther post").endObject());

        DeleteRequestBuilder deleteRequestBuilder = client.prepareDelete("twitter", "tweet", "2");


        UpdateRequestBuilder updateRequest = client.prepareUpdate("twitter", "tweet", "2")
                .setDoc(jsonBuilder().startObject().field("message", "update request").endObject());

        bulkRequestBuilder.add(indexRequest).add(deleteRequestBuilder).add(updateRequest).execute().actionGet();
    }
}

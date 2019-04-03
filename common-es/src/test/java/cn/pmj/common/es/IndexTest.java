package cn.pmj.common.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class IndexTest {


    private TransportClient client;


    @Before
    public void before() {
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        client = new PreBuiltTransportClient(settings);
        InetSocketAddress address = new InetSocketAddress("localhost", 9300);
        client.addTransportAddress(new TransportAddress(address));
    }

    @org.junit.Test
    public void createIndex() throws IOException {
        CreateIndexRequestBuilder megacorp = client.admin().indices().prepareCreate("megacorp");
        XContentBuilder builder = jsonBuilder()
                .startObject()
                .field("user", "kimchy")
                .field("postDate", new Date())
                .field("message", "trying out Elasticsearch")
                .array("interest", "haha", "lol", "lol")
                .endObject();
        String json = Strings.toString(builder);
        System.out.println(json);
//        CreateIndexResponse createIndexResponse = megacorp.execute().actionGet();
//        System.out.println(createIndexResponse.isAcknowledged());
    }



    @Test
    public void createMapping() throws IOException {
        IndexRequestBuilder indexRequestBuilder = client.prepareIndex("megacorp", "employee")
                .setSource(jsonBuilder().startObject().
                        field("first_name","keyword").
                        field("last_name","keywprd").
                        field("age","int").
                        field("about","text")
                        .field("interests","array").endObject());
        IndexResponse indexResponse = indexRequestBuilder.get();
        System.out.println(indexResponse.status());
    }


    @Test
    public void getIndex(){
        GetRequestBuilder getRequestBuilder = client.prepareGet("megacorp", "employee", "1");
        GetResponse documentFields = getRequestBuilder.get();
        Map<String, DocumentField> fields = documentFields.getFields();
        System.out.println(fields);
    }




    @org.junit.Test
    public void deleteIndex() {
        String s ="blog,blog3,book,books,patheon,range_test_index,weather";
        String[] split = s.split(",");
        for (String s1 : split) {
            DeleteIndexRequestBuilder megacorp = client.admin().indices().prepareDelete(s1);
        }

    }
}

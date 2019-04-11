package cn.pmj.common.es.code;

import cn.pmj.common.es.EsBaseTest;
import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class CodeTest extends EsBaseTest {

    @Test
    public void selectOne() {
        GetResponse documentFields = client.prepareGet("books", "IT", "1").get();
        Map<String, Object> source = documentFields.getSource();
        System.out.println(source.toString());
    }


    @Test
    public void indexExist() {
        IndicesAdminClient indices = client.admin().indices();
        IndicesExistsResponse it = indices.prepareExists("books").get();
        System.out.println(it.isExists());
    }

    @Test
    public void typeExist() {
        TypesExistsRequestBuilder types = client.admin().indices().prepareTypesExists("books").setTypes("IT");
        TypesExistsResponse typesExistsResponse = types.get();
        System.out.println(typesExistsResponse.isExists());
    }

    @Test
    public void createIndex() {
        IndicesAdminClient indices = client.admin().indices();
        CreateIndexRequestBuilder pmj = indices.prepareCreate("pmj");
        System.out.println(pmj.get().isAcknowledged());
    }

    @Test
    public void createSettingIndex() {
        IndicesAdminClient indices = client.admin().indices();
        CreateIndexResponse pmj2 = indices.prepareCreate("pmj2").
                setSettings(Settings.builder().put("index.number_of_shards", 3).
                        put("index.number_of_replicas", 2)).get();
        System.out.println(pmj2.isAcknowledged());
    }

    @Test
    public void updateSettingIndex() {
        IndicesAdminClient indices = client.admin().indices();
        AcknowledgedResponse pmj2 = indices.prepareUpdateSettings("pmj2")
                .setSettings(Settings.builder().put("index.number_of_replicas", 4)).get();
        System.out.println(pmj2.isAcknowledged());
    }

    @Test
    public void getSettings() {
        IndicesAdminClient indices = client.admin().indices();
        GetSettingsResponse pmj2 = indices.prepareGetSettings("pmj2").get();
        ImmutableOpenMap<String, Settings> indexToSettings = pmj2.getIndexToSettings();
        for (ObjectObjectCursor<String, Settings> indexToSetting : indexToSettings) {
            System.out.println(indexToSetting.key);
            System.out.println(indexToSetting.value);
        }
    }

    @Test
    public void setMappings(){}{
        IndicesAdminClient indices = client.admin().indices();
        indices.prepareCreate("pmj2").addMapping("local").get();
    }


}

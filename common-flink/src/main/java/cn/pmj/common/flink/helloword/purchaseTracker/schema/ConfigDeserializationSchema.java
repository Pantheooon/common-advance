package cn.pmj.common.flink.helloword.purchaseTracker.schema;

import cn.pmj.common.flink.helloword.purchaseTracker.model.Config;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema;

import java.io.IOException;

public class ConfigDeserializationSchema implements KeyedDeserializationSchema<Config> {
    @Override
    public Config deserialize(byte[] messageKey, byte[] message, String topic, int partition, long offset) throws IOException {
        return JSON.parseObject(new String(message), new TypeReference<Config>() {});
    }

    @Override
    public boolean isEndOfStream(Config nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Config> getProducedType() {
        return TypeInformation.of(new TypeHint<Config>() {});
    }
}
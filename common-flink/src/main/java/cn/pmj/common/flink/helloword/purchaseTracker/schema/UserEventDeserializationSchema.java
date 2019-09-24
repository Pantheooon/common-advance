package cn.pmj.common.flink.helloword.purchaseTracker.schema;

import cn.pmj.common.flink.helloword.purchaseTracker.model.UserEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema;

import java.io.IOException;

public class UserEventDeserializationSchema implements KeyedDeserializationSchema<UserEvent> {
    @Override
    public UserEvent deserialize(byte[] messageKey, byte[] message, String topic, int partition, long offset) throws IOException {
        return JSON.parseObject(new String(message), new TypeReference<UserEvent>() {
        });
    }

    @Override
    public boolean isEndOfStream(UserEvent nextElement) {
        return false;
    }

    @Override
    public TypeInformation<UserEvent> getProducedType() {
        return TypeInformation.of(new TypeHint<UserEvent>() {
        });
    }
}

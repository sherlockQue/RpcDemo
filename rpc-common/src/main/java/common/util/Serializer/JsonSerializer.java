package common.util.Serializer;

import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.json.JsonObjectDecoder;
import java.io.IOException;

/**
 * @author fsq
 */
public class JsonSerializer implements Serializer{


  public byte[] serialize(Object o) throws IOException {

    return JSON.toJSONBytes(o);
  }

  public <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException {
    return JSON.parseObject(bytes,clazz);
  }
}

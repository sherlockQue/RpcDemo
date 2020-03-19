package common.util.Serializer;

import java.io.IOException;

public interface Serializer {

  /**
   * 序列化，java对象转二进制
   * @param o
   * @return
   * @throws IOException
   */
  byte[] serialize(Object o) throws IOException;

  /**
   * 二进制转java
   * @param clazz
   * @param bytes
   * @param <T>
   * @return
   * @throws IOException
   */
  <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException;

}

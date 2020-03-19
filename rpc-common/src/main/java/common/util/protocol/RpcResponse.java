package common.util.protocol;

public class RpcResponse {
  /**
   * 响应ID
   */
  private String requestId;

  /**
   * 返回的结果
   */
  private Object result;

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }
}
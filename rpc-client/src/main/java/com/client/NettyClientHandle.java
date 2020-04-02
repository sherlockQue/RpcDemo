package com.client;


import common.util.protocol.RpcResponse;
import common.util.sharedata.ShareData;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * @author fsq
 */
public class NettyClientHandle extends ChannelDuplexHandler {

  private ShareData shareData;

  public NettyClientHandle(ShareData shareData) {
    this.shareData = shareData;
  }

  @Override
  public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    if (o instanceof RpcResponse) {

      shareData.addRpcResponse(((RpcResponse) o).getRequestId(),(RpcResponse) o);

    }
  }
  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

    super.write(ctx, msg, promise);


  }

}

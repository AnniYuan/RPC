package handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import future.ResultFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import model.Response;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter implements ChannelHandler {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg.toString().equals("ping")){
			System.out.println("Received ping,Send pong to server");
			ctx.channel().writeAndFlush("pong\r\n");
		}
		
		//Set response
		Response response = JSONObject.parseObject(msg.toString(), Response.class);
		ResultFuture.receive(response);//find corresponding request using response ID, and set response
	}
	
}

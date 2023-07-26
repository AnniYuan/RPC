package Yuan.rpc.cousumer.handler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import Yuan.rpc.cousumer.core.ResultFuture;
import Yuan.rpc.cousumer.param.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter implements ChannelHandler {
	private static final Executor exec = Executors.newFixedThreadPool(10);
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		final Object m = msg;
		if(msg.toString().equals("ping")){
			System.out.println("received ping, send pong to server side");
			ctx.channel().writeAndFlush("pong\r\n");
		}
		
		//Set response
//		final Response response = JSONObject.parseObject(msg.toString(), Response.class);
//		System.out.println("SimpleClientHandler中的Response:"+JSONObject.toJSONString(response));
		exec.execute(new Runnable() {
			
			public void run() {
				Response response = JSONObject.parseObject(m.toString(), Response.class);
				System.out.println("SimpleClientHandler中的Response:"+JSONObject.toJSONString(response));
				ResultFuture.receive(response);				
			}
		});
//		ResultFuture.receive(response);//find corresponding request using response ID, and set response
	}
	
}

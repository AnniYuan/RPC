
package Yuan.rpc.cousumer.core;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

import com.alibaba.fastjson.JSONObject;

import Yuan.rpc.cousumer.constans.Constans;
import Yuan.rpc.cousumer.handler.SimpleClientHandler;
import Yuan.rpc.cousumer.param.ClientRequest;
import Yuan.rpc.cousumer.param.Response;
import Yuan.rpc.cousumer.zk.ServerWatcher;
import Yuan.rpc.cousumer.zk.ZooKeeperFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {
//	public static Set<String> realServerPath = new HashSet<String>();//remove duplicate and remove index
	public static final Bootstrap b = new Bootstrap();


	private static ChannelFuture f = null;
	
	static{
		String host = "localhost";
		int port = 8080;
		
		EventLoopGroup work = new NioEventLoopGroup();
		try {
		b.group(work)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							
							ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
							ch.pipeline().addLast(new StringDecoder());//decoder
							ch.pipeline().addLast(new StringEncoder());//encoder
							ch.pipeline().addLast(new SimpleClientHandler());//handles service
						}
			});
				
				CuratorFramework client = ZooKeeperFactory.getClient();
			
				List<String> serverPath = client.getChildren().forPath(Constans.SERVER_PATH);
				//客户端加上ZK监听服务器的变化
				CuratorWatcher watcher = new ServerWatcher();
				client.getChildren().usingWatcher(watcher ).forPath(Constans.SERVER_PATH);
				
				for(String path :serverPath){
					String[] str = path.split("#");
					ChannelManager.realServerPath.add(str[0]+"#"+str[1]);
					ChannelFuture channnelFuture = NettyClient.b.connect(str[0], Integer.valueOf(str[1]));
					ChannelManager.addChnannel(channnelFuture);
				}
				if(ChannelManager.realServerPath.size()>0){
					String[] netMessageArray = ChannelManager.realServerPath.toArray()[0].toString().split("#");
					host = netMessageArray[0];
					port = Integer.valueOf(netMessageArray[1]);
				}
			
//			f = b.connect(host, port).sync();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static Response send(ClientRequest request){
		f=ChannelManager.get(ChannelManager.position);
		f.channel().writeAndFlush(JSONObject.toJSONString(request)+"\r\n");
//		f.channel().writeAndFlush("\r\n");
		Long timeOut = 60l;
		ResultFuture future = new ResultFuture(request);
//		return future.get(timeOut);
		return future.get(timeOut);

	}
	
}

package Yuan.rpc.cousumer.Yuan.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import Yuan.rpc.cousumer.annotation.RemoteInvoke;
import Yuan.rpc.cousumer.param.Response;
import Yuan.rpc.user.TestRemote;
import Yuan.rpc.user.User;
import Yuan.rpc.user.UserRemote;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RemoteInvokeTest.class)
@ComponentScan("\\")
public class RemoteInvokeTest {
	public static List<User> list = new ArrayList<User>();
	@RemoteInvoke
	public static TestRemote userremote;
	public static User user;
	public static Long count = 0l;
	
	static{
		user = new User();
		user.setId(1000);
		user.setName("Bob");
	}
	@Test
	public void testSaveUser(){
		User user = new User();
		user.setId(1000);
		user.setName("Bob");
		userremote.testUser(user);
//		Long start = System.currentTimeMillis();
//		for(int i=1;i<10000;i++){
//			userremote.saveUser(user);
//		}
//		Long end = System.currentTimeMillis();
//		Long count = end-start;
//		System.out.println("Total time taken:"+count/1000+"秒");
		
	}			


}

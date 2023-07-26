# Introduction 
**After studying Netty, I decided to write a lightweight RPC framework based on Netty, Zookeeper, and Spring. If there are any suggestions, please send them to the email hbayuan@gmail.com**


# Features
- **Supports persistent connections**
- **Supports asynchronous calls**
- **Supports heartbeat detection**
- **Supports JSON serialization**
- **Near-zero configuration, calls implemented based on annotations**
- **Service registry center implemented based on Zookeeper**
- **Supports dynamic management of client connections**
- **Supports client-side service monitoring and discovery features**
- **Supports server-side service registration features**
- **Implemented based on Netty4.X**

# Quick Start
### Server-side Development
- **Add your own Service under the server's Service, and add the @Service annotation**
	<pre>
	@Service
	public class TestService {
		public void test(User user){
			System.out.println("调用了TestService.test");
		}
	}
	</pre>

- **Create an interface for a service and create a class that implements this interface.**
	###### Interface
	<pre>
	public interface TestRemote {
		public Response testUser(User user);  
	}
	</pre>
	###### Create an class which implements the interface, add the @Remote annotation to your class. This class is where you truly invoke the service. You can generate any form of Response you want to return to the client.
	<pre> 
	@Remote
	public class TestRemoteImpl implements TestRemote{
		@Resource
		private TestService service;
		public Response testUser(User user){
			service.test(user);
			Response response = ResponseUtil.createSuccessResponse(user);
			return response;
		}
	}	
	</pre>


### Client-side development
- **Create an interface on the client side, this interface is the one you want to call**
	<pre>
	public interface TestRemote {
		public Response testUser(User user);
	}
	</pre>

### usage
- **Create an attribute of the interface type where you want to make the call and add the @RemoteInvoke annotation to this attribute.**
	<pre>
	@RunWith(SpringJUnit4ClassRunner.class)
	@ContextConfiguration(classes=RemoteInvokeTest.class)
	@ComponentScan("\\")
	public class RemoteInvokeTest {
		@RemoteInvoke
		public static TestRemote userRemote;
		public static User user;
		@Test
		public void testSaveUser(){
			User user = new User();
			user.setId(1000);
			user.setName("张三");
			userRemote.testUser(user);
		}
	}	
	</pre>

### Results
![results.png](results.png)



# Overview

![Markdown](https://s1.ax1x.com/2018/07/06/PZK3SP.png)

package Yuan.rpc.cousumer.core;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Yuan.rpc.cousumer.param.ClientRequest;
import Yuan.rpc.cousumer.param.Response;



public class ResultFuture {
	public final static ConcurrentHashMap<Long,ResultFuture> map = new ConcurrentHashMap<Long,ResultFuture>();
	final  Lock lock = new ReentrantLock();//更改
	private Condition condition = lock.newCondition();
	private Response response;
	private Long timeOut = 2*60*1000l;
	private Long start = System.currentTimeMillis();
	
	
	public ResultFuture(ClientRequest request){
		map.put(request.getId(), this);
	}
	
	public Response get(){
//		System.out.println("get lock info:"+lock);
		lock.lock();
		
		try {

			while(!done()){
				condition.await();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
			System.out.println(Thread.currentThread().getName() + "   release get lock！");
		}
		
		return this.response;
	}
	
	public Response get(Long time){
		lock.lock();
//		System.out.println("get lock info:"+lock);
		try {
			while(!done()){
				condition.await(time,TimeUnit.MILLISECONDS);
				if((System.currentTimeMillis()-start)>time){
//					System.out.println("Future request out of time        "
//							+ "                                                                                                                                                                                                               ");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
//			System.out.println(Thread.currentThread().getName() + "   release get lock！");
		}
		
		return this.response;
		
	}
	
	public static void receive(Response response){
		if(response != null){
			ResultFuture future = map.get(response.getId());
			if(future != null){
				Lock lock = future.lock;
//				System.out.println("Receive lock info:"+lock);
				lock.lock();
				try {
					future.setResponse(response);
					future.condition.signal();
					map.remove(future);
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					lock.unlock();
//					System.out.println(Thread.currentThread().getName() + "   release receive lock！");
				}
			}

		}
		
	} 

	private boolean done() {
		if(this.response != null){
			return true;
		}
		return false;
	}

	public Long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Long timeOut) {
		this.timeOut = timeOut;
	}

	public Long getStart() {
		return start;
	}


	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
	
	//清理线程
	static class ClearFutureThread extends Thread{
		@Override
		public void run() {
			Set<Long> ids = map.keySet();
			for(Long id : ids){
				ResultFuture f = map.get(id);
				if(f==null){
					map.remove(f);
				}else if(f.getTimeOut()<(System.currentTimeMillis()-f.getStart()))
				{//链路超时
					Response res = new Response();
					res.setId(id);
					res.setCode("33333");
					res.setMsg("链路超时");
					receive(res);
				}
			}
		}
	}
	
	static{
		ClearFutureThread clearThread = new ClearFutureThread();
		clearThread.setDaemon(true);
		clearThread.start();
	}
	
	
	
}

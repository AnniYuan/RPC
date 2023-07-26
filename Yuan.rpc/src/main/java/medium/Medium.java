package medium;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;

import model.Response;
import model.ServerRequest;

public class Medium {
	public static final HashMap<String, BeanMethod> mediamap = new HashMap<String,BeanMethod>();
	private static Medium media = null;
	
	
	private Medium(){}
	
	public static Medium newInstance(){
		if(media == null){
			media = new Medium();
		}
		
		return media;
	}
	
	public Response process(ServerRequest request){
		Response result = null;
		try {
			String command = request.getCommand();//command is key
			BeanMethod beanMethod = mediamap.get(command);
			if(beanMethod == null){
				return null;
			}
			
			Object bean = beanMethod.getBean();
			Method method = beanMethod.getMethod();
			Class type = method.getParameterTypes()[0];
			Object content = request.getContent();
			Object args = JSONObject.parseObject(JSONObject.toJSONString(content), type);
			
			result = (Response) method.invoke(bean, args);
			result.setId(request.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
}

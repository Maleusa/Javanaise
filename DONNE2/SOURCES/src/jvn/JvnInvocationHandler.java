package jvn;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JvnInvocationHandler implements InvocationHandler,Serializable {
	private static final long serialVersionUID = 1L;
	JvnObject object;

	public JvnInvocationHandler(JvnObject obj) {
		this.object = obj;
	}

	public JvnInvocationHandler(Serializable obj, String name) throws Exception {
JvnServerImpl server = JvnServerImpl.jvnGetServer();
		
		this.object = server.jvnLookupObject(name);
		
		// look up the IRC object in the JVN server
		// if not found, create it, and register it in the JVN server
		if(object==null){
			this.object = server.jvnCreateObject(obj);	
			if(object == null){
				throw new JvnException("Cannot create object. Server is full");
			}
			
			this.object.jvnUnLock();
			server.jvnRegisterObject(name, object);
		}
	}

	public static Object newInstance(Serializable obj, String name) throws Exception {
		return java.lang.reflect.Proxy.newProxyInstance(
				obj.getClass().getClassLoader(),
				obj.getClass().getInterfaces(),
				new JvnInvocationHandler(obj, name));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable, IllegalArgumentException {
		Object result;
		if (method.isAnnotationPresent(Operation.class)) {
			switch (method.getAnnotation(Operation.class).name()) {
				case "read":
					object.jvnLockRead();
					break;
				case "write":
					object.jvnLockWrite();
					break;
				default:
					throw new IllegalArgumentException("Only read and write are valid Operation");
			}
			result = method.invoke(object.jvnGetSharedObject(), args);
			object.jvnUnLock();
		} else {
			result = method.invoke(object.jvnGetSharedObject(), args);
		}

		return result;
	}

}

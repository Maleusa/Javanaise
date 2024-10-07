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

	public static Object newInstance(JvnObject obj) throws JvnException {
		Object shared = obj.jvnGetSharedObject();
		return Proxy.newProxyInstance(
				shared.getClass().getClassLoader(),
				shared.getClass().getInterfaces(),
				new JvnInvocationHandler(obj));
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

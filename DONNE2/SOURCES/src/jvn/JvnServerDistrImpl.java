package jvn;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class JvnServerDistrImpl extends UnicastRemoteObject implements JvnRemoteDistrServer {
	private static final long serialVersionUID = 1L;
	private static JvnServerDistrImpl js = null; // A JVN server is managed as a singleton
    private final JvnNode jvnCoordinator;
    private final HashMap<String, JvnObject> cachedObjects;
	public JvnServerDistrImpl(JvnNode Node) throws Exception {
		super();
		jvnCoordinator=Node;
		js=this;
		this.cachedObjects=new HashMap<String,JvnObject>();
	}

	@Override
	public void jvnInvalidateReader(String joi) throws RemoteException, JvnException {
		this.cachedObjects.get(joi).jvnInvalidateReader();
		
	}

	@Override
	public Serializable jvnInvalidateWriter(String joi) throws RemoteException, JvnException {
		return this.cachedObjects.get(joi).jvnInvalidateWriter();
	}

	@Override
	public Serializable jvnInvalidateWriterForReader(String joi) throws RemoteException, JvnException {
		return this.cachedObjects.get(joi).jvnInvalidateWriterForReader();
	}

	@Override
	public JvnObject jvnCreateObject(Serializable jos) throws JvnException, RemoteException {
		String id = this.jvnCoordinator.jvnGetObjectId();
        JvnObject jvnObject = new JvnObjectDistr(jos, this, id);
        this.cachedObjects.put(id, jvnObject);
        return jvnObject;
	}

	@Override
	public void jvnRegisterObject(String jon, JvnObject jo) throws JvnException {
		try {
            this.jvnCoordinator.jvnRegisterObject(jon, jo, js);
        } catch (RemoteException | JvnException e) {
            System.out.println("Could not register object " + jon);
            e.printStackTrace();
        }
		
	}

	@Override
	public JvnObject jvnLookupObject(String jon) throws JvnException {
		JvnObjectImpl object;

        try {
            object = (JvnObjectImpl) this.jvnCoordinator.jvnLookupObject(jon, this);
        } catch (RemoteException | JvnException e) {
            return null;
        }

        if (object != null) {
            object.setLocalDistr(this);
            this.cachedObjects.put(this.jvnCoordinator.getServerId()+";"+object.jvnGetObjectId(), object);
        }

        return object;
	}

	@Override
	public Serializable jvnLockRead(String joi) throws JvnException {
		Serializable obj = this.cachedObjects.get(joi).jvnGetSharedObject();

        try {
            obj = this.jvnCoordinator.jvnLockRead(joi, this);
        } catch (RemoteException | JvnException e) {
            e.printStackTrace();
        }

        return obj;
	}

	@Override
	public Serializable jvnLockWrite(String joi) throws JvnException {
		Serializable obj = this.cachedObjects.get(joi).jvnGetSharedObject();

        try {
            obj = this.jvnCoordinator.jvnLockWrite(joi, this);
        } catch (RemoteException | JvnException e) {
            e.printStackTrace();
        }

        return obj;
	}

	@Override
	public void jvnTerminate() throws JvnException {
		// TODO Auto-generated method stub
		
	}
}

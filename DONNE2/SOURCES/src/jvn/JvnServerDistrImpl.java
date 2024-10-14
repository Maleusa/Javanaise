package jvn;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class JvnServerDistrImpl extends UnicastRemoteObject implements JvnLocalServer,JvnRemoteServer {

	public JvnServerDistrImpl(JvnNode Node) throws Exception {
		super();
	}

	@Override
	public void jvnInvalidateReader(int joi) throws RemoteException, JvnException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Serializable jvnInvalidateWriter(int joi) throws RemoteException, JvnException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable jvnInvalidateWriterForReader(int joi) throws RemoteException, JvnException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JvnObject jvnCreateObject(Serializable jos) throws JvnException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void jvnRegisterObject(String jon, JvnObject jo) throws JvnException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JvnObject jvnLookupObject(String jon) throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable jvnLockRead(int joi) throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable jvnLockWrite(int joi) throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void jvnTerminate() throws JvnException {
		// TODO Auto-generated method stub
		
	}
}

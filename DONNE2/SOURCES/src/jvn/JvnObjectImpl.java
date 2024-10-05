/***
 * JAVANAISE API
 * Contact:
 *
 * Authors:
 */

package jvn;

import java.io.*;
import java.rmi.RemoteException;

/**
 * Interface of a JVN object. A JVN object is used to acquire read/write locks
 * to access a given shared object
 */

public class JvnObjectImpl implements JvnObject {
	private static final long serialVersionUID = 1L;
	private LockStateEnum lockState;
	private transient JvnServerImpl localServer;
	private int id;
	private Serializable object;
	
	public JvnObjectImpl(Serializable object, JvnServerImpl jvnServerImpl,int id) {
		this.setObject(object);
		this.setLocalServer(jvnServerImpl);
		this.lockState=LockStateEnum.NOLOCK;
		this.id=id;
	}

	/**
	 * Get a Read lock on the shared object
	 *
	 * @throws JvnException
	 **/
	public void jvnLockRead() throws jvn.JvnException {
		switch(this.lockState) {
		case NOLOCK:
			this.object=this.localServer.jvnLockRead(id);
			this.lockState=LockStateEnum.READLOCK;
			break;
		case WRITELOCK:
			this.lockState=LockStateEnum.READWRITECACHED;
			break;
		case READLOCK:
			this.lockState=LockStateEnum.READLOCK;
			break;
		default:
			break;
		}
	}

	/**
	 * Get a Write lock on the object
	 * @return 
	 *
	 * @throws JvnException
	 **/
	public void jvnLockWrite() throws jvn.JvnException {
		switch(this.lockState) {
		default:
			this.object=this.localServer.jvnLockWrite(this.id);
			this.lockState=LockStateEnum.WRITELOCK;
		}
	}

	/**
	 * Unlock the object
	 *
	 * @throws JvnException
	 **/
	public void jvnUnLock() throws jvn.JvnException {
		switch(this.lockState) {
		case READLOCK:
			this.lockState=LockStateEnum.READLOCKCACHED;
			break;
		case WRITELOCK:
			this.lockState=LockStateEnum.WRITELOCKCACHED;
			break;
		default:
			break;
		}
		this.notifyAll();
	}

	/**
	 * Get the object identification
	 *
	 * @throws JvnException
	 **/
	public int jvnGetObjectId() throws jvn.JvnException {
		return this.id;
	}

	/**
	 * Get the shared object associated to this JvnObject
	 *
	 * @throws JvnException
	 **/
	public Serializable jvnGetSharedObject() throws jvn.JvnException {
		return this;
	}

	/**
	 * Invalidate the Read lock of the JVN object
	 *
	 * @throws JvnException
	 **/
	public void jvnInvalidateReader() throws jvn.JvnException {
		try {
			this.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.lockState=LockStateEnum.NOLOCK;
	}

	/**
	 * Invalidate the Write lock of the JVN object
	 *
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnInvalidateWriter() throws jvn.JvnException {
		try {
			this.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.lockState = LockStateEnum.NOLOCK;
		return this.object;
	}

	/**
	 * Reduce the Write lock of the JVN object
	 *
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader() throws jvn.JvnException {
		try {
			this.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.lockState = LockStateEnum.READLOCKCACHED;
		return this.object;
	}

	public JvnServerImpl getLocalServer() {
		return localServer;
	}

	public void setLocalServer(JvnServerImpl localServer) {
		this.localServer = localServer;
	}

	public Serializable getObject() {
		return object;
	}

	public void setObject(Serializable object) {
		this.object = object;
	}
}

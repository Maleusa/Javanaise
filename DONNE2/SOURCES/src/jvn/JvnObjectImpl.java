/***
 * JAVANAISE API
 *
 * Authors: Florent Pouzol, Hugo Triolet, Yazid Cheriti
 */

package jvn;

import java.io.*;
import java.rmi.RemoteException;

/**
 * Interface of a JVN object. A JVN object is used to acquire read/write locks
 * to access a given shared object
 */

public class JvnObjectImpl implements JvnObject {
	@Serial
	private static final long serialVersionUID = 1L;
	private LockStateEnum lockState;
	private transient JvnServerImpl localServer;
	private int id;
	private Serializable object;
	
	public JvnObjectImpl(Serializable object, JvnServerImpl jvnServerImpl, int id) {
		this.setObject(object);
		this.setLocalServer(jvnServerImpl);
		this.lockState = LockStateEnum.NOLOCK;
		this.id = id;
	}

	/**
	 * Get a Read lock on the shared object
	 *
	 * @throws JvnException
	 **/
	public synchronized void jvnLockRead() throws jvn.JvnException {
		switch(this.lockState) {
		case NOLOCK:
			this.object=this.localServer.jvnLockRead(id);
			this.lockState=LockStateEnum.READLOCK;
			break;
		case WRITELOCK:
			this.lockState=LockStateEnum.READLOCK;
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
	public synchronized void jvnLockWrite() throws jvn.JvnException {
		switch(this.lockState) {
		case WRITELOCKCACHED:
			this.lockState=LockStateEnum.WRITELOCK;
			break;
		case WRITELOCK:
			break;
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
	public synchronized void jvnUnLock() throws jvn.JvnException {
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
		this.notifyAll(); // thread owner issue to fix
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
		return this.object;
	}

	/**
	 * Invalidate the Read lock of the JVN object
	 *
	 * @throws JvnException
	 **/
	public synchronized void jvnInvalidateReader() throws jvn.JvnException {
		while(this.lockState!=LockStateEnum.READLOCKCACHED) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.lockState=LockStateEnum.NOLOCK;
	}

	/**
	 * Invalidate the Write lock of the JVN object
	 *
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public synchronized Serializable jvnInvalidateWriter() throws jvn.JvnException {
		while(this.lockState!=LockStateEnum.WRITELOCKCACHED) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	public synchronized Serializable jvnInvalidateWriterForReader() throws jvn.JvnException {
		switch (this.lockState) {
        case WRITELOCK:
            while (this.lockState == LockStateEnum.WRITELOCK) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
              
                    ex.printStackTrace();
                    Thread.currentThread().interrupt();

                }
            }
            this.lockState = LockStateEnum.READLOCKCACHED;
            break;
        case READWRITECACHED:
        case WRITELOCKCACHED:
            this.lockState=LockStateEnum.READLOCKCACHED;
            break;
        default:
            
    }
    return this.object;
	}

	public JvnServerImpl getLocalServer() {
		return localServer;
	}

	public void setLocalServer(JvnServerImpl localServer) {
		this.localServer = localServer;
	}

	public Serializable getObject() {
		return this.object;
	}

	public void setObject(Serializable object) {
		this.object = object;
	}
}

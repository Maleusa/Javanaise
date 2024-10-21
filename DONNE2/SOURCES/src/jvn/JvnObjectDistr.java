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

public class JvnObjectDistr implements JvnObject {
	@Serial
	private static final long serialVersionUID = 1L;
	private LockStateEnum lockState;
	private transient JvnServerImpl localServer;
	private transient JvnNode localNode;
	private transient JvnServerDistrImpl localDistr;
	private String id;
	private Serializable object;
	
	public JvnObjectDistr(Serializable object, JvnServerDistrImpl jvnServerDistrImpl, String id) {
		this.setObject(object);
		this.setLocalDistr(jvnServerDistrImpl);
		this.lockState = LockStateEnum.NOLOCK;
		this.id = id;
	}


	public JvnObjectDistr(Serializable jos, JvnServerDistrImpl jvnServerDistrImpl, int id2) {
		this.setObject(object);
		this.setLocalDistr(jvnServerDistrImpl);
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
			try {
				this.object=this.localDistr.jvnLockRead(id);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JvnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		System.out.println(this.id);
		switch(this.lockState) {
		case WRITELOCKCACHED:
			this.lockState=LockStateEnum.WRITELOCK;
			break;
		case WRITELOCK:
			break;
		default:
			try {
				this.object=this.localDistr.jvnLockWrite(this.id);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JvnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	public String jvnGetObjectIdS() throws jvn.JvnException {
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


	public JvnServerDistrImpl getLocalDistr() {
		return localDistr;
	}


	public void setLocalDistr(JvnServerDistrImpl localDistr) {
		this.localDistr = localDistr;
	}


	public JvnNode getLocalNode() {
		return localNode;
	}


	public void setLocalNode(JvnNode localNode) {
		this.localNode = localNode;
	}


	@Override
	public int jvnGetObjectId() throws JvnException {
		// TODO Auto-generated method stub
		return 0;
	}
}

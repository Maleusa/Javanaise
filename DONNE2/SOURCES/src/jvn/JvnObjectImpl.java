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
	private LockStateEnum lockState = LockStateEnum.NOLOCK;
	private int id;
	private Serializable object;
	
	public JvnObjectImpl(Serializable object) {
		this.object = object;
	}

	/**
	 * Get a Read lock on the shared object
	 *
	 * @throws JvnException
	 **/
	public void jvnLockRead() throws jvn.JvnException {
		this.lockState = LockStateEnum.READLOCK;
	}

	/**
	 * Get a Write lock on the object
	 * @return 
	 *
	 * @throws JvnException
	 **/
	public void jvnLockWrite() throws jvn.JvnException {
		try {
			// To take a lock we have to wait a no lock state.
			while (this.lockState != LockStateEnum.NOLOCK) {
				wait();
			}
			this.lockState = LockStateEnum.WRITELOCK;
			
			// TODO: This will notify a single thread. notifyAll() is maybe more appropriated?
			notify();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unlock the object
	 *
	 * @throws JvnException
	 **/
	public void jvnUnLock() throws jvn.JvnException {
		this.lockState = LockStateEnum.NOLOCK;			
		notify();
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
		this.lockState = LockStateEnum.NOLOCK;			
		notify();
	}

	/**
	 * Invalidate the Write lock of the JVN object
	 *
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnInvalidateWriter() throws jvn.JvnException {
		this.lockState = LockStateEnum.NOLOCK;
		return this;
	}

	/**
	 * Reduce the Write lock of the JVN object
	 *
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader() throws jvn.JvnException {
		this.lockState = LockStateEnum.READLOCKCACHED;
		return this;
	}
}

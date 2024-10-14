/***
 * JAVANAISE API
 * JvnRemoteServer interface
 * Defines the remote interface provided by a JVN server 
 * This interface is intended to be invoked by the Javanaise coordinator 
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.*;
import java.io.*;


/**
 * Remote interface of a JVN server (used by a remote JvnCoord) 
 */

public interface JvnRemoteDistrServer extends Remote {
	    
	/**
	* Invalidate the Read lock of a JVN object 
	* @param joi : the JVN object id
	* @throws java.rmi.RemoteException,JvnException
	**/
  public void jvnInvalidateReader(String joi)
	throws java.rmi.RemoteException,jvn.JvnException;
	    
	/**
	* Invalidate the Write lock of a JVN object 
	* @param joi : the JVN object id
	* @return the current JVN object state 
	* @throws java.rmi.RemoteException,JvnException
	**/
        public Serializable jvnInvalidateWriter(String joi)
	throws java.rmi.RemoteException,jvn.JvnException;
	
	/**
	* Reduce the Write lock of a JVN object 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
   public Serializable jvnInvalidateWriterForReader(String joi)
	 throws java.rmi.RemoteException,jvn.JvnException;

	Serializable jvnLockWrite(String joi) throws JvnException;

	Serializable jvnLockRead(String joi) throws JvnException;

	JvnObject jvnCreateObject(Serializable jos) throws JvnException, RemoteException;

	void jvnRegisterObject(String jon, JvnObject jo) throws JvnException;

	JvnObject jvnLookupObject(String jon) throws JvnException;

	void jvnTerminate() throws JvnException;

}

 

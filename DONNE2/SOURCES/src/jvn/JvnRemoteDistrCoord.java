/***
 * JAVANAISE API
 * JvnRemoteCoord interface
 * This interface defines the remote interface provided by the Javanaise coordinator
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.*;
import java.util.ArrayList;
import java.io.*;


/**
 * Remote Interface of the JVN Coordinator  
 */

public interface JvnRemoteDistrCoord extends Remote {

	public void updateList(ArrayList<JvnNode> jvnList) throws RemoteException;
	/**
	*  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
	* @throws java.rmi.RemoteException,JvnException
	**/
	public String jvnGetObjectId()
	throws java.rmi.RemoteException,jvn.JvnException;  
	
	/**
	* Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo  : the JVN object
	* @param js  : the remote reference of the JVNServer
	* @throws java.rmi.RemoteException,JvnException
	**/
	public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteDistrServer js)
	throws java.rmi.RemoteException,jvn.JvnException; 
	
	/**
	* Get the reference of a JVN object managed by a given JVN server 
	* @param jon : the JVN object name
	* @param js : the remote reference of the JVNServer
	* @throws java.rmi.RemoteException,JvnException
	**/
	public JvnObject jvnLookupObject(String jon, JvnRemoteDistrServer js)
	throws java.rmi.RemoteException,jvn.JvnException; 
	
	/**
	* Get a Read lock on a JVN object managed by a given JVN server 
	* @param joi : the JVN object identification
	* @param js  : the remote reference of the server
	* @return the current JVN object state
	* @throws java.rmi.RemoteException, JvnException
	**/
   Serializable jvnLockRead(String joi, JvnRemoteDistrServer js) 
	 throws RemoteException, JvnException;

	/**
	* Get a Write lock on a JVN object managed by a given JVN server 
	* @param joi : the JVN object identification
	* @param js  : the remote reference of the server
	* @return the current JVN object state
	* @throws java.rmi.RemoteException, JvnException
	**/
   Serializable jvnLockWrite(String joi, JvnRemoteDistrServer js) throws RemoteException, JvnException;

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
  public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException;
	void registerjvnServer(JvnNode jvnClient2) throws RemoteException;
	
	

 }



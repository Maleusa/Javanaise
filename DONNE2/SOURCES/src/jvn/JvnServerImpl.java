/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.NotBoundException;
import java.util.Random;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;



public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer { 
	
	@Serial
	private static final long serialVersionUID = 1L;

	private static JvnServerImpl js = null; // A JVN server is managed as a singleton 
	private JvnRemoteCoord JvnCoordinator;
	private Registry registry;
	private int id;

	private HashMap<String,JvnObject> cachedObject;

	/**
	* Default constructor
	* @throws JvnException
	**/
	private JvnServerImpl() throws Exception {
		super();
		Random rand= new Random();
		this.id=rand.nextInt();
		this.cachedObject=new HashMap<String,JvnObject>();
		System.out.println("Init JvnServerImpl !");
		if (JvnServerImpl.js == null) {
			JvnServerImpl.js = this;
		}
		this.registry = LocateRegistry.getRegistry();
		JvnRemoteServer remote_stub = (JvnRemoteServer) UnicastRemoteObject.exportObject(this, 0);
		System.out.println("Done init JvnServerImpl");
		registry.bind("remote_service", remote_stub);
		JvnCoordinator = (JvnRemoteCoord) registry.lookup("coord_service");
		JvnCoordinator.registerjvnServer(this);
		// to be completed
	}

	/**
    * Static method allowing an application to get a reference to 
    * a JVN server instance
    * @throws JvnException
    **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null) {
			try {
				js = new JvnServerImpl();
			} catch (ExportException e) {
				// Could be init elsewhere.
				return js;
			} catch (Exception e) {
				System.out.println("Cannot init JvnServerImpl: " + e.getMessage());
				return null;
			}
		}
		return js;
	}

	/**
	* The JVN service is not used anymore
	* @throws JvnException
	**/
	public  void jvnTerminate() throws jvn.JvnException {
		try {
			this.JvnCoordinator.jvnTerminate(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JvnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JvnServerImpl.js = null;
	} 

	/**
	* creation of a JVN object
	* @param o : the JVN object state
	* @throws JvnException
	 * @throws RemoteException 
	**/
	public JvnObject jvnCreateObject(Serializable o) throws jvn.JvnException, RemoteException {
		int id =this.JvnCoordinator.jvnGetObjectId();
		JvnObject jvnObject = new JvnObjectImpl(o,this,id);
		cachedObject.put(this.id+" "+ this.cachedObject.size()+ " ", jvnObject);
		return jvnObject;
		// to do
	}

	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object 
	* @throws JvnException
	**/
	public void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException {
		try {
			this.JvnCoordinator.jvnRegisterObject(jon, jo, js);
		} catch (RemoteException | JvnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	* Provide the reference of a JVN object being given its symbolic name
	* @param jon : the JVN object name
	* @return the JVN object 
	* @throws JvnException
	**/
	public JvnObject jvnLookupObject(String jon) throws jvn.JvnException {
		JvnObjectImpl object=null;
		try {
			object= (JvnObjectImpl) this.JvnCoordinator.jvnLookupObject(jon, this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JvnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(object!=null) {
			object.setLocalServer(this);;
			this.cachedObject.put(jon, object);
		}
		return object;
       
	}	

	/**
	* Get a Read lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
	public Serializable jvnLockRead(int joi)throws JvnException {
		Serializable obj = this.cachedObject.get(joi).jvnGetSharedObject();
		try {
			obj = this.JvnCoordinator.jvnLockRead(joi, this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JvnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return obj;
	}

	/**
	* Get a Write lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
	public Serializable jvnLockWrite(int joi) throws JvnException {
		Serializable obj = this.cachedObject.get(joi).jvnGetSharedObject();
		try {
			obj = this.JvnCoordinator.jvnLockWrite(joi, this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JvnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return obj;
	}	

	/**
	* Invalidate the Read lock of the JVN object identified by id 
	* called by the JvnCoord
	* @param joi : the JVN object id
	* @return void
	* @throws java.rmi.RemoteException,JvnException
	**/
	public void jvnInvalidateReader(int joi) throws java.rmi.RemoteException,jvn.JvnException {
		this.cachedObject.get(joi).jvnInvalidateReader();
	}
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
	public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException,jvn.JvnException { 
		
		return this.cachedObject.get(joi).jvnInvalidateWriter();
	}
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
	public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException,jvn.JvnException { 
	 
		return this.cachedObject.get(joi).jvnInvalidateWriterForReader();
	}

}

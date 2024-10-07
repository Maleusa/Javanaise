/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 *
 * Authors: Florent Pouzol, Hugo Triolet, Yazid Cheriti
 */

package jvn;

import java.util.Random;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.io.*;


public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer { 
	
	@Serial
	private static final long serialVersionUID = 1L;
	private static JvnServerImpl js = null; // A JVN server is managed as a singleton

	private final JvnRemoteCoord jvnCoordinator;
	private Registry registry;
	private int id;

	private final HashMap<Integer, JvnObject> cachedObjects;

	/**
	* Default constructor
	* @throws JvnException
	**/
	private JvnServerImpl() throws Exception {
		super();
		Random rand = new Random();
		this.id = rand.nextInt();
		this.cachedObjects = new HashMap<Integer, JvnObject>();
		this.registry = LocateRegistry.getRegistry();
		this.jvnCoordinator = (JvnRemoteCoord) registry.lookup("coord_service");
		this.jvnCoordinator.registerjvnServer(this);
		js = this;
	}

	/**
    * Static method allowing an application to get a reference to 
    * a JVN server instance
    * @throws JvnException
    **/
	public static JvnServerImpl jvnGetServer() throws Exception {
		if (js == null) {
			new JvnServerImpl();
			System.out.println("Server is up");
		}
		return js;
	}

	/**
	* The JVN service is not used anymore
	* @throws JvnException
	**/
	public void jvnTerminate() throws jvn.JvnException {
		try {
			this.jvnCoordinator.jvnTerminate(this);
		} catch (RemoteException | JvnException e) {
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
		int id = this.jvnCoordinator.jvnGetObjectId();
		System.out.println("jvnCreateObject -> o =");
		System.out.println(o.toString());
		JvnObject jvnObject = new JvnObjectImpl(o,this,id);
		cachedObjects.put(this.id, jvnObject);
		return jvnObject;
	}

	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object 
	* @throws JvnException
	**/
	public void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException {
		try {
			cachedObjects.put(jo.jvnGetObjectId(), jo);
			this.jvnCoordinator.jvnRegisterObject(jon, jo, js);
		} catch (RemoteException | JvnException e) {
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
		JvnObjectImpl object;
		try {
			object = (JvnObjectImpl) this.jvnCoordinator.jvnLookupObject(jon, this);
		} catch (RemoteException | JvnException e) {
			return null;
		}
		if (object != null) {
			object.setLocalServer(this);;
			this.cachedObjects.put(object.jvnGetObjectId(), object);
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
		Serializable obj = this.cachedObjects.get(joi).jvnGetSharedObject();
		try {
			obj = this.jvnCoordinator.jvnLockRead(joi, this);
		} catch (RemoteException | JvnException e) {
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
		Serializable obj = this.cachedObjects.get(joi).jvnGetSharedObject();
		try {
			obj = this.jvnCoordinator.jvnLockWrite(joi, this);
		} catch (RemoteException | JvnException e) {
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
		this.cachedObjects.get(joi).jvnInvalidateReader();
	}
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
	public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException,jvn.JvnException {
		return this.cachedObjects.get(joi).jvnInvalidateWriter();
	}
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
	public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException,jvn.JvnException {
		return this.cachedObjects.get(joi).jvnInvalidateWriterForReader();
	}

}

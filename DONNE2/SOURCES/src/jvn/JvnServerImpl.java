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
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;



public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer { 
	
	@Serial
	private static final long serialVersionUID = 1L;

	private static JvnServerImpl js = null; // A JVN server is managed as a singleton 
	private JvnRemoteCoord JvnCoordinator;
	private Registry registry;

	/**
	* Default constructor
	* @throws JvnException
	**/
	private JvnServerImpl() throws Exception {
		super();
		System.out.println("Init JvnServerImpl !");
		if (JvnServerImpl.js == null) {
			JvnServerImpl.js = this;
		}
		this.registry = LocateRegistry.getRegistry();
		JvnRemoteServer remote_stub = (JvnRemoteServer) UnicastRemoteObject.exportObject(this, 0);
		System.out.println("Done init JvnServerImpl");
		registry.bind("remote_service", remote_stub);
		JvnCoordinator = (JvnRemoteCoord) registry.lookup("coord_service");
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
		// to be completed
		JvnServerImpl.js = null;
	} 

	/**
	* creation of a JVN object
	* @param o : the JVN object state
	* @throws JvnException
	**/
	public JvnObject jvnCreateObject(Serializable o) throws jvn.JvnException { 
		JvnObject jo = new JvnObjectImpl(o);
		this.jvnRegisterObject("objectnametoto", jo);
		return null;
		// to do
	}

	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object 
	* @throws JvnException
	**/
	public void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException {
		// to be completed 
	}

	/**
	* Provide the reference of a JVN object being given its symbolic name
	* @param jon : the JVN object name
	* @return the JVN object 
	* @throws JvnException
	**/
	public JvnObject jvnLookupObject(String jon) throws jvn.JvnException {
		// to be completed
        try {
            return (JvnObject) registry.lookup(jon);
        } catch (RemoteException | NotBoundException e) {
            //throw new RuntimeException(e);
        }
        return null;
	}	

	/**
	* Get a Read lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
	public Serializable jvnLockRead(int joi)throws JvnException {
		// to be completed 
		return null;
	}

	/**
	* Get a Write lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
	public Serializable jvnLockWrite(int joi) throws JvnException {
		// to be completed 
		return null;
	}	

	/**
	* Invalidate the Read lock of the JVN object identified by id 
	* called by the JvnCoord
	* @param joi : the JVN object id
	* @return void
	* @throws java.rmi.RemoteException,JvnException
	**/
	public void jvnInvalidateReader(int joi) throws java.rmi.RemoteException,jvn.JvnException {
		// to be completed 
	}
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
	public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException,jvn.JvnException { 
		// to be completed 
		return null;
	}
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
	public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException,jvn.JvnException { 
		// to be completed 
		return null;
	}

}

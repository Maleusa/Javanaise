/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;


public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
	

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int jvnObjectId;
	private HashMap <Integer,String> jvnObjectIdList;
	private ArrayList<JvnRemoteServer> jvnServerList;
	private HashMap <String,JvnObject> remoteObjectList;
	private ArrayList<JvnObject> jvnObjectList;
	private HashMap<jvnObjectId,<JvnRemoteServer,>
	private Registry registry;
/**
  * Default constructor
  * @throws JvnException
  **/
	private JvnCoordImpl() throws Exception {
		// to be completed
		this.jvnObjectId=0;
		this.jvnObjectList = new ArrayList<JvnObject>();
		this.jvnServerList = new ArrayList<JvnRemoteServer>();//CECI EST FAUX
		//AJOUTER HASHMAP DE MAP POUR LES ETATS DE LOCKS ET MODIFIER LES FONCTIONS QUI EN DEPAND
		
		this.remoteObjectList = new HashMap<String,JvnObject>();
		this.registry= LocateRegistry.getRegistry();
		JvnRemoteCoord remote_stub= (JvnRemoteCoord) UnicastRemoteObject.exportObject(this, 0);
		registry.bind("coord_service", remote_stub);
		System.out.println("Coord service UP");
	}

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
  * @throws java.rmi.RemoteException,JvnException
  **/
  public int jvnGetObjectId()
  throws java.rmi.RemoteException,jvn.JvnException {
    
    return this.jvnObjectId+=1;
  }
  
  /**
  * Associate a symbolic name with a JVN object
  * @param jon : the JVN object name
  * @param jo  : the JVN object 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
    if(!this.jvnServerList.contains(js))
    		return;
    Integer joi = this.jvnGetObjectId();
    this.jvnObjectIdList.put(joi, jon);
    this.remoteObjectList.put(jon, jo);
	  // to be completed 
  }
  
  /**
  * Get the reference of a JVN object managed by a given JVN server 
  * @param jon : the JVN object name
  * @param js : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
    if(!this.jvnServerList.contains(js))// to be completed 
    	return null;
    return null; //TODO
   
  }
  
  /**
  * Get a Read lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockRead(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
	   if(!this.jvnServerList.contains(js))// to be completed 
	    	return null;
	   // to be completed
    return null;//TODO
   }

  /**
  * Get a Write lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
	   if(!this.jvnServerList.contains(js))// to be completed 
	    	return null;
	   // to be completed
   return null;//TODO
    
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
	 this.jvnServerList.remove(js);
    }

	public HashMap <String,JvnObject> getRemoteObjectList() {
		return remoteObjectList;
	}

	public void setRemoteObjectList(HashMap <String,JvnObject> remoteObjectList) {
		this.remoteObjectList = remoteObjectList;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
}

 

/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 *
 * Authors: Florent Pouzol, Hugo Triolet, Yazid Cheriti
 */

package jvn;

import java.io.Serial;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;


public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {
    @Serial
    private static final long serialVersionUID = 1L;
    private int jvnObjectId;
    private final HashMap<String, Integer> jvnObjectIdList;
    private final ArrayList<JvnRemoteServer> jvnServerList;
    private final HashMap<Integer, JvnObject> jvnObjectList;
    private final HashMap<Integer, ArrayList<JvnRemoteServer>> readerList;
    private final HashMap<Integer, JvnRemoteServer> writerList;

    public static void main(String[] args) throws Exception {
        new JvnCoordImpl();
        System.out.println("Coordinator service UP");
    }

    /**
     * Default constructor
     *
     * @throws JvnException
     **/
    private JvnCoordImpl() throws Exception {
		// Init variables
        this.jvnObjectId = 0;
        this.jvnObjectList = new HashMap<Integer, JvnObject>();
        this.jvnServerList = new ArrayList<JvnRemoteServer>();
        this.jvnObjectIdList = new HashMap<String, Integer>();
        this.readerList = new HashMap<Integer, ArrayList<JvnRemoteServer>>();
        this.writerList = new HashMap<Integer, JvnRemoteServer>();

        // Create registry and bind coordinator
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("coord_service", this);
    }

    /**
     * Allocate a NEW JVN object id (usually allocated to a newly created JVN
     * object)
     *
     * @throws java.rmi.RemoteException,JvnException
     **/
    public int jvnGetObjectId() throws java.rmi.RemoteException, jvn.JvnException {
        return this.jvnObjectId += 1;
    }

    /**
     * Associate a symbolic name with a JVN object
     *
     * @param jon : the JVN object name
     * @param jo  : the JVN object
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
            throws java.rmi.RemoteException, jvn.JvnException {
        if (!this.jvnServerList.contains(js)) {
            System.out.println("Skip: JS not found to register the object " + jon);
			return;
		}
        int joi = jo.jvnGetObjectId();

        this.jvnObjectIdList.put(jon, joi);
        this.jvnObjectList.put(joi, jo);

        this.readerList.put(joi, new ArrayList<JvnRemoteServer>());
        this.writerList.put(joi, null);
    }

    /**
     * Register a new sJVN server
     *
     * @param js : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException
     */
    public void registerjvnServer(JvnRemoteServer js) throws java.rmi.RemoteException {
        jvnServerList.add(js);
        System.out.println("Registered a new JvnServer");
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     *
     * @param jon : the JVN object name
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException, JvnException
     **/
    public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        if (!this.jvnServerList.contains(js)) {
            throw new JvnException("Server not found");
        }

		if (!this.jvnObjectIdList.containsKey(jon)) {
			throw new JvnException("Remote object not found");
		}

        Integer id = this.jvnObjectIdList.get(jon);
        return this.jvnObjectList.get(id);
    }

    /**
     * Get a Read lock on a JVN object managed by a given JVN server
     *
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        JvnObject jo = this.jvnObjectList.get(joi);
        if (jo == null) {
            throw new JvnException("Remote object not found");
        }

        Serializable serializable = jo.jvnGetSharedObject();
        JvnRemoteServer writer = this.writerList.get(joi);

        // If object is write locked by another server, invalidate its write lock
        // and add it to the list of readers
        if (writer != null && !writer.equals(js)) {
            serializable = writer.jvnInvalidateWriterForReader(joi);
            jo.setObject(serializable);
            this.writerList.put(joi, null);
            this.readerList.get(joi).add(writer);
        }

        this.readerList.get(joi).add(js);
        return serializable;
    }

    /**
     * Get a Write lock on a JVN object managed by a given JVN server
     *
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        JvnObject jo = this.jvnObjectList.get(joi);
        if (jo == null) {
            throw new JvnException("Remote object not found");
        }

        Serializable serializable = jo.jvnGetSharedObject();
        JvnRemoteServer writer = this.writerList.get(joi);

        // Invalidate lock for the server having a write lock
        if (writer != null && !writer.equals(js)) {
            serializable = writer.jvnInvalidateWriter(joi);
            jo.setObject(serializable);
        }

        // Invalidate lock for the servers having a read lock
        for (JvnRemoteServer reader: this.readerList.get(joi)) {
            if (!reader.equals(js)) {
				reader.jvnInvalidateReader(joi);
			}
        }

        // Set the given js as writer
        this.readerList.get(joi).clear();
        this.writerList.put(joi, js);

        return serializable;
    }

    /**
     * A JVN server terminates
     *
     * @param js : the remote reference of the server
     * @throws java.rmi.RemoteException, JvnException
     **/
    public void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        this.jvnServerList.remove(js);

        // Fetch all objects
        for (Integer objectId : jvnObjectList.keySet()) {

            // Check and clean readers if necessary
            ArrayList<JvnRemoteServer> readers = this.readerList.get(objectId);
            if (readers != null) {
                readers.remove(js);
            }

            // Check and clean writer if necessary
            JvnRemoteServer writer = this.writerList.get(objectId);
            if (writer != null && writer.equals(js)) {
                this.writerList.put(objectId, null);
            }
        }
    }
}

/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 *
 * Authors: Florent Pouzol, Hugo Triolet, Yazid Cheriti
 */

package jvn;

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
    private final HashMap<Integer, JvnObject> cachedObjects;

    /**
     * Default constructor
     **/
    protected JvnServerImpl() throws Exception {
        super();
        this.cachedObjects = new HashMap<Integer, JvnObject>();
        Registry registry = LocateRegistry.getRegistry();
        this.jvnCoordinator = (JvnRemoteCoord) registry.lookup("coord_service");
        this.jvnCoordinator.registerjvnServer(this);
        js = this;
    }

    /**
     * Static method allowing an application to get a reference to
     * a JVN server instance
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
     **/
    public void jvnTerminate() throws jvn.JvnException {
        try {
            this.jvnCoordinator.jvnTerminate(this);
            System.out.println("Server is being stopped");
        } catch (RemoteException | JvnException e) {
            e.printStackTrace();
        }
        JvnServerImpl.js = null;
    }

    /**
     * creation of a JVN object
     *
     * @param o : the JVN object state
     **/
    public JvnObject jvnCreateObject(Serializable o) throws jvn.JvnException, RemoteException {
        int id = this.jvnCoordinator.jvnGetObjectId();
        JvnObject jvnObject = new JvnObjectImpl(o, this, id);
        this.cachedObjects.put(id, jvnObject);
        return jvnObject;
    }

    /**
     * Associate a symbolic name with a JVN object
     *
     * @param jon : the JVN object name
     * @param jo  : the JVN object
     **/
    public void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException {
        try {
            this.jvnCoordinator.jvnRegisterObject(jon, jo, js);
        } catch (RemoteException | JvnException e) {
            System.out.println("Could not register object " + jon);
            e.printStackTrace();
        }
    }

    /**
     * Provide the reference of a JVN object being given its symbolic name
     *
     * @param jon : the JVN object name
     * @return the JVN object
     **/
    public JvnObject jvnLookupObject(String jon) throws jvn.JvnException {
        JvnObjectImpl object;

        try {
            object = (JvnObjectImpl) this.jvnCoordinator.jvnLookupObject(jon, this);
        } catch (RemoteException | JvnException e) {
            return null;
        }

        if (object != null) {
            object.setLocalServer(this);
            this.cachedObjects.put(object.jvnGetObjectId(), object);
        }

        return object;
    }

    /**
     * Get a Read lock on a JVN object
     *
     * @param joi : the JVN object identification
     * @return the current JVN object state
     **/
    public synchronized Serializable jvnLockRead(int joi) throws JvnException {
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
     *
     * @param joi : the JVN object identification
     * @return the current JVN object state
     **/
    public synchronized Serializable jvnLockWrite(int joi) throws JvnException {
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
     *
     * @param joi : the JVN object id
     **/
    public synchronized void jvnInvalidateReader(int joi) throws java.rmi.RemoteException, jvn.JvnException {
        this.cachedObjects.get(joi).jvnInvalidateReader();
    }

    /**
     * Invalidate the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     **/
    public synchronized Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException, jvn.JvnException {
        return this.cachedObjects.get(joi).jvnInvalidateWriter();
    }

    /**
     * Reduce the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     **/
    public synchronized Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException, jvn.JvnException {
        return this.cachedObjects.get(joi).jvnInvalidateWriterForReader();
    }

}

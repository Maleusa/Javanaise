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
import java.util.Map;


public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final int maxCacheSize = 5;
    private static JvnServerImpl js = null; // A JVN server is managed as a singleton
    private final JvnRemoteCoord jvnCoordinator;
    private final HashMap<Integer, JvnObject> cachedObjects;
    private final HashMap<Integer, String> jvnObjectNameList;

    /**
     * Default constructor
     **/
    private JvnServerImpl() throws Exception {
        super();
        this.cachedObjects = new HashMap<Integer, JvnObject>();
        this.jvnObjectNameList = new HashMap<Integer, String>();
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
        this.addObjectToCache(id, jvnObject);
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
            System.out.println("jvnRegisterObject -> jvnObjectNameList, joi: " + jo.jvnGetObjectId() + " for jon: " + jon);
            this.jvnObjectNameList.put(jo.jvnGetObjectId(), jon); // Keep joi<->jon relation for JO server cache
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
            System.out.println("Put in jvnObjectNameList, joi: " + object.jvnGetObjectId() + " for jon: " + jon);
            this.jvnObjectNameList.put(object.jvnGetObjectId(), jon); // Keep joi<->jon relation for JO server cache
            this.addObjectToCache(object.jvnGetObjectId(), object);
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
        Serializable obj = this.getCachedObject(joi).jvnGetSharedObject();

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
        Serializable obj = this.getCachedObject(joi).jvnGetSharedObject();

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
        this.getCachedObject(joi).jvnInvalidateReader();
    }

    /**
     * Invalidate the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     **/
    public synchronized Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException, jvn.JvnException {
        return this.getCachedObject(joi).jvnInvalidateWriter();
    }

    /**
     * Reduce the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     **/
    public synchronized Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException, jvn.JvnException {
        return this.getCachedObject(joi).jvnInvalidateWriterForReader();
    }

    /**
     * Add to cache
     *
     * @param joi : the JVN object identification
     * @param obj : the JVN shared object
     */
    public void addObjectToCache(int joi, JvnObject obj) throws JvnException {
        if (this.cachedObjects.size() >= maxCacheSize) {
            this.flushEldestCachedObject();
        }
        this.cachedObjects.put(joi, obj);
    }

    /**
     * Get cached object or retrieve it from Coordinator
     *
     * @param joi : the JVN object identification
     * @return JvnObject|null
     * @throws JvnException
     */
    public JvnObject getCachedObject(int joi) throws JvnException {
        JvnObject obj = this.cachedObjects.get(joi);

        if (obj == null) {
            System.out.println("Object with ID " + joi + " was flushed. Trying to recover it from coordinator.");
            try {
                // Get JON from local object name list by a JOI
                String jon = jvnObjectNameList.get(joi);
                if (jon == null) {
                    throw new JvnException("Cannot find JON in jvnObjectNameList for object " + joi);
                }

                // Get from coordinator to put it back in cache
                obj = jvnLookupObject(jon);
                if (obj == null) {
                    throw new JvnException("Cannot find in coordinator flushed object " + joi + " / jon = " + jon);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new JvnException("Cannot retrieve flushed object " + joi);
            }
        }

        return obj;
    }

    /**
     * Flush eldest cached object
     *
     * @throws JvnException
     */
    private void flushEldestCachedObject() throws JvnException {
        Map.Entry<Integer, JvnObject> eldest = this.cachedObjects.entrySet().iterator().next();
        if (eldest != null) {
            JvnObject objectToFlush = eldest.getValue();
            try {
                objectToFlush.jvnUnLock();
            } catch (JvnException e) {
                System.out.println("Cannot flush cached object: " + eldest.getKey());
                e.printStackTrace();
            }
            this.cachedObjects.remove(eldest.getKey());
            System.out.println("Flushed object with ID: " + eldest.getKey());
        }
    }

}

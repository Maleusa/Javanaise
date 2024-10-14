package jvn;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

public class JvnNode implements JvnRemoteCoord,Serializable{
	private static final String separator = ";";
	private static final long serialVersionUID = 1L;
	private JvnRemoteCoord jc;
	private int id;
	private int serverId;
	private int jvnObjectIdInternal;
    private static JvnNode jn = null; // A JVN server is managed as a singleton
    private static JvnRemoteServer js=null;
    private JvnServerDistrImpl JvnClient;
    private final HashMap<String, Integer> jvnObjectIdList;
    private final ArrayList<JvnNode> jvnServerList;
    private final HashMap<Integer, JvnObject> jvnObjectList;
    private final HashMap<Integer, ArrayList<JvnRemoteServer>> readerList;
    private final HashMap<Integer, JvnRemoteServer> writerList;
	private JvnNode jvnPresident;
    
    private JvnNode(int arg) throws Exception {
    	this.jvnObjectIdInternal = 0;
        this.jvnObjectList = new HashMap<Integer, JvnObject>();
        this.jvnServerList = new ArrayList<JvnNode>();
        this.jvnObjectIdList = new HashMap<String, Integer>();
        this.readerList = new HashMap<Integer, ArrayList<JvnRemoteServer>>();
        this.writerList = new HashMap<Integer, JvnRemoteServer>();
        this.JvnClient=new JvnServerDistrImpl(this);
        Registry registry;
        if(arg==0) {
        	registry = LocateRegistry.createRegistry(1099);
            registry.bind("coord_service", this);
            this.id=0;

        }
        else {
        	registry=LocateRegistry.getRegistry();
        	this.jvnPresident = (JvnNode) registry.lookup("coord_service");
        	this.jc=this;
        	jvnPresident.registerjvnServer(JvnClient);
        }        
    }

	@Override
	public int jvnGetObjectId() throws RemoteException, JvnException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws RemoteException, JvnException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws RemoteException, JvnException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Serializable jvnLockRead(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void jvnTerminate(JvnRemoteServer js) throws RemoteException, JvnException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void registerjvnServer(JvnRemoteServer js) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
    
}

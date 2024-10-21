package jvn;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class JvnNode extends UnicastRemoteObject implements JvnRemoteDistrCoord,Serializable{
	private static final String separator = ";";
	private static final long serialVersionUID = 1L;
	private JvnRemoteDistrCoord jc;
	private int id;
	private int serverId;
	private int jvnObjectIdInternal;
    private static JvnNode jn = null; // A JVN server is managed as a singleton
    private static JvnRemoteDistrServer js=null;
    private JvnServerDistrImpl JvnClient;
    private final HashMap<String, String> jvnObjectIdList;
    private ArrayList<JvnNode> jvnServerList;
    private final HashMap<String, JvnObject> jvnObjectList;
    private final HashMap<Integer, ArrayList<JvnRemoteDistrServer>> readerList;
    private final HashMap<Integer, JvnRemoteDistrServer> writerList;
	private JvnNode jvnPresident;
    
    public JvnNode(int arg) throws Exception {
    	this.jvnObjectIdInternal = 0;
        this.jvnObjectList = new HashMap<String, JvnObject>();
        this.jvnServerList = new ArrayList<JvnNode>();
        this.jvnObjectIdList = new HashMap<String, String>();
        this.readerList = new HashMap<Integer, ArrayList<JvnRemoteDistrServer>>();
        this.writerList = new HashMap<Integer, JvnRemoteDistrServer>();
       
        
        if(arg==0) {
        	Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("coord_service", this);
            this.id=0;
            this.jvnServerList.add(this);
            this.jc=this;

        }
        else {
        	Registry registry=LocateRegistry.getRegistry(1099);
        	this.jvnPresident = (JvnNode) registry.lookup("coord_service");
        	this.jc=jvnPresident;
        	jvnPresident.registerjvnServer(this);
        }
        this.JvnClient=new JvnServerDistrImpl(this) ;
        js =this.JvnClient;
        jn=this;
    }

	@Override
	public String jvnGetObjectId() throws RemoteException, JvnException {
		this.jvnObjectIdInternal+=1;
		String id =this.id +";"+this.jvnObjectIdInternal;
		System.out.println(id);
				return id;
	}
	@Override
	public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteDistrServer js) throws RemoteException, JvnException {
		 
//		        if (!this.jvnServerList.contains(js)) {
//		            System.out.println("Skip: JS not found to register the object " + jon);
//					return;
//		        }
		        //String id =this.jvnGetObjectId();

		        this.jvnObjectIdList.put(jon, jo.jvnGetObjectIdS());
		        this.jvnObjectList.put(jo.jvnGetObjectIdS(), jo);
		        String[] words = jo.jvnGetObjectIdS().split(separator);
		        this.readerList.put(Integer.valueOf(words[1]), new ArrayList<JvnRemoteDistrServer>());
		        this.writerList.put(Integer.valueOf(words[1]), null);
		    }
		
	
	@Override
	public JvnObject jvnLookupObject(String jon, JvnRemoteDistrServer js) throws RemoteException, JvnException {
//        if (!this.jvnServerList.contains(js)) {
//            throw new JvnException("Server not found");
//        }

//		if (!this.jvnObjectIdList.containsKey(jon)) {
//			throw new JvnException("Remote object not found");
//		}

        String id = this.jvnObjectIdList.get(jon);
        String[] words = id.split(separator);
        if(Integer.valueOf(words[0])==this.id) {
        	return this.jvnObjectList.get(Integer.valueOf(words[1]));
        }else
        return this.jvnServerList.get(Integer.valueOf(words[0])).jvnLookupObject(jon, js);
	}
	@Override
	public Serializable jvnLockRead(String joi, JvnRemoteDistrServer js) throws RemoteException, JvnException {
		String[] words = joi.split(separator);
		if(Integer.getInteger(words[0])!=this.id) {
			return this.jvnServerList.get(Integer.valueOf(words[0])).jvnLockRead(joi, js);
		}
		JvnObject jo = this.jvnObjectList.get(joi);
        if (jo == null) {
            throw new JvnException("Remote object not found");
        }
        Serializable serializable = jo.jvnGetSharedObject();
        JvnRemoteDistrServer writer = this.writerList.get(Integer.valueOf(words[1]));

        // If object is write locked by another server, invalidate its write lock
        // and add it to the list of readers
        if (writer != null && !writer.equals(js)) {
            serializable = writer.jvnInvalidateWriterForReader(joi);
            jo.setObject(serializable);
            this.writerList.put(Integer.valueOf(words[1]), null);
            this.readerList.get(Integer.valueOf(words[1])).add(writer);
        }

        this.readerList.get(Integer.valueOf(words[1])).add(js);
        return serializable;
	}
	@Override
	public Serializable jvnLockWrite(String joi, JvnRemoteDistrServer js) throws RemoteException, JvnException {
		System.out.println(joi);
		String[] words = joi.split(separator);
		if(Integer.valueOf(words[0])!=this.id) {
			return this.jvnServerList.get(Integer.valueOf(words[0])).jvnLockWrite(joi, js);
		}
		JvnObject jo = this.jvnObjectList.get(joi);
        if (jo == null) {
            throw new JvnException("Remote object not found");
        }

        Serializable serializable = jo.jvnGetSharedObject();
        JvnRemoteDistrServer writer = this.writerList.get(Integer.valueOf(words[1]));

        // Invalidate lock for the server having a write lock
        if (writer != null && !writer.equals(js)) {
            serializable = writer.jvnInvalidateWriter(joi);
            jo.setObject(serializable);
        }

        // Invalidate lock for the servers having a read lock
        for (JvnRemoteDistrServer reader: this.readerList.get(Integer.valueOf(words[1]))) {
            if (!reader.equals(js)) {
				reader.jvnInvalidateReader(joi);
			}
        }

        // Set the given js as writer
        this.readerList.get(Integer.valueOf(words[1])).clear();
        this.writerList.put(Integer.valueOf(words[1]), js);

        return serializable;
	}
	@Override
	public void jvnTerminate(JvnRemoteServer js) throws RemoteException, JvnException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void registerjvnServer(JvnNode jvnClient2) throws RemoteException {
		if (id==0) {
			this.jvnServerList.add(jvnClient2);
			for(JvnNode node : jvnServerList) {
				node.updateList(this.jvnServerList);
			}
		}
		
		
	}

	public void updateList(ArrayList<JvnNode> jvnList) throws RemoteException {
		this.jvnServerList=jvnList;
		
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public static JvnNode getJn() {
		return jn;
	}

	public static void setJn(JvnNode jn) {
		JvnNode.jn = jn;
	}

	public JvnServerDistrImpl getJvnClient() {
		return JvnClient;
	}

	public void setJvnClient(JvnServerDistrImpl jvnClient) {
		JvnClient = jvnClient;
	}

	public static JvnNode jvnGetServer(int arg) {
		if (js == null) {
            try {
				jn=new JvnNode(arg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println("Server is up");
        }
		
        return jn;
		
	}

	


    
}

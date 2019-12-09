// libs
import java.net.*;
import java.io.*;

public class MulticastGroup {
    private String mIP ="224.4.4.4"; // 224.XX.XX.XX - 239.XX.XX.XX
    private int mPort; // XXXX > 1024
    private MulticastSocket mSocket;
    private InetAddress mGroupAddrs=null;

    MulticastGroup(int mPort) {
        this.mPort = mPort;
        if (mJoinGroup())
            System.out.println("OK");
    }

    MulticastGroup (int mPort, String mIP){
        this.mPort = mPort;
        this.mIP = mIP;
        if (mJoinGroup())
            System.out.println("OK");
    }

    public boolean mJoinGroup() {
        System.out.println("\nMulticastGroup ...");
        System.out.print("... Joining group: ");
        try {
            mGroupAddrs = InetAddress.getByName(mIP);
        }catch (UnknownHostException e){
            System.err.println("MulticastGroup - ERR: Can't resolve \""+mIP+"\": "+e);
            return false;
        }

        System.out.print(mGroupAddrs+":"+mPort+"... ");

        try {
            mSocket = new MulticastSocket(mPort);
        }catch (IOException e) {
            System.err.println("... FAIL");
            System.err.println("... Can't creat MulticasSocket:"+mPort+": "+ e);
            return false;
        }

        try {
            mSocket.joinGroup(mGroupAddrs);
        }catch (IOException e) {
            System.err.println("... ERR: Can't join group "+mGroupAddrs+": "+ e);
            return false;
        }
        return true;
    }

    public boolean mLeaveGroup() {
        System.out.println("MulticastGroup - Leaving group...");
        try {
            mSocket.leaveGroup(mGroupAddrs);
        }catch (IOException e) {
            System.out.println("... ERR: Can't leave group: IO Exception:" + e);
            return false;
        }
        System.out.println("... OK");
        return true;
    }

    public MulticastSocket getSocket() {
        return mSocket;
    }

    public int getPort() {
        return mPort;
    }

    public InetAddress getGroupAddrs() {
        return mGroupAddrs;
    }

    public void Close() {
        mSocket.close();
    }
}
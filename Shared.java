//package main;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Shared extends Remote {
	
	public void startWrite(int id, long beginTime) throws Exception;
	public int startRead(int id,long beginTime) throws Exception;
	public void endRead(int id, long endTime) throws Exception;
	public void endWrite(int id, long endTime) throws Exception;
	public int read(int id, int opTime, long beginTime) throws Exception;
	public int write(int id, int opTime, long beginTime) throws Exception;
	public void finish() throws Exception;
	
}

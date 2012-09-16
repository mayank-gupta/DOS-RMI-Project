/**
 * 
 */
//package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.rmi.Naming;

/**
 * @author Mayank
 *
 */
public class MyClient {

  //private int id;
  /**
   * @param args
   */

  public static void main(String... args ) {


    ReaderObj readerObj = new ReaderObj();
    readerObj.load();
    String serverName = readerObj.getMachineName(0, "server");

    int regPort = readerObj.getRegistryPort();
    int id = Integer.parseInt(args[0].trim());
    long startTime = Long.parseLong(args[2].trim());
    int noOfAccesses = readerObj.getNoOfAccesses();
    String type = args[1].trim();
    int sleepTime;
    int opTime;
    boolean reader = false;
    int objValue = -1;
    //File file;
    if(type.equalsIgnoreCase(Constants.READER)){
      sleepTime = readerObj.getReaderSleepTime(id);
      opTime = readerObj.getReaderOpTime(id);
      //file = new File(Constants.READER+id+".log");
      reader = true;
    }else{
      sleepTime = readerObj.getWriterSleepTime(id);
      opTime = readerObj.getWriterOpTime(id);
      //file = new File(Constants.WRITER+id+".log");
    }
    try{
      //PrintStream printStream = new PrintStream(new FileOutputStream(file));
      //System.setOut(printStream);
    }catch(Exception e){
      e.printStackTrace();
    }


    try{
      System.out.println("Client Started");
      System.out.flush();
      long currentTime = System.currentTimeMillis();
      while((currentTime)<(startTime+3000)) {
	Thread.sleep(1);
	currentTime = System.currentTimeMillis();
      }
      System.out.println("waited for appropriate time");
      System.out.flush();
      //Registry registry = LocateRegistry.getRegistry(serverName, regPort);
      Shared stub = (Shared) Naming.lookup("rmi://"+InetAddress.getByName(serverName).getHostAddress()/*"localhost"*/
	  +":"+ regPort+ "/"+Constants.REMOTE_SERVICE_NAME); 
      System.out.println("Connection established");
      System.out.flush();
      for(int i = 0; i<noOfAccesses; i++){
	long beginTime= System.currentTimeMillis() - currentTime;
	Thread.sleep(sleepTime);
	System.out.println("Starting "+ i+"th iteration");
	System.out.flush();
	if(reader){
	  stub.startRead(id,System.currentTimeMillis()-currentTime);
	  objValue = stub.read(id, opTime, beginTime);
	  System.out.println("ObjValue :: "+ objValue);
	  System.out.flush();
	  stub.endRead(id,System.currentTimeMillis()-currentTime);
	}else{
	  stub.startWrite(id,System.currentTimeMillis()-currentTime);
	  objValue = stub.write(id, opTime,beginTime);
	  System.out.println("ObjValue :: "+ objValue);
	  System.out.flush();
	  stub.endWrite(id, System.currentTimeMillis()-currentTime);
	}
	//Thread.sleep(opTime);
      }
      stub.finish();
    }catch(Exception e){
      e.printStackTrace();
    }
    System.exit(0);
  }

}

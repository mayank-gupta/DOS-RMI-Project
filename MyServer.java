//package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class MyServer extends UnicastRemoteObject implements Shared{

  private volatile int objValue = Constants.DEFAULT_VALUE;
  private ArrayList<Info> readerQueue  = new ArrayList<Info>();
  private ArrayList<Info> writerQueue = new ArrayList<Info>();

  private Object readerLock = new Object();
  private Object writerLock = new Object();
  private volatile boolean readerBusy = false;
  private volatile boolean writerBusy = false;
  private PrinterObj printerObj = new PrinterObj();
  //private int topReaderPriority = Constants.DEFAULT_PRIORITY;
  //private volatile int priorityInheritNo =0;
  private boolean propertyInherited = false;
  private long startTime;
  private volatile int noFinished;
  private int totalClients;
  private ReaderObj readerObj = new ReaderObj();
  private int regPort;
  private volatile int iterationCount =0;
  private volatile int counter =0;
  private volatile int priInv =0;

  protected MyServer() throws RemoteException {
    super();
    readerObj.load();
    regPort = readerObj.getRegistryPort();
    totalClients = readerObj.getNoOfReaders() + readerObj.getNoOfWriters();
  }

  /**
   * @return the startTime
   */
  public long getStartTime() {
    return startTime;
  }

  /**
   * @param startTime the startTime to set
   */
  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  /**
   * @return the regPort
   */
  public int getRegPort() {
    return regPort;
  }

  /**
   * @param regPort the regPort to set
   */
  public void setRegPort(int regPort) {
    this.regPort = regPort;
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static void main(String... args ){

    try {
      //File file = new File("server.log");
      //PrintStream printStream = new PrintStream(new FileOutputStream(file));
      //System.setOut(printStream);
      System.out.println("ServiceSequence\tObjectValue\tAccessedBy\tOptTime");
      MyServer myServer = new MyServer();
      myServer.setStartTime(Long.parseLong(args[0].trim()));
      LocateRegistry.createRegistry(myServer.getRegPort());

      Shared stub = myServer;
      Naming.rebind("rmi://"+InetAddress.getLocalHost().getHostAddress()/*myServer.readerObj.getMachineName(0, "server")*//*"localhost"*/
	  +":"+ myServer.getRegPort()+ "/"+Constants.REMOTE_SERVICE_NAME
	  ,stub);
      long currentTime = System.currentTimeMillis();
      while((currentTime)<(myServer.getStartTime()+3000)) {
	Thread.sleep(1);
	currentTime = System.currentTimeMillis();
      }
      myServer.setStartTime(currentTime);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }



  @Override
  public synchronized int startRead(int id, long beginTime) throws Exception {
    readerQueue.add(new Info(id, Constants.READER,Constants.DEFAULT_PRIORITY, beginTime, -1, objValue));
    //wait while there are elements in writer queue and your priority is low or writer is busy in processing
    /*while(!((!writerQueue.isEmpty()&&readerQueue.get(0).priority > Constants.DEFAULT_WRITER_PRIORITY)&&(!writerBusy)||writerQueue.isEmpty())){
      wait();
      }*/
    System.out.println("startRead :: Entering startRead by reader "+id);
    int tempMember = getElementNoForID(id);
    boolean firstElementFlag = false;
    while(((!writerQueue.isEmpty()&&(readerQueue.get(0).getPriority()<=Constants.DEFAULT_WRITER_PRIORITY)||writerBusy||(!firstElementFlag)))){
      System.out.println("startRead :: Reader "+id+ " going to wait");
      wait();
      if(tempMember !=0 &&readerQueue.get(0).getPriority()>Constants.DEFAULT_WRITER_PRIORITY){
	notifyAll();
	//tempMember = getElementNoForID(id);
      }else if(tempMember ==0){
	firstElementFlag = true;
	break;
      }
      tempMember = getElementNoForID(id);
    }
    System.out.println("startRead :: Getting lock by reader "+id);
    //Get position in queue from id if its priority is less then again send it to wait


    /*if(readerQueue.get(0).priority>Constants.DEFAULT_WRITER_PRIORITY){
      while(tempMember!=0){
      while(((!writerQueue.isEmpty())||(readerQueue.get(0).getPriority()<=Constants.DEFAULT_WRITER_PRIORITY))||writerBusy){
      wait();

      }
      System.out.println("startRead :: Wait to get 1st of queue "+tempMember);
      while(writerBusy){
      System.out.println("startRead :: reader "+id+ " going to wait because not first from queue");
      wait();
      }
      tempMember = getElementNoForID(id);
      notifyAll();
      }
      System.out.println("startRead :: got for id 0 "+id );

      }*/
    readerBusy = true;
    if(writerQueue.isEmpty()){
      //send signal to all readers
      return 0;
    }else if ((readerQueue.get(0).getPriority() > Constants.DEFAULT_WRITER_PRIORITY)&&(!readerQueue.get(0).priorityInherited)){

      //Send signal to three readers
      readerQueue.get(0).priorityInherited =true;
      for(int i=1; (i<readerQueue.size())&&(i<3); i++){
	readerQueue.get(i).setPriority(Constants.HIGHEST_PRIORITY);
	readerQueue.get(i).priorityInherited = true;

      }

    }

    return 0;
  }

  @Override
  public int read(int id, int opTime, long beginTime) throws Exception {
    Thread.sleep(opTime);

    int tempMember = getElementNoForID(id);
    if(tempMember != -1){

      readerQueue.get(tempMember).setValue(objValue);
      //readerQueue.get(tempMember).setBeginTime(beginTime);
    }else{
      System.out.println("Error due to element not in queue");
    }
    return objValue;

  }

  @Override
  public synchronized void endRead(int id, long endTime) throws Exception {

    int tempMember = getElementNoForID(id);
    readerQueue.get(tempMember).setEndTime(endTime);
    System.out.println(" Element in queue to be updated "+ tempMember);
    //Print into the event.log
    printerObj.write(readerQueue.get(tempMember));
    System.out.println(iterationCount++ +"\t"+objValue+"\tR"+readerQueue.get(tempMember).getId()+"\t"+
	readerQueue.get(tempMember).beginTime+"-"+readerQueue.get(tempMember).endTime);
    System.out.flush();
    System.out.println("endRead :: Ended reading for reader "+id);
    if(counter ==0){
      while(readerQueue.get(counter).priorityInherited){
	priInv = counter++;
	//System.out.println("Can be a location for infinite loop");
      }
    }
    /*if(readerQueue.get(0).priority>Constants.HIGHEST_PRIORITY){


      counter =0;
      }*/

    if(!readerQueue.get(tempMember).priorityInherited){
      readerQueue.remove(tempMember);
    }else if(readerQueue.get(tempMember).priorityInherited){
      counter--;
      if(counter ==0){
	for(int i=0; i<priInv; i++){
	  readerQueue.remove(i);
	}
	priInv =0;
      }
    }
    if(readerQueue.isEmpty()){
      readerBusy = false;
    }else if((!(writerQueue.isEmpty())&&(readerQueue.get(0).priority< Constants.DEFAULT_WRITER_PRIORITY))){
      readerBusy = false;

    }else if(readerQueue.get(0).priority<Constants.DEFAULT_WRITER_PRIORITY){
      readerBusy = false;
    }
    /*if(!readerQueue.get(0).priorityInherited){
    }
    readerBusy = false;
    }*/
    //readerBusy = false;
    notifyAll();
}

@Override
public synchronized void startWrite(int id, long beginTime) throws Exception {
  //Receive a read or write request
  //Check queues and priority and give control to that 

  //long beginTime = System.currentTimeMillis()-startTime;
  writerQueue.add(new Info(id, Constants.WRITER, Constants.DEFAULT_WRITER_PRIORITY, beginTime, -1, id));
  //wait till reader's priority is greater than writer's or writer is busy or reader is busy

  while((!readerQueue.isEmpty())&&(readerQueue.get(0).getPriority() > Constants.DEFAULT_WRITER_PRIORITY) || (writerBusy)||(readerBusy)){
    System.out.println("startWrite :: Writer "+id+ " going to wait and priority is " + (readerQueue.isEmpty() ? "EMPTY" : ""+readerQueue.get(0).getPriority() ));
    System.out.println("startWrite :: WriterBusy=" + writerBusy);
    System.out.println("startWrite :: ReaderBusy=" + readerBusy);
    wait(); 
  }

  System.out.println("startWrite :: Getting lock by writer "+id);
  /*while(!(!writerBusy||(!readerBusy)||(readerQueue.get(0).getPriority()<= Constants.DEFAULT_WRITER_PRIORITY))){
    wait();
    }*/
  writerBusy = true;

}

@Override
public int write(int id, int opTime, long beginTime) throws Exception {
  int tempMember = getElementNoForIDWriter(id);
  //	writerQueue.get(tempMember).setBeginTime(beginTime);

  objValue = id;
  writerQueue.get(tempMember).value = objValue;
  Thread.sleep(opTime);
  return objValue;
}

@Override
public synchronized void endWrite(int id, long endTime) throws Exception {
  int tempMember = getElementNoForIDWriter(id);

  if(!readerQueue.isEmpty()&&(readerQueue.get(0).getBeginTime()<writerQueue.get(tempMember).getBeginTime())){
    System.out.println("endWrite :: Increasing priority for reader "+readerQueue.get(0).getId());
    readerQueue.get(0).setPriority(readerQueue.get(0).getPriority()+1);
    System.out.println("endWrite :: Increased priority for reader "+readerQueue.get(0).getId()+ " to "+ readerQueue.get(0).getPriority());
  }
  writerQueue.get(tempMember).setEndTime(endTime);
  //Print into the event.log
  printerObj.write(writerQueue.get(tempMember));
  System.out.println(iterationCount++ +"\t"+objValue+"\t" +
      "W"+writerQueue.get(tempMember).getId()+"\t"+
      writerQueue.get(tempMember).beginTime+"-"+writerQueue.get(tempMember).endTime);
  System.out.flush();
  System.out.println("endWrite :: ended writing for writer "+id);

  writerQueue.remove(tempMember);
  //Setting priority when reader queue is not empty
  writerBusy = false;
  readerBusy = false;


  notifyAll();
}



private int getElementNoForID(int id){
  int tempMember = -1;
  long tempBeginTime= -1;
  for(int i =0; i< readerQueue.size(); i++){
    if(readerQueue.get(i).getId() ==id){
      /*tempMember = i;
	break;*/
      if(tempMember == -1){
	tempMember = i;
	tempBeginTime = readerQueue.get(i).getBeginTime();
      }else{
	if(tempBeginTime>readerQueue.get(i).getBeginTime()){
	  tempMember = i;
	  tempBeginTime = readerQueue.get(i).getBeginTime();
	}
      }
    }
  }
  return tempMember;
}

private int getElementNoForIDWriter(int id){
  int tempMember = -1;
  long tempBeginTime= -1;
  for(int i =0; i< writerQueue.size(); i++){
    if(writerQueue.get(i).getId() ==id){
      /*tempMember = i;
	break;*/
      if(tempMember == -1){
	tempMember = i;
	tempBeginTime = writerQueue.get(i).getBeginTime();
      }else{
	if(tempBeginTime>writerQueue.get(i).getBeginTime()){
	  tempMember = i;
	  tempBeginTime = writerQueue.get(i).getBeginTime();
	}
      }
    }
  }
  return tempMember;
}	

@Override
public synchronized void finish() throws Exception {
  noFinished++;
  if(totalClients == noFinished){
    printerObj.close();
    try {

      Registry reg = LocateRegistry.getRegistry(regPort);

      reg.unbind(/*readerObj.getMachineName(0, "server")*/Constants.REMOTE_SERVICE_NAME);

      UnicastRemoteObject.unexportObject(this, true); // Unexport, this will also remove us from the RMI runtime
      System.exit(0);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
}


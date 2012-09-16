//package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Start {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    //Start registry
    //Read from properties file and start server thread
    //Start programs on sites 
    ReaderObj readerObj = new ReaderObj();
    readerObj.load();
    String serverName = readerObj.getMachineName(0, "server");
    //Start server program on server machine
    String path = System.getProperty("user.dir");
    final long startTime = System.currentTimeMillis();
    try {
      //File file = new File("start.log");
      //PrintStream printStream  = new PrintStream(new FileOutputStream(file));
      //System.setOut(printStream);
      Process server;
      Runtime.getRuntime().exec("ssh "+serverName+" cd "+path+";java MyServer " + startTime);

      /*new Thread() {

	(non-Javadoc)
       * @see java.lang.Thread#run()

       @Override
       public void run() {
       MyServer.main(""+startTime);
       }

       }.start();*/
      int noOfReaders = readerObj.getNoOfReaders();
      int noOfWriters = readerObj.getNoOfWriters();

      BufferedReader in;
      BufferedReader serverIS;
      Process temp;
      temp = null;
      BufferedReader in1;

      for(int i=1; i<=noOfReaders; i++){
	String readerName = readerObj.getMachineName(i, Constants.READER);
	//final String procId = ""+i;
	Runtime.getRuntime().exec("ssh "+readerName+" cd "+path+";java MyClient "+i + " " + Constants.READER + " "+ startTime);
	/*new Thread() {

	  (non-Javadoc)
	 * @see java.lang.Thread#run()

	 @Override
	 public void run() {
	 MyClient.main(procId,Constants.READER, ""+startTime);
	 }

	 }.start();*/
      }

      for(int i=noOfReaders+1; i<=noOfReaders+noOfWriters; i++){
	String readerName = readerObj.getMachineName(i, Constants.WRITER);
	Runtime.getRuntime().exec("ssh "+readerName+" cd "+path+";java MyClient "+ i+ " " + Constants.WRITER + " "+ startTime);
	/*final String procId = ""+i;
	  new Thread() {

	  (non-Javadoc)
	 * @see java.lang.Thread#run()

	 @Override
	 public void run() {
	 MyClient.main(procId,Constants.WRITER, ""+startTime);
	 }

	 }.start();*/
      }

      /*in = new BufferedReader(new InputStreamReader(temp.getInputStream()));
	in1 = new BufferedReader(new InputStreamReader(temp.getErrorStream()));
	serverIS = new BufferedReader(new InputStreamReader(server.getErrorStream()));
	String c;
	String c1;
	boolean keepRunning = true;
	while (keepRunning) {
	if (in.ready() || in1.ready()) {
	c1 = serverIS.readLine();
      //c = in1.readLine();
      //System.out.print(c != null ? c+"\n" : "");
      System.out.print(c1 != null ? c1+"\n" : "");
      if (BYE.equals(c) || BYE.equals(c1)) {
      keepRunning = false;
      continue;
      }
      //c = c + "\n";
      c1 = c1 + "\n";
	}
	}*/
      //Thread.sleep(25000);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}

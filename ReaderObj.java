//package main;


import java.io.FileInputStream;
import java.util.*;

public class ReaderObj {

  public Properties readObj;

  public void load(){
    readObj = new Properties();
    try{
      readObj.load(new FileInputStream(Constants.INPUT_FILE));

    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public int getNoOfAccesses(){
    int n = 0;
    n = Integer.parseInt(readObj.getProperty("RW.numberOfAccesses").trim());
    return n;
  }

  public int getNoOfReaders(){
    int n = 0;
    n = Integer.parseInt(readObj.getProperty("RW.numberOfReaders").trim());
    return n;
  }

  public int getNoOfWriters(){
    int n = 0;
    n = Integer.parseInt(readObj.getProperty("RW.numberOfWriters").trim());
    return n;
  }

  public int getNoOfCycles(){
    int m = 0;
    m = Integer.parseInt(readObj.getProperty("M").trim());
    return m;
  }

  public int getReaderSleepTime(int readerNo){
    int sleepTime =0;
    sleepTime = Integer.parseInt(readObj.getProperty
	("RW.reader"+readerNo+".sleepTime").trim());
    return sleepTime;
  }

  public int getReaderOpTime(int readerNo){
    int sleepTime =0;
    sleepTime = Integer.parseInt(readObj.getProperty
	("RW.reader"+readerNo+".opTime").trim());
    return sleepTime;
  }

  public int getWriterSleepTime(int writerNo){
    int sleepTime =0;
    sleepTime = Integer.parseInt(readObj.getProperty
	("RW.writer"+writerNo+".sleepTime").trim());
    return sleepTime;
  }

  public int getWriterOpTime(int writerNo){
    int sleepTime =0;
    sleepTime = Integer.parseInt(readObj.getProperty
	("RW.writer"+writerNo+".opTime").trim());
    return sleepTime;
  }

  public String getMachineName(int processNo, String type){
    String machineName;
    String temp ="RW.";
    if(type.equalsIgnoreCase("server")){
      temp += type;
    }else{
      temp = temp + type + processNo;
    }
    machineName = readObj.getProperty(temp).trim();
    return machineName;
  }

  public int getRegistryPort(){
    int port= 1099; //Default for rmi
    port = Integer.parseInt(readObj.getProperty("Rmiregistry.port").trim());
    return port;
  }
}

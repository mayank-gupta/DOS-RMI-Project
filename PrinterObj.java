//package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.TreeSet;
class MyComparator implements Comparator<Info>{

	@Override
	public int compare(Info o1, Info o2) {
		// TODO Auto-generated method stub
		long startTime1 = o1.getBeginTime();
		long startTime2 = o2.getBeginTime();
		if(startTime1> startTime2){
			return 1;
		}else if(startTime1 < startTime2){
			return -1;
		}else{
			return 0;
		}
	}
	
}
public class PrinterObj {

	private BufferedWriter writerObj;
	private int sequenceNo;
	private TreeSet<Info> sorter;
	public PrinterObj() {
		// TODO Auto-generated constructor stub
		try {
			writerObj = new BufferedWriter(new FileWriter(Constants.OUTPUT_FILE));
			writerObj.write("ServiceSequence\tObjectValue\tAccessedBy\tOptTime\n");
			writerObj.flush();
			sequenceNo = 0;
			sorter = new TreeSet<Info>(new MyComparator());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			int count =0;
			for(Info i : sorter){
				count++;
				String type;
				if(i.getMachineType().equalsIgnoreCase(Constants.READER)){
					type = "R";
				}else{
					type = "W";
				}
				writerObj.write(count+"\t"+i.value+"\t"+type+i.getId()+"\t"
						+i.getBeginTime()+ "-"+ i.getEndTime()+"\n");
				writerObj.flush();
			}
			writerObj.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(Info tempObj){
		sorter.add(tempObj);
	}
}

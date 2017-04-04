package uteev.bk.ru;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

//abcdefhjigklmnopqrstuvwxyz
//abcdefhjigklmnopqrstuvwxyz
public class FutureTest {

	
	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		System.out.print(
				"Enter base derictori (e.g. /usr/local/jdk5.0/src): ");
		String directory = in.nextLine();
		System.out.print(
				"Enter keyword (e.g. volatile): ");
		String keyword = in.nextLine();
		
		MatchCounter counter = new MatchCounter(new File(directory), keyword);
		FutureTask<Integer> task = new FutureTask<Integer>(counter);
		Thread t = new Thread(task);
		t.start();
		try{
			System.out.println(task.get() + " matching files.");
		}
		catch(ExecutionException e)
		{
			e.printStackTrace();			
		}
		catch(InterruptedException e)
		{					
		}
	}			
}

class MatchCounter implements Callable<Integer>{
	private File direc;
	private String keyw;
	private int count;

	public MatchCounter(File dir, String key){
		direc = dir;
		keyw = key;
	}
	
	public Integer call(){
		count  = 0;
		try{
			File[] files = direc.listFiles();
			List<Future<Integer>> rezs = new ArrayList<Future<Integer>>();
			for(File f : files)
				if(f.isDirectory()){
					MatchCounter counter = new MatchCounter(f, keyw);
					FutureTask<Integer> task = new FutureTask<Integer>(counter);
					rezs.add(task);
					Thread t = new Thread(task);
					t.start();					
				}
				else{
					if(search(f)) count++;
				}
			for(Future<Integer> rez :rezs)
				try{
					count += rez.get();
				}
				catch(ExecutionException e){
					e.printStackTrace();
				}
		}
		catch(InterruptedException e){
			
		}
		return count;
	}
	
	public boolean search(File f){
		try{
			try(Scanner in = new Scanner(f)){
				boolean found = false;
				while( !found && in.hasNextLine() ){
					String line = in.nextLine();
					
					if(line.contains(keyw) ){
						System.out.println(line);
						found = true;
					}
				}
				return found;
			}
		}
		catch(IOException e){
			return false;
		}
	}
}

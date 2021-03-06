package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

/**
 *  this class provides the parsing of the Document
 * @author kimburke
 * @version 1.0
 * 
 *
 */

public class documentParser implements Runnable {
	
	private BlockingQueue <Shingle> queue;
	private String file;
	private int shingleSize,k;
	private Deque<String>Buffer=new LinkedList<>();
	
	
	public documentParser(String f, BlockingQueue<Shingle> q, int ss, int k) {
		
		this.file= f;
		this.queue = q;
		this.shingleSize= ss;
		this.k=k;
	}

	@Override
	public void run() {
		
		try {
			
				//Buffered Reader
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line= null;
				
				//loop through all the lines in the file
				while((line = br.readLine())!=null){
					//convert to upperacase
					String uLine= line.toUpperCase();
					//splits the lines into words
					String [] words = uLine.split(" ");
					//adds single words to the buffer
					addWordsToBuffer(words);
					//calls getNextShingle
					Shingle s = getNextShingle();
					
					if(s== null) {
						//ignores empty shingles
						continue;		
					}
					
					queue.put(s);
				}//while
				
				queue.put(new Shingle(0,0));
				//calles flush buffer method
				flushBuffer();
				//closes the BufferedReader
				br.close();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
			}//run
			

	private void addWordsToBuffer(String[] words) {
		// TODO Auto-generated method stub
		for(String s: words) {
			Buffer.add(s);
		}
	}

	private void flushBuffer() throws InterruptedException {
		//flushes the buffer
		
		while(Buffer.size() > 0) {
			Shingle s = getNextShingle();
			
			if(s != null) {
				queue.put(s);	
			}
			else {
				queue.put(new Poison(0,0));
			}
		}//while
		
	}//flushBuffer

	private Shingle getNextShingle() {
		//gets next shingle
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		
		while(counter < shingleSize) {
			
			if(Buffer.peek()!=null) 
				sb.append(Buffer.poll());
			
			counter++;
		}
		
		if(sb.length()>0) {
			
			int docID = 0;
			return (new Shingle(docID,sb.toString().hashCode()));
		}
		
		else {
			return null;
		}
		
	}//getNextShingle
}
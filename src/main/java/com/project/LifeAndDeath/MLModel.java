package com.project.LifeAndDeath;

/**
 * @Summary   : 
 * @Package : 
 * @FileName : test.java
 * @Author : Yang TaeIl
 * @date : 2018. 10. 15.  
 * 
 */

/**
 * 
 * @Package : 
 * @FileName : test.java
 * @Author : Yang TaeIl
 * @date : 2018. 10. 15. 
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
public class MLModel {
	
	static int ROW = 1;
	static int FEATURE = 361;
	
	
	public long model1() throws IOException{
	   
	    float[][] testInput = {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
	        
	    
	    //load the model bundle
	    try(SavedModelBundle b = SavedModelBundle.load("/tmp/fromPython1", "serve")){
	            
	        //create a session from the Bundle
	        Session sess = b.session();
	    
	        //create an input Tensor
	        Tensor x = Tensor.create(testInput);
	        Tensor bo = Tensor.create(true);
	        //run the model and get the result
	        long y[] = sess.runner()
	                .feed("x", x)
	                .feed("b",bo)
	                .fetch("h")
	                .run()
	                .get(0).copyTo(new long[ROW]);
	        sess.close();
	        b.close();
	        System.out.println("다 닫힘");
	        return y[0];
	    }
	    
	}
	
	
	public static void getDataSize(String filePath) throws IOException {
		try {
			//read csv data file
			File csv = new File(filePath);
			BufferedReader br = new BufferedReader(new FileReader(csv));
			String line = "";
			String[] field = null;
			
			while((line=br.readLine())!=null) {
				field = line.split(",");
				ROW++;
			}
			
			FEATURE = field.length;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * csv 파일 데이터를 행렬로 옮김
	 * @param filePath
	 * @param mtrx
	 * @throws IOException
	 */
	public static void csvToMtrx(String filePath, float[][] mtrx) throws IOException {
		try {
			//read csv data file
			File csv = new File(filePath);
			BufferedReader br = new BufferedReader(new FileReader(csv));
			String line = "";
			String[] field = null;
			
			for(int i=0; i<mtrx.length; i++) {
				if((line=br.readLine())!= null) {
					field = line.split(",");
					for(int j=0; j<field.length; j++) {
						mtrx[i][j] = Float.parseFloat(field[j]);
					}
				}
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("여기");
		}
	}

	/**
	 * 행렬 값 확인용 출력
	 * @param mtrx
	 */
	public static void printMatrix(float[][] mtrx) {
		System.out.println("============ARRAY VALUES============");
		for(int i=0; i<mtrx.length; i++) {
			if(i==0)
				System.out.print("[");
			else
				System.out.println();
			for(int j =0; j<mtrx[i].length; j++) {
				System.out.print("["+mtrx[i][j]+"]");
			}
		}
		System.out.println("]");
	}
}
package invoker;

import java.io.IOException;
import java.util.ArrayList;


public class sgx_invoker{
//export LD_LIBRARY_PATH=/home/xidian/Development/SGX-project/source-code/NaiveTest/bin
	//public native int varargsMethod( int... no,float... fl,double... dl,long... lo );
  public static final int N=30;
	public native int init();
	public native int destroy();
	public native int commit(long counter, int[] intArray, int intTail, double[] doubleArray, int doubleTail,float[] floatArray, int floatTail, long[] longArray, int longTail, char[] charArray,int charTail, byte[] byteArray,int byteTail);
  //  static {System.loadLibrary("/home/xidian/Development/SGX-project/source-code/NaiveTest/src/edu/xidian/libSGX.so");}  
   
	static {
		try{
			System.out.println("invoker:"+System.getProperty("java.library.path"));
			System.loadLibrary("SGX");
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			//System.out.println("invoker"+System.getProperty("java.library.path"));
			e.printStackTrace();
		}
	}  

    //TODO: may potentially reduce the performance, can be improved later
	//ArrayList<Object> objects = null;
	Object[] objArray = new Object[N];
	int objTail= 0;

	int[] intArray = new int[N];
	int intTail = 0;
	
	double[] doubleArray = new double[N];
	int doubleTail = 0;
	
	float[] floatArray = new float[N];
	int floatTail = 0;
	
	long[] longArray = new long[N];
	int longTail = 0;
	
	char[] charArray = new char[N];
	int charTail = 0;
	
	byte[] byteArray = new byte[N];
	int byteTail = 0;
	
	long counter = -1;
	
	public sgx_invoker(){
		//objects = new ArrayList<Object>();
	}
	
	public void clear(){
		intTail = 0;
		doubleTail = 0;
		floatTail = 0;
		longTail = 0;
		charTail = 0;
		byteTail = 0;
		//objects.clear();
	}
	
	public void add(Object o){
		if(o==null)
			intArray[intTail++]=0;
		else
			intArray[intTail++]=o.hashCode();
		//objArray[objTail++] = o;
	}
	
	public void add(boolean o){
		if(o==true)
			intArray[intTail++]=1;
		else
			intArray[intTail++]=0;
		//objArray[objTail++] = o;
	}
	
	public void add(int o){
		intArray[intTail++] = o;
	}
	
	public void add(double o){
		doubleArray[doubleTail++] = o;
	}
	public void add(float o){
		floatArray[floatTail++] = o;
	}
	
	public void add(long o){
		longArray[longTail++] = o;
	}
	public void add(char o){
		charArray[charTail++] = o;
	}
	public void add(byte o){
		byteArray[byteTail++] = o;
	}
	public void setCounter(long counter){
		this.counter = counter;
	}
	
	public boolean initenclave(){
		long startTime = System.currentTimeMillis();
		if(1==init()){
	 		long endTime   = System.currentTimeMillis();
     			long totalTime = (endTime - startTime);
     			System.out.println("init enclave running time= "+totalTime+" Millis");
			return true;
		}
		else
			return false;
	}
	
	public boolean closeenclave(){
		if(0==destroy())
		return true;
		else
		return false;
	}
	//sgx_invoker invo2= new sgx_invoker();

	public boolean call(){ 
		
		//int[] intArray; double[] doubleArray; float[] floatArray; long[] longArray;
//		ArrayList<Integer> intArray = new ArrayList<Integer>();
//		ArrayList<Double> doubleArray = new ArrayList<Double>();
//		ArrayList<Float> floatArray = new ArrayList<Float>();
//		ArrayList<Long> longArray = new ArrayList<Long>();
//		ArrayList<String> charArray = new ArrayList<String>();
//		for(Object obj: objects){
//			if(obj instanceof Integer)
//				intArray.add((Integer)obj);
//				//c_call(((Integer)obj).intValue());
//			if(obj instanceof Double)
//				c_call(((Double)obj).doubleValue());
//			if(obj instanceof Float)
//				c_call(((Float)obj).floatValue());
//			if(obj instanceof Long)
//				c_call(((Long)obj).longValue());
//			String srt="abc";
//			//...
//		}
//		//int, double, float, boolean, long
////		objects.add(new Integer(x));
////		objects.add(args);
////		objects.add("S2");
////		objects.add(2.5);
//		//<invokeSGX(int, int int, double)>
//		//Object[] objectArray = (Object[])objects.toArray();
//		int[] intPrimArray = new int[intArray.size()];
//		for(int i = 0; i< intA public static void main(String[] args)
//		rray.size();i++){
//		    intPrimArray[i] = intArray.get(i);
//		}
		int ret = commit(counter,intArray,intTail,doubleArray,doubleTail,floatArray,floatTail,longArray,longTail,charArray,charTail,byteArray,byteTail);
		//int ret = 	commit(0,new int[]{1,1,1,4,5,6,7,8,9,0}, N,new double[]{0.0},0,new float[]{0},0,new long[]{0},0,new char[]{0},0);
		
		if(ret == 1)
			return true;
		else if(ret == 0)
			return false;
		else{
			
			//throw new Exception("error");
			System.out.println("ret:"+ret);
			System.out.println("error");
			System.out.println("ret");
			System.exit(1);
		}
		return false;
		
	//return commit(counter,intArray,intTail,doubleArray,doubleTail,floatArray,floatTail,longArray,longTail,charArray,charTail);

	
	}
//public static void main(String[] args)
//{
//	 sgx_invoker invo= new sgx_invoker();
//    invo.commit(0,new int[]{1,1,1,4,5,6,7,8,9,0},N,new double[]{0.0},0,new float[]{0},0,new long[]{0},0,new char[]{0},0,new byte[]{0},0);

//};

	
}

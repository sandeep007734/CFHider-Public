
/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Eric Bodden
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import soot.ArrayType;
import soot.Body;
import soot.BodyTransformer;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.G;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.SootMethod;
import soot.Transform;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Grimp;
import soot.jimple.*;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.AbstractInterfaceInvokeExpr;
import soot.jimple.internal.JCmpExpr;
import soot.jimple.internal.JCmpgExpr;
import soot.jimple.internal.JCmplExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JNeExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.FlowSet;
import soot.util.Chain;

public class LoggerMain100{

	static long counter = 0;
	static Writer indexWriter=null;
    final static double ratio = 0.5; 
	
	static Writer getWriter(){
		String filename = "/tmp/counter";
	    if(indexWriter==null){
			try{
				indexWriter = new PrintWriter(filename, "UTF-8");

			} catch (IOException e) {
			   // do something
			}
	    }
		return indexWriter;
	}
	
	static void closeWriter(){
		if(indexWriter !=null){
			try {
				indexWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			indexWriter = null;
		}
	}
	
	public static void indexwriter(String conent) {
		String file="/tmp/SGXindex";
		BufferedWriter out = null;
		try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file, true)));
					out.write(conent+"\n");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	
	public static void main(String[] args) {

		PackManager.v().getPack( "jtp" ).add( 
				new Transform("jtp.LogInserter", new BodyTransformer() {
				      protected void internalTransform(Body body, String phase, Map options) {
				        new LogInserter(body);
				      }
				    }) );
		
		
		
//		StringBuffer sb = new StringBuffer();
//		for(int i = 0; i < args.length; i++){
//		 sb. append(args[i]);
//		}
//		String s = sb.toString();
//		G.v().out.println(String.valueOf(s));
		
		soot.Main.main(args);
		
		
		closeWriter();
	}

	public static class LogInserter{
		
		String declaredClassName = "";
		public LogInserter(Body aBody) {
			declaredClassName = aBody.getMethod().getDeclaringClass().toString();
			String declaredFunction = aBody.getMethod().toString();
			G.v().out.println("start insertting at class ..."+declaredClassName+"function..."+declaredFunction);
			G.v().out.println("*****declaredClassName******"+declaredClassName);
			if(declaredClassName.contains("sgx_invoker")){
				G.v().out.println("Encounters the Transformer class ...skip...");
				return;
			}
			if(declaredClassName.contains("TotalOrderPartitioner")){
				G.v().out.println("Encounters the Transformer class ...skip...");
				return;
			}
			//declaredClassName.contains("PagerankNaive$MapStage1")||
			if(declaredClassName.contains("PagerankNaive$RedStage1")){
				G.v().out.println("Encounters the Transformer class ...skip...");
				return;
			}
			
//			if(declaredClassName.contains("spec.harness.")){
//				G.v().out.println("Encounters the Transformer class ...skip...");
//				return;}
//			if(declaredClassName.contains("spec.io.")){
//				G.v().out.println("Encounters the Transformer class ...skip...");
//				return;}
//			if(declaredClassName.contains("spec.reporter.")){
//				G.v().out.println("Encounters the Transformer class ...skip...");
//				return;}
			if(declaredClassName.contains("spec.harness.CommandLineParser")){
				G.v().out.println("Encounters the spec.harness.CommandLineParser class ...skip...");
				return;
			}
			if(declaredClassName.contains("spec.benchmarks.check.PepTest")){
				G.v().out.println("Encounters the spec.benchmarks.check.PepTest class ...skip...");
				return;
			}

			//get the counter value from the counter file(/tmp/counter) 
			 File file = new File("/tmp/counter");
			 if (file.exists()) {
				 System.out.println("===============================file exists");
				 //read counter value from the file(/tmp/counter)
				 BufferedReader reader = null;  
			        try {  
			            //System.out.println("以行为单位读取文件内容，一次读一整行：");  
			            reader = new BufferedReader(new FileReader(file));  
			            String tempString = null;  
			            int line = 1;  
			            // 一次读入一行，直到读入null为文件结束  
			            while ((tempString = reader.readLine()) != null) {  
			                // 显示行号  
			                //System.out.println("+++++++++++++++++++++++=line " + line + ": " + tempString);  
			                line++;  
			                counter=Long.valueOf(tempString);
			            }  
			            reader.close();
			        } catch (IOException e) {  
			            e.printStackTrace();  
			        } finally {  
			            if (reader != null) {  
			                try {  
			                    reader.close();  
			                } catch (IOException e1) {  
			                }  
			            }  
			        }  
			 }

	        PatchingChain units = aBody.getUnits();
	          
	        //G.v().out.println("units:"+units.toString());
	          
	          
	          //Chain<Trap> traps = aBody.getTraps();
	          Iterator<Unit> staticScanIt = null;
	          Unit currStmt = null;

	         // Local temp = Jimple.v().newLocal("temp", IntType.v());
	         // aBody.getLocals().add(temp);
	          //Map<Unit, Unit> ifStmtMap = new HashMap<Unit, Unit>();

	          ArrayList<Local> localArray = new ArrayList<Local>();
	          Iterator<Local> locali = aBody.getLocals().iterator();
	          while(locali.hasNext()){
	          	localArray.add((Local)locali.next());
	          }
	
	 		 //G.v().out.println("localarray:"+localArray.toString());

	          //Lo
	          Local branchInvokeResultLocal = Jimple.v().newLocal("branchInvokeResult", BooleanType.v());

	          Local invokerLocal = Jimple.v().newLocal("sgxInvoker", RefType.v("invoker.sgx_invoker"));
         	
	          aBody.getLocals().add(branchInvokeResultLocal);  //1.insert local branchInvokeResultLocal
	          aBody.getLocals().add(invokerLocal);             //2.insert local invokerLocal
	          staticScanIt = units.snapshotIterator();
	          boolean initInvokerLocal = false;

	          
	          //boolean initDataLogger = false;
	          //boolean closeBranchLogger = false;
	          //ArrayList<Value> paramList = new ArrayList<Value>();
	          //int loggedParamNo = 0;
	          //Value iteratorValue = null;
	          //char funcType = getFunctionType(declaredFunction);
	          HashSet<Value> identifiedLocal = new HashSet<Value>();
	          
	          while (staticScanIt.hasNext()) {
	        	  
		            currStmt = (Unit)staticScanIt.next();
		            
		            G.v().out.println("say:"+currStmt.toString());

		            if(currStmt instanceof IdentityStmt){
		            	
		            	identifiedLocal.add(((IdentityStmt)currStmt).getLeftOp());
		            	
		            	//G.v().out.println("IdentityStmt-Say:"+(currStmt).toString()+"--------"+((IdentityStmt)currStmt).getLeftOp().toString());

		            	
		            	continue;
		            }
		            
		            
		            
		            if(!initInvokerLocal){
		            	  initLocal(localArray, units, currStmt,identifiedLocal);
		            	  
		            	  initLogger(invokerLocal, units, currStmt, "invoker.sgx_invoker", declaredFunction);
		            	  
		            	  initInvokerLocal = true;
		            }
		            
		            if(currStmt instanceof AssignStmt){
		            	
		            
		            	if(!(declaredClassName.contains("TotalOrderPartitioner"))){
			            	insertFakebranch(invokerLocal,branchInvokeResultLocal,units, currStmt,localArray, staticScanIt);

		    			}
		            }
		            
		           /*
		            ArrayList<Local> localnewArray = new ArrayList<Local>();
			          Iterator<Local> newlocali = aBody.getLocals().iterator();
			          
			          while(newlocali.hasNext()){
			        	  Local templocal=(Local)newlocali.next();
			        	  while(locali.hasNext())
			        	  {
			        		  if(templocal.equals((Local)locali.next()))
			        		  {
			        			  G.v().out.println(templocal);
			        		  }
			        	  }
			          	
			          }*/
			          //initLocal(localnewArray, units, currStmt);
		            //skip non-exception identity Stmt
//		            if (currStmt instanceof IdentityStmt){
//		            	Value value = ((IdentityStmt)currStmt).getRightOp();
//		            	if(!(value instanceof CaughtExceptionRef)){
//		            		IdentityStmt is = (IdentityStmt) currStmt;
//							Value rightOp = is.getRightOp();
//							if (rightOp instanceof ParameterRef && loggedParamNo<2){
//								G.v().out.println("Identity Stmt rightOp is "+ rightOp.toString());
//								paramList.add(is.getLeftOp());
//								if(loggedParamNo==1&&funcType == 'r'){
//									iteratorValue = is.getLeftOp();
//								}
//								loggedParamNo++;
//							}
//			            	continue;
//		            	}
//		            }
		            
//		            if(!initBranchLogger){
//		            	//init BranchLogger
//		            	if(isMapReduceFunction(declaredFunction)){
//		            	  aBody.getLocals().add(inputLoggerLocal); 
//		            	  initLogger(inputLoggerLocal, units, currStmt, "edu.xidian.DataLogger", "input", declaredFunction);
//		            	  aBody.getLocals().add(outputLoggerLocal); 
//		            	  initLogger(outputLoggerLocal, units, currStmt, "edu.xidian.DataLogger", "output", declaredFunction);
//		            	  initDataLogger = true;
//		            	  int paramIndex = 0;
//		            	  if(funcType == 'm'){
//			            	  for(Value param: paramList){
//									logDataBefore(param, inputLoggerLocal, units, currStmt);
//							  	paramIndex++;
//			            	  }
//		            	  }
//		            	  if(funcType == 'r'){ //in reduce function, the input 
//								logDataBefore(paramList.get(0), inputLoggerLocal, units, currStmt);
//		            	  }
//		            	}
//		            	initLogger(branchResultLocal, units, currStmt, "edu.xidian.BranchLogger", "branch", declaredFunction);
//		            	initLogger(invokeLoggerLocal, units, currStmt, "edu.xidian.InvokeLogger", "invoke", declaredFunction);
//		            	initBranchLogger = true;
//		            }
//		            if(funcType == 'r'){
//						if(currStmt instanceof AssignStmt){
//							AssignStmt as = (AssignStmt)currStmt;
//							if(as.getRightOp() instanceof AbstractInterfaceInvokeExpr){
//								AbstractInterfaceInvokeExpr aiie = (AbstractInterfaceInvokeExpr)as.getRightOp();
//								if(aiie.getBase().toString().equals(iteratorValue.toString())&&aiie.getMethod().getSignature().contains("java.lang.Iterable: java.util.Iterator iterator()")){
//									iteratorValue = as.getLeftOp();
//								}
//								if(aiie.getBase().toString().equals(iteratorValue.toString())&&aiie.getMethod().getSignature().contains("java.util.Iterator: java.lang.Object next()")){
//									iteratorValue = as.getLeftOp();
//								}
//							}
//							if(as.getRightOp() instanceof CastExpr){
//								CastExpr ce = (CastExpr)as.getRightOp();
//								if(ce.getOp().toString().equals(iteratorValue.toString())){
//									iteratorValue = as.getLeftOp();
//									logDataAfter(iteratorValue, inputLoggerLocal, units, currStmt, 1, declaredFunction);
//								}
//							}
//						}
//		            }
		            //TODO: add parameter to the functions
//		            if (currStmt instanceof DefinitionStmt) {
//						DefinitionStmt ds = (DefinitionStmt) currStmt;
//						Value rightOp = ds.getRightOp();
//						if (rightOp instanceof ParameterRef){staticScanIt
//							G.v().out.println("Definition Stmt rightOp is "+ rightOp.toString());
//							int paramIndex = ((ParameterRef)rightOp).getIndex();
//							if(initDataLogger && paramIndex<2){
//								//Value leftOp = ds.getLeftOp();				
//								logData(rightOp, inputLoggerLocal,units, currStmt, paramIndex);
//							}
//							
//						}
//					}
		            
//		            if(currStmt instanceof InvokeStmt){
//		            	SootMethod method = ((InvokeStmt)currStmt).getInvokeExpr().getMethod();	
//		            	String methodName = method.toString();
//		            	G.v().out.println("invoke stmt method is "+method.toString());
//		        		if(method.toString().equals("<java.lang.System: void exit(int)>")){
//			            	insertCloseLogger(branchResultLocal, "edu.xidian.BranchLogger", units, currStmt);
//			            	if(initDataLogger){
//			            		insertEnter(inputLoggerLocal, units, currStmt);
//				            	insertCloseLogger(inputLoggerLocal, "edu.xidian.DataLogger", units, currStmt);
//			            		insertEnter(outputLoggerLocal, units, currStmt);
//				            	insertCloseLogger(outputLoggerLocal, "edu.xidian.DataLogger", units, currStmt);
//				            	insertCloseLogger(invokeLoggerLocal, "edu.xidian.InvokeLogger", units, currStmt);
//
//			            	}
//		        		}else
//		        		if(methodName.contains("Context: void write(")&&
//		        				isMapReduceFunction(declaredFunction)){ //TODO: temporary solution
//		        			InvokeExpr ie = ((InvokeStmt)currStmt).getInvokeExpr();
//		    				List<Value> parameters = ie.getArgs();
//		    				if(parameters.size()==2){
//		    					int paramIndex=0;
//			    				for(Value v: parameters){
//				        			logDataBefore(v, outputLoggerLocal,units, currStmt);
//				        			paramIndex++;
////			                        Type type = v.getType();
////			    					if(type instanceof RefLikeType){
////			    						searchConstraint(Exp2String(v,false),true);
////			    					}
//			    				}
//		    				}
//		        		}else{// normal function invoke
//		        			InvokeExpr ie = ((InvokeStmt)currStmt).getInvokeExpr();
//		    				List<Value> parameters = ie.getArgs();
//		    				logInvokeBefore(parameters, invokeLoggerLocal,units, currStmt,ie.getMethod().getSignature());
//		        			logInvokeAfter(parameters, invokeLoggerLocal, units, currStmt, ie.getMethod().getSignature());
//		        		}
//		            }
//					if(currStmt instanceof AssignStmt){
//						AssignStmt as = (AssignStmt)currStmt;
//						if(as.getRightOp() instanceof InvokeExpr){
//							InvokeExpr ie = (InvokeExpr)(as.getRightOp());
//							Value returnVal = as.getLeftOp();
//							
//		    				List<Value> parameters = ie.getArgs();
//		    					logInvokeBefore(parameters, invokeLoggerLocal,units, currStmt,ie.getMethod().getSignature());
//			        			parameters.add(returnVal);
//		    					logInvokeAfter(parameters, invokeLoggerLocal, units, currStmt,ie.getMethod().getSignature());
//			        			
//			        			//logInvokeAfter( Arrays.asList(returnVal), invokeLoggerLocal, units, currStmt);
//						}
//					}
//		            if(currStmt instanceof RetStmt||currStmt instanceof ReturnStmt||currStmt instanceof ReturnVoidStmt){
//			            	G.v().out.println("return stmt is "+currStmt.toString());
//			            	insertCloseLogger(branchResultLocal, "edu.xidian.BranchLogger",units, currStmt);
//			            	if(initDataLogger){
//			            		insertEnter(inputLoggerLocal, units, currStmt);
//				            	insertCloseLogger(inputLoggerLocal, "edu.xidian.DataLogger", units, currStmt);
//				            	insertEnter(outputLoggerLocal, units, currStmt);
//				            	insertCloseLogger(outputLoggerLocal, "edu.xidian.DataLogger", units, currStmt);
//				            	insertCloseLogger(invokeLoggerLocal, "edu.xidian.InvokeLogger", units, currStmt);
//			            	}
//		            }

		            //G.v().out.println("currStmt is "+ currStmt.toString());
					//G.v().out.println("currStmt is  "+currStmt.toString()+",hash is "+currStmt.hashCode());

		            if(currStmt instanceof IfStmt){
		            		//if(!ifStmtMap.containsKey(currStmt)){ //insert logger
			            		
			            		//G.v().out.println("IfCondition is "+currStmt.toString());
		            	
		            	
			            replaceBranch(invokerLocal,branchInvokeResultLocal,units, currStmt,localArray);
			            		//units.insertBefore(invokeStmt, currStmt);
			            		//ifStmtMap.put(currStmt, firstInsertedStmt);
		            		//}
		            		// for a if stmt that points to original target, should check if the original target has added
		            		// the logger, if true, update the target.
//		            		Unit originalTarget = ((IfStmt)currStmt).getTarget();
//		            		if(ifStmtMap.containsKey(originalTarget)){
//		            			((IfStmt)currStmt).setTarget(ifStmtMap.get(originalTarget));
//		            		}
		            }
		            	
		            if(declaredFunction.contains("void main(java.lang.String[])")){
		            if(currStmt.toString().contains("return"))
		            {
		            	//G.v().out.print("asjfdbashklfbhsak"+currStmt.toString());
		            	closeenclave(invokerLocal, units, currStmt, "invoker.sgx_invoker");
		            }
		            }
		            
		            	// for any stmt go to original if stmt, should 
//		            	// redirect to new stmt according to the ifStmtMap
//		            if(currStmt instanceof GotoStmt){
//		            		Unit originalTarget = ((GotoStmt)currStmt).getTarget();
//		            		if(ifStmtMap.containsKey(originalTarget)){
//		            			((GotoStmt)currStmt).setTarget(ifStmtMap.get(originalTarget));
//		            		}
//		            }
	          }

	          // write the max counter value in "/tmp/counter"
	          
	          try{
	        	  BufferedWriter out =new BufferedWriter(new FileWriter("/tmp/counter"));
	        	  out.write(String.valueOf(counter));
	        	  out.close();
				}catch(IOException e){
					e.printStackTrace();
				}
		}

		private void initLocal(ArrayList<Local> localList,
			PatchingChain units, Unit currStmt, HashSet<Value> identifiedLocal) {
			
			//G.v().out.println("initLocal-Say:"+currStmt.toString());
			
			soot.jimple.AssignStmt stmt = null; 
			for(Local l: localList){
				if(identifiedLocal.contains(l) //TODO: remove ref like type|| l.getType() instanceof RefLikeType
						)
					continue;
				Type t = l.getType();
				
				//G.v().out.println("t.toString()-Say:"+t.toString());
				
				if(t instanceof RefLikeType){
				    stmt = soot.jimple.Jimple.v().newAssignStmt(l, NullConstant.v());
				}
				if(t instanceof IntType){
				    stmt = soot.jimple.Jimple.v().newAssignStmt(l, IntConstant.v(0));
				}
				if(t instanceof DoubleType){
				    stmt = soot.jimple.Jimple.v().newAssignStmt(l, DoubleConstant.v(0));
				}
				if(t instanceof FloatType){
				    stmt = soot.jimple.Jimple.v().newAssignStmt(l, FloatConstant.v(0));
				}
				if(t instanceof LongType){
				    stmt = soot.jimple.Jimple.v().newAssignStmt(l, LongConstant.v(0));
				}
				if(t instanceof BooleanType){
				    stmt = soot.jimple.Jimple.v().newAssignStmt(l, IntConstant.v(0));
				}
				if(t instanceof ShortType){
				    stmt = soot.jimple.Jimple.v().newAssignStmt(l, IntConstant.v(0));
				}
				if(t instanceof CharType){
				    stmt = soot.jimple.Jimple.v().newAssignStmt(l, IntConstant.v(0));
				}
				if(t instanceof ByteType){
				    stmt = soot.jimple.Jimple.v().newAssignStmt(l, IntConstant.v(0));
				}
				units.insertBefore(stmt, currStmt);						
			}
				
		}
		private void initLocal(ArrayList<Local> localList,
				PatchingChain units, Unit currStmt) {
				
				//G.v().out.println("initLocal-Say:"+currStmt.toString());
				
				soot.jimple.AssignStmt stmt = null; 
				for(Local l: localList){
					
					Type t = l.getType();
					
					//G.v().out.println("t.toString()-Say:"+t.toString());
					
					if(t instanceof RefLikeType){
					    stmt = soot.jimple.Jimple.v().newAssignStmt(l, NullConstant.v());
					}
					if(t instanceof IntType){
					    stmt = soot.jimple.Jimple.v().newAssignStmt(l, IntConstant.v(0));
					}
					if(t instanceof DoubleType){
					    stmt = soot.jimple.Jimple.v().newAssignStmt(l, DoubleConstant.v(0));
					}
					if(t instanceof FloatType){
					    stmt = soot.jimple.Jimple.v().newAssignStmt(l, FloatConstant.v(0));
					}
					if(t instanceof LongType){
					    stmt = soot.jimple.Jimple.v().newAssignStmt(l, LongConstant.v(0));
					}
					if(t instanceof BooleanType){
					    stmt = soot.jimple.Jimple.v().newAssignStmt(l, IntConstant.v(0));
					}
					if(t instanceof ShortType){
					    stmt = soot.jimple.Jimple.v().newAssignStmt(l, IntConstant.v(0));
					}
					if(t instanceof CharType){
					    stmt = soot.jimple.Jimple.v().newAssignStmt(l, IntConstant.v(0));
					}
					if(t instanceof ByteType){
					    stmt = soot.jimple.Jimple.v().newAssignStmt(l, IntConstant.v(0));
					}
					units.insertBefore(stmt, currStmt);						
				}
					
			}

		private boolean isMapReduceFunction(String declaredFunction){
			char functionType = getFunctionType(declaredFunction);
			if(functionType=='m'||functionType =='r')
				return true;
			else
				return false;
		}
		
		private char getFunctionType(String declaredFunction){
			if(declaredFunction.contains("TokenizerMapper: void map(java.lang.Object,org.apache.hadoop.io.Text,org.apache.hadoop.mapreduce.Mapper$Context)"))
				return 'm';
			if(declaredFunction.contains("IntSumReducer: void reduce(org.apache.hadoop.io.Text,java.lang.Iterable,org.apache.hadoop.mapreduce.Reducer$Context)"))
				return 'r';
			else
				return 'f';			
		}

		private void insertCloseLogger(Local loggerLocal,String loggerClassSig,
				PatchingChain units, Unit currStmt) {
			SootMethod toCall = Scene.v().getMethod
				      ("<"+loggerClassSig+": void close()>");
			Stmt invokeStmt = Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(
					loggerLocal, toCall.makeRef(),new ArrayList()));
			units.insertBefore(invokeStmt,currStmt);
		}

		private void initLogger(Local loggerLocal, PatchingChain units,
				Unit currStmt, String className, String declaredFunction) {
			
			//G.v().out.println("initLogger-Say:"+currStmt.toString()+"\n");
			
		    soot.jimple.NewExpr sootNew = soot.jimple.Jimple.v().newNewExpr(RefType.v(className));

		    soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(loggerLocal, sootNew);
			//Expr rhs = Jimple.v().newInstanceOfExpr(transLocal, RefType.v("java.util.ArrayList"));
			units.insertBefore(stmt, currStmt);
			
			
			G.v().out.println("*******************"+className);
			
			SpecialInvokeExpr newTrans = Jimple.v().newSpecialInvokeExpr(loggerLocal,
					Scene.v().getMethod("<invoker.sgx_invoker: void <init>()>").makeRef(),
					Arrays.asList());
			soot.jimple.Stmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(newTrans);
			units.insertBefore(invokeStmt, currStmt);	
			
			//if(declaredFunction.contains("void main(java.lang.String[])")){
			SootMethod toCall = Scene.v().getMethod
				      ("<invoker.sgx_invoker: boolean initenclave()>");
			Stmt newInvokeStmt = Jimple.v().newInvokeStmt(
					Jimple.v().newVirtualInvokeExpr
			           (loggerLocal, toCall.makeRef(), Arrays.asList()));
			units.insertBefore(newInvokeStmt, currStmt);
			
			//}
			
			//G.v().out.println("units-Say:"+units.toString()+"\n");
		}
		
		private void closeenclave(Local loggerLocal, PatchingChain units,
				Unit currStmt, String className) {
			
			SootMethod toCall = Scene.v().getMethod
				      ("<invoker.sgx_invoker: boolean closeenclave()>");
			Stmt newInvokeStmt = Jimple.v().newInvokeStmt(
					Jimple.v().newVirtualInvokeExpr
			           (loggerLocal, toCall.makeRef(), Arrays.asList()));
			units.insertBefore(newInvokeStmt, currStmt);
			
			
			
			//G.v().out.println("units-Say:"+units.toString()+"\n");
		}
		
		private InvokeStmt prepareInsertStmt(Value loggedValue, Local loggerLocal, String className){
			Type vType = loggedValue.getType();
			//G.v().out.println("test type is ..."+vType.toString());

			SootMethod toCall = null;
			if(vType instanceof IntType || vType instanceof BooleanType || vType instanceof ShortType){
				 toCall = Scene.v().getMethod
					      ("<"+className+": void add(int)>");}
			else if(vType instanceof DoubleType){
				 toCall = Scene.v().getMethod
					      ("<"+className+": void add(double)>");}
			else if(vType instanceof FloatType){
				 toCall = Scene.v().getMethod
					      ("<"+className+": void add(float)>");}
			else if(vType instanceof LongType){
				 toCall = Scene.v().getMethod
					      ("<"+className+": void add(long)>");}
			else if(vType instanceof CharType){
				 toCall = Scene.v().getMethod
					      ("<"+className+": void add(char)>");}
			else if(vType instanceof ByteType){
				 toCall = Scene.v().getMethod
					      ("<"+className+": void add(byte)>");}
		 else
			{
				toCall = Scene.v().getMethod
					      ("<"+className+": void add(java.lang.Object)>");
			}
			InvokeStmt newInvokeStmt = Jimple.v().newInvokeStmt(
						Jimple.v().newVirtualInvokeExpr
				           (loggerLocal, toCall.makeRef(), Arrays.asList(loggedValue)));
				 return newInvokeStmt;
		}
		
		private Unit insertEnter(Local dataLoggerLocal, PatchingChain units, Unit currStmt){
			SootMethod toCall = Scene.v().getMethod
		      ("<edu.xidian.DataLogger: boolean logRawString(java.lang.String)>");
			//inserted code: loggerLocal.logString("\n");
			Stmt newInvokeStmt = Jimple.v().newInvokeStmt(
			Jimple.v().newVirtualInvokeExpr
	           (dataLoggerLocal, toCall.makeRef(), Arrays.asList(StringConstant.v("\n"))));
			units.insertBefore(newInvokeStmt, currStmt);
			return newInvokeStmt;
		}
		//*******
		private Unit logDataBefore(Value loggedValue, Local dataLoggerLocal, PatchingChain units, Unit currStmt) {
			Stmt insertedStmt = prepareInsertStmt(loggedValue, dataLoggerLocal,"edu.xidian.DataLogger");
			units.insertBefore(insertedStmt, currStmt);
//			toCall = Scene.v().getMethod
//				      ("<edu.xidian.DataLogger: boolean logString(java.lang.String)>");
			//inserted code: loggerLocal.logString("\n");
//			String deliminator = "\t";
//			if(paramIndex == 1){
//				deliminator = methodName+"\n";
//			}
//			Stmt newInvokeStmt2 = Jimple.v().newInvokeStmt(
//					Jimple.v().newVirtualInvokeExpr
//			           (dataLoggerLocal, toCall.makeRef(), Arrays.asList(StringConstant.v(deliminator))));
			//units.insertBefore(newInvokeStmt, currStmt);
			return insertedStmt;
		}
		
		private Unit logDataAfter(Value loggedValue, Local dataLoggerLocal, PatchingChain units, Unit currStmt, int paramIndex, String methodName) {
			Stmt insertedStmt = prepareInsertStmt(loggedValue, dataLoggerLocal, "edu.xidian.DataLogger");
			
			units.insertAfter(insertedStmt, currStmt);
			return insertedStmt;
		}
		
		private void logInvokeAfter(List<Value> parameters, Local invokeLoggerLocal,
				PatchingChain units, Unit currStmt, String invokeMethod) {
			Stmt inFuncCurStmt = (Stmt)currStmt;
			SootMethod toCall = Scene.v().getMethod
				      ("<edu.xidian.InvokeLogger: boolean logAfterInvoke(java.lang.String)>");
			InvokeStmt insertedStmt = Jimple.v().newInvokeStmt(
					Jimple.v().newVirtualInvokeExpr
			           (invokeLoggerLocal, toCall.makeRef(), Arrays.asList(StringConstant.v(invokeMethod))));
			//Unit newCurrStmt = newInvokeStmt;
			units.insertAfter(insertedStmt, inFuncCurStmt);
			inFuncCurStmt = insertedStmt;
			for(Value v: parameters){
//				 toCall = Scene.v().getMethod
//					      ("<edu.xidian.InvokeLogger: boolean logRawString(java.lang.String)>");
//				//inserted code: loggerLocal.logString("\tparams[i]\t");
//				String paramName = "unknown";
//				if(v instanceof Local){
//					paramName = ((Local)v).getName();
//				}
//				insertedStmt = Jimple.v().newInvokeStmt(
//						Jimple.v().newVirtualInvokeExpr
//				           (invokeLoggerLocal, toCall.makeRef(), Arrays.asList(StringConstant.v(paramName))));
//				units.insertAfter(insertedStmt, inFuncCurStmt);
//				inFuncCurStmt = insertedStmt;
				
				insertedStmt = prepareInsertStmt(v, invokeLoggerLocal, "edu.xidian.InvokeLogger");
				units.insertAfter(insertedStmt, inFuncCurStmt);
				inFuncCurStmt = insertedStmt;
			}		
			toCall = Scene.v().getMethod
				      ("<edu.xidian.InvokeLogger: boolean logRawString(java.lang.String)>");
					//inserted code: loggerLocal.logString("\n");
					Stmt newInvokeStmt = Jimple.v().newInvokeStmt(
					Jimple.v().newVirtualInvokeExpr
			           (invokeLoggerLocal, toCall.makeRef(), Arrays.asList(StringConstant.v("\n"))));
					units.insertAfter(newInvokeStmt, inFuncCurStmt);
		}

		private void logInvokeBefore(List<Value> parameters, Local invokeLoggerLocal,
				PatchingChain units, Unit currStmt, String invokeMethod) {
			SootMethod toCall = Scene.v().getMethod
				      ("<edu.xidian.InvokeLogger: boolean logBeforeInvoke(java.lang.String)>");
			InvokeStmt newInvokeStmt = Jimple.v().newInvokeStmt(
					Jimple.v().newVirtualInvokeExpr
			           (invokeLoggerLocal, toCall.makeRef(), Arrays.asList(StringConstant.v(invokeMethod))));
			//Unit newCurrStmt = newInvokeStmt;
			units.insertBefore(newInvokeStmt, currStmt);
			
			for(Value v: parameters){
//				toCall = Scene.v().getMethod
//					      ("<edu.xidian.InvokeLogger: boolean logRawString(java.lang.String)>");
//				//inserted code: loggerLocal.logString("\tparams[i]\t");
//				String paramName = "unknown";
//				if(v instanceof Local){
//				 paramName = ((Local)v).getName();
//				}
//				newInvokeStmt = Jimple.v().newInvokeStmt(
//						Jimple.v().newVirtualInvokeExpr
//				           (invokeLoggerLocal, toCall.makeRef(), Arrays.asList(StringConstant.v(paramName))));
//				units.insertBefore(newInvokeStmt, currStmt);
				
				Stmt insertedStmt = prepareInsertStmt(v, invokeLoggerLocal, "edu.xidian.InvokeLogger");
				units.insertBefore(insertedStmt, currStmt);
			}
			toCall = Scene.v().getMethod
				      ("<edu.xidian.InvokeLogger: boolean logRawString(java.lang.String)>");
					//inserted code: loggerLocal.logString("\n");
					Stmt newInvokeStmt2 = Jimple.v().newInvokeStmt(
					Jimple.v().newVirtualInvokeExpr
			           (invokeLoggerLocal, toCall.makeRef(), Arrays.asList(StringConstant.v("\n"))));
					units.insertBefore(newInvokeStmt2, currStmt);
		}
		
		private void insertFakebranch(Local invokerLocal, Local resultLocal, PatchingChain units, Unit currStmt, ArrayList<Local> localArray,Iterator<Unit> staticScanIt) {
			Unit randnextStmt=null;
			Random rand = new Random();
			Iterator<Unit> CstaticScanIt=units.snapshotIterator();
			/*if(CstaticScanIt.hasNext()){
			//G.v().out.println("!!!!!------------CstaticScanIt----------:"+CstaticScanIt.next().toString());
				randnextStmt=CstaticScanIt.next();
				//G.v().out.println("!!!!!------------currStmt----------:"+currStmt.toString());
				//G.v().out.println("!!!!!------------randnextStmt----------:"+randnextStmt.toString());
			}*/
			
//			for(int i=0;i<2;i++){
//				if(rand.nextDouble()<ratio){
//					if(CstaticScanIt.hasNext()){
//						Unit temmp=(Unit)CstaticScanIt.next();
//						if(temmp instanceof AssignStmt){
//							randnextStmt=temmp;
//						}
//					}
//				}
//			}
			
			//if(randnextStmt==null)
			//{
				randnextStmt=currStmt;
			//}
			
			SootMethod toCall = Scene.v().getMethod
				      ("<invoker.sgx_invoker: void clear()>");
			Stmt newInvokeStmt = Jimple.v().newInvokeStmt(
					Jimple.v().newVirtualInvokeExpr
			           (invokerLocal, toCall.makeRef(), Arrays.asList()));
			units.insertBefore(newInvokeStmt, currStmt);
			
			toCall = Scene.v().getMethod
				      ("<invoker.sgx_invoker: void setCounter(long)>");
			newInvokeStmt = Jimple.v().newInvokeStmt(
					Jimple.v().newVirtualInvokeExpr
			           (invokerLocal, toCall.makeRef(), Arrays.asList(LongConstant.v(counter))));			
			units.insertBefore(newInvokeStmt, currStmt);
			//G.v().out.println("******counter:"+counter+"****");
			
			/*for(Local local: localArray){
				Type vType = local.getType();
				if(vType instanceof IntType){
					
					 toCall = Scene.v().getMethod
						      ("<invoker.sgx_invoker: void add(int)>");
					  newInvokeStmt = Jimple.v().newInvokeStmt(
								Jimple.v().newVirtualInvokeExpr
						           (invokerLocal, toCall.makeRef(), Arrays.asList(local)));
					  units.insertBefore(newInvokeStmt, currStmt);
					  
					  toCall = Scene.v().getMethod
						      ("<invoker.sgx_invoker: void add(int)>");
					  newInvokeStmt = Jimple.v().newInvokeStmt(
								Jimple.v().newVirtualInvokeExpr
						           (invokerLocal, toCall.makeRef(), Arrays.asList(local)));
					  units.insertBefore(newInvokeStmt, currStmt);
					 break;}
			}*/
			
            for(Local local: localArray){
            	Type vType = local.getType();
				if(vType instanceof IntType){
					if(rand.nextDouble()<=ratio){
					newInvokeStmt = prepareInsertStmt(local,invokerLocal, "invoker.sgx_invoker");
					units.insertBefore(newInvokeStmt, currStmt);
					}
				}
			}
			
			toCall = Scene.v().getMethod
				      ("<invoker.sgx_invoker: boolean call()>");

			DefinitionStmt assignStmt = Jimple.v().newAssignStmt(resultLocal, 
					Jimple.v().newVirtualInvokeExpr
			           (invokerLocal, toCall.makeRef(), Arrays.asList()));
			units.insertBefore(assignStmt, currStmt);
			//units.insertBefore(newInvokeStmt, currStmt);
			
			//Unit fakeStmt=currStmt;
			
			//((IfStmt)fakeStmt).setCondition(new JEqExpr(resultLocal, IntConstant.v(1)));
			//G.v().out.println("------------fakeStmt----------:"+fakeStmt.toString());
			//units.insertBefore(fakeStmt, currStmt);
			IfStmt fakeStmt=Grimp.v().newIfStmt(new JEqExpr(resultLocal, IntConstant.v(1)),randnextStmt);
			//((IfStmt)fakeStmt).setCondition(new JEqExpr(resultLocal, IntConstant.v(1)));
			//G.v().out.println("------------fakeStmt----------:"+fakeStmt.toString());
			units.insertBefore(fakeStmt, currStmt);
			
//			try{		
//				Writer writer = getWriter();
//				//writer.append(counter+"\t");
//				
//				writer.append(10+"\n");
//				writer.append(1+"\n");
//				writer.append(1+"\n");
//			    writer.append(2+"\n");
//				writer.flush();
//			}catch(IOException e){
//				e.printStackTrace();
//			}		
			
			indexwriter("10");
			indexwriter("1");
			indexwriter("1");
			indexwriter("2");
			
			counter++;
		}
		
		private void replaceBranch(Local invokerLocal, Local resultLocal, PatchingChain units, Unit currStmt, ArrayList<Local> localArray) {
			
			
			Value orgIfCondition = ((IfStmt) currStmt).getCondition();
    		//G.v().out.println("If statement is "+currStmt.toString()+"||"+((IfStmt) currStmt).toString()+"------end\n");
			
			ArrayList<String> params= new ArrayList<String>();
			ArrayList<Value> values = new ArrayList<Value>();
			ArrayList<String> operator = new ArrayList<String>();
			analyzeCondition(orgIfCondition,params, values, operator);
			//TODO: dump the params operator and fake variables into text file
			//ArrayType paramType = ArrayType.v(RefType.v("java.lang.String"), params.length);
			//ArrayList newArgList = new ArrayList();
//			newArgList.add(StringConstant.v("S1"));
//			
//			newArgList.add(ArrayType.v(RefType.v("java.lang.String"), 1));
//			newArgList.add(ArrayType.v(RefType.v("java.lang.String"), 1));
			//newArgList.add();
            //for (int i = 0; i < 3; i++)
            //    newArgList.add(newExpr(v.getArg(i)));
			//counter++;
			//inserted code: loggerLocal.logString("S<counter>\t");
			
			
			SootMethod toCall = Scene.v().getMethod
				      ("<invoker.sgx_invoker: void clear()>");
			Stmt newInvokeStmt = Jimple.v().newInvokeStmt(
					Jimple.v().newVirtualInvokeExpr
			           (invokerLocal, toCall.makeRef(), Arrays.asList()));
			units.insertBefore(newInvokeStmt, currStmt);
			
			toCall = Scene.v().getMethod
				      ("<invoker.sgx_invoker: void setCounter(long)>");
			newInvokeStmt = Jimple.v().newInvokeStmt(
					Jimple.v().newVirtualInvokeExpr
			           (invokerLocal, toCall.makeRef(), Arrays.asList(LongConstant.v(counter))));			
			units.insertBefore(newInvokeStmt, currStmt);
			//G.v().out.println("******counter:"+counter+"****");
			int index = 0;
			String l_index="-1", r_index="-1";
			boolean setParam0 = false, setParam1 = false;
			String symbol;
			Random rand = new Random();
			symbol = operator.get(0);
			
			String typeStr = values.get(0).getType().toString();
			//G.v().out.println("values test type is ..."+typeStr);

			int typeIndex = -1;
			if(typeStr.equals("int"))
				typeIndex = 0;
			else if(typeStr.equals("double"))
				typeIndex = 1;			
			else if(typeStr.equals("float"))
				typeIndex = 2;
			else if(typeStr.equals("char"))
				typeIndex = 3;
			else if(typeStr.equals("long"))
				typeIndex = 4;
			else if(typeStr.equals("byte"))
				typeIndex = 5;
			else //TODO: contains type object , boolean , short
				typeIndex = 0;

			for(Local local: localArray){
				
				//G.v().out.println("index:"+Integer.toString(index)+"local:"+local.toString()+"\n");
				
				if(!local.getType().toString().equals(typeStr))
					continue;
				if(local.toString().equals(params.get(0))||local.toString().equals(params.get(1))
						||(//TODO: remove ref like type !(local.getType() instanceof RefLikeType) && 
								rand.nextDouble()<=ratio)){
					if(local.toString().equals(params.get(0).toString())){
						l_index = Integer.toString(index);
						setParam0 = true;
					}
					if(local.toString().equals(params.get(1).toString())){
						r_index = Integer.toString(index);
						setParam1 = true;
					}
					newInvokeStmt = prepareInsertStmt(local,invokerLocal, "invoker.sgx_invoker");
					units.insertBefore(newInvokeStmt, currStmt);
					index++;
				}
			}
			if(l_index.equals("-1")){
				if(!setParam0)
					l_index = ((Value)(values.get(0))).getType().toString()+"_"+values.get(0);
				else if(!setParam1)
					l_index = ((Value)(values.get(1))).getType().toString()+"_"+values.get(1);
			}
			
			if(r_index.equals("-1")){
				if(!setParam0)
					r_index = ((Value)(values.get(0))).getType().toString()+"_"+values.get(0);
				else if(!setParam1)
					r_index = ((Value)(values.get(1))).getType().toString()+"_"+values.get(1);
			}
			
			toCall = Scene.v().getMethod
				      ("<invoker.sgx_invoker: boolean call()>");

			DefinitionStmt assignStmt = Jimple.v().newAssignStmt(resultLocal, 
					Jimple.v().newVirtualInvokeExpr
			           (invokerLocal, toCall.makeRef(), Arrays.asList()));
			units.insertBefore(assignStmt, currStmt);
			//return the first inserted stmt
			
			//G.v().out.println("setCondition  before:\n********"+currStmt.toString()+"*************\n");
			((IfStmt)currStmt).setCondition(new JEqExpr(resultLocal, IntConstant.v(1)));
			//G.v().out.println("setCondition  after:\n********"+currStmt.toString()+"*************\n");
			
//			try{		
//				Writer writer = getWriter();
//				//writer.append(counter+"\t");
//				
//				writer.append(typeIndex+"\n");
//				writer.append(l_index+"\n");
//				writer.append(r_index+"\n");
				
				
				indexwriter(String.valueOf(typeIndex));
				indexwriter(String.valueOf(l_index));
				indexwriter(String.valueOf(r_index));
				
				if(symbol.equals(" == "))
					//writer.append(1+"\n");
					indexwriter("1");
				if(symbol.equals(" != "))
					//writer.append(2+"\n");
				indexwriter("2");
				if(symbol.equals(" > "))
					//writer.append(3+"\n");
				indexwriter("3");
				if(symbol.equals(" < "))
					//writer.append(4+"\n");
				indexwriter("4");
				if(symbol.equals(" >= "))
					//writer.append(5+"\n");
				indexwriter("5");	
				if(symbol.equals(" <= "))
					//writer.append(6+"\n");
				indexwriter("6");
				//writer.flush();
//			}catch(IOException e){
//				e.printStackTrace();
//		}
			
			counter++;
						
		}

		private void analyzeCondition(Value exp,//x>y
				ArrayList<String> params, ArrayList<Value> values, ArrayList<String> operator) {
			
			G.v().out.println("exp:\n********"+exp.toString()+"*************\n");
			
			if(exp instanceof AbstractBinopExpr){
				analyzeCondition(((AbstractBinopExpr)exp).getOp1(),params, values, operator);
				analyzeCondition(((AbstractBinopExpr)exp).getOp2(),params, values, operator);
				if(exp instanceof JCmpExpr){
					operator.add(((JCmpExpr)exp).getSymbol());

				}
				if(exp instanceof JCmpgExpr){
					operator.add(((JCmpgExpr)exp).getSymbol());

				}
				if(exp instanceof JCmplExpr){
					operator.add(((JCmplExpr)exp).getSymbol());

				}
				if(exp instanceof JEqExpr){
					operator.add(((JEqExpr)exp).getSymbol());

				}
				if(exp instanceof JGeExpr){
					operator.add(((JGeExpr)exp).getSymbol());

				}
				if(exp instanceof JGtExpr){
					operator.add(((JGtExpr)exp).getSymbol());

				}
				if(exp instanceof JLeExpr){
					operator.add(((JLeExpr)exp).getSymbol());

				}
				if(exp instanceof JLtExpr){
					operator.add(((JLtExpr)exp).getSymbol());

				}
				if(exp instanceof JNeExpr){
					operator.add(((JNeExpr)exp).getSymbol());

				}
			}else if(exp instanceof BinopExpr){
				analyzeCondition(((BinopExpr)exp).getOp1(),params, values, operator);
				analyzeCondition(((BinopExpr)exp).getOp2(),params, values, operator);
				operator.add(((BinopExpr)exp).getSymbol());
			}else if(exp instanceof CastExpr){
				analyzeCondition(((BinopExpr)exp).getOp1(),params, values, operator);
			}else{
				if(exp instanceof Constant){
					params.add(exp.toString());
					values.add(exp);
				}
				if(exp instanceof Local){
					params.add(((Local)exp).getName());
					values.add(exp);
				}
			}
			
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < params.toArray().length; i++){
			 sb. append(params.toArray()[i]);
			 sb. append(" || ");
			}
			String s = sb.toString();
			//G.v().out.println("params:\n");
			//G.v().out.println("********"+s+"*************\n\n");
			
			StringBuffer sb1 = new StringBuffer();
			for(int i = 0; i < values.toArray().length; i++){
			 sb1. append(values.toArray()[i]);
			 sb1. append(" || ");
			}
			String s1 = sb1.toString();
			//G.v().out.println("values:\n");
			//G.v().out.println("********"+s1+"*************\n\n");
			
			StringBuffer sb2 = new StringBuffer();
			for(int i = 0; i < operator.toArray().length; i++){
			 sb2. append(operator.toArray()[i]);
			 sb2. append(" || ");
			}
			String s2 = sb2.toString();
			//G.v().out.println("operator:\n");
			//G.v().out.println("********"+s2+"*************\n\n");
		}
	}

//	private void initBranchLogger(Local transLocal, PatchingChain units,
//	Unit currStmt, String functionSignature) {
//
//soot.jimple.NewExpr sootNew = soot.jimple.Jimple.v().newNewExpr(RefType.v("edu.xidian.BranchLogger"));
//
//soot.jimple.AssignStmt stmt = soot.jimple.Jimple.v().newAssignStmt(transLocal, sootNew);
////Expr rhs = Jimple.v().newInstanceOfExpr(transLocal, RefType.v("java.util.ArrayList"));
//units.insertBefore(stmt, currStmt);
//SpecialInvokeExpr newTrans = Jimple.v().newSpecialInvokeExpr(transLocal,
//		Scene.v().getMethod("<edu.xidian.BranchLogger: void <init>(java.lang.String)>").makeRef(),
//		Arrays.asList(StringConstant.v(functionSignature)));
//soot.jimple.Stmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(newTrans);
//units.insertBefore(invokeStmt, currStmt);			
//}


//	private InvokeStmt prepareInsertStmt(Value loggedValue, Local loggerLocal, String className){
//		SootMethod toCall = Scene.v().getMethod
//			      ("<"+className+": boolean logString(java.lang.String)>");
//		Type vType = loggedValue.getType();
//		if(vType instanceof IntType){
//			toCall = Scene.v().getMethod
//				      ("<"+className+": boolean logString(int)>");
//		}else if(vType instanceof LongType){
//			toCall = Scene.v().getMethod
//				      ("<"+className+": boolean logString(long)>");
//		}else if(vType instanceof ShortType){
//			toCall = Scene.v().getMethod
//				      ("<"+className+": boolean logString(short)>");
//		}else if(vType instanceof CharType){
//			toCall = Scene.v().getMethod
//				      ("<"+className+": boolean logString(char)>");
//		}else if(vType instanceof DoubleType){
//			toCall = Scene.v().getMethod
//				      ("<"+className+": boolean logString(double)>");
//		}else if(vType instanceof FloatType){
//			toCall = Scene.v().getMethod
//				      ("<"+className+": boolean logString(float)>");
//		}else if(vType instanceof BooleanType){
//			toCall = Scene.v().getMethod
//				      ("<"+className+": boolean logString(boolean)>");
//		}else if(vType instanceof ByteType){
//			toCall = Scene.v().getMethod
//				      ("<"+className+": boolean logString(byte)>");
//		}else if(vType instanceof RefLikeType){
//			toCall = Scene.v().getMethod
//				      ("<"+className+": boolean logString(java.lang.Object)>");
//		}
//		//inserted code: loggerLocal.logString(values[i]);
//		 InvokeStmt newInvokeStmt = Jimple.v().newInvokeStmt(
//				Jimple.v().newVirtualInvokeExpr
//		           (loggerLocal, toCall.makeRef(), Arrays.asList(loggedValue)));
//		 return newInvokeStmt;
//	}

//	public static class MyAnalysis extends ForwardFlowAnalysisVerification/*extends ForwardFlowAnalysis */ {
//
//		public MyAnalysis(ExceptionalUnitGraph graph) {
//			super(graph);
//
//			Body aBody = graph.getBody();
//			doAnalysis();
//		}
		
		

//		@Override
//		protected void flowThrough(Object in, Object d, Object out) {
//	        FlowSet inSet = (FlowSet) in;
//	        FlowSet outSet = (FlowSet) out;
//	        Unit s = (Unit) d;
//	        G.v().out.println("flow through "+d.toString());
//		}
//
//		@Override
//		protected Object newInitialFlow() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		protected Object entryInitialFlow() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		protected void merge(Object in1, Object in2, Object out) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		protected void copy(Object source, Object dest) {
//			// TODO Auto-generated method stub
//			
//		}
//
//	}

}

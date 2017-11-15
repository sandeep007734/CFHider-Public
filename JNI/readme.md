## hadoopJNI is the interface for hadoop tests to call the c++ shared object
### you can modify the interface, then  
    $ javac your-hadoop-invoker.java  
    $ javah your-hadoop-invoker

## javaJNI is the interface for java tests to call the c++ shared object  
### you can modify the interface, then  
    $ javac your-java-invoker.java
    $ javah your-java-invoker    
### The compiled \*.h file should be put into *func_call_v3.17_Encryption/App* or *func_call_v3.14_without_Encryption/App*  

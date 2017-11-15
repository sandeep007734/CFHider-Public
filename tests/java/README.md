## There are Bubble Sort, Quick Sort and BinarySearch tests in this folder.
    $ cd java
    $ mkdir bin replaceOutput  
    $ cd src/  
    $ javac -d ../bin */*.java    
    $ cd ../  
    $ ./SGX-transformer.sh  

## add the SGX path to LD_LIBRARY_PATH  
    $ export LD_LIBRARY_PATH=/YOUR/PATH/TO/func_call_v3.14_without_Encryption/  or  
    $ export LD_LIBRARY_PATH=/YOUR/PATH/TO/func_call_v3.17_Encryption/  

## Then run the transformed class  
    $ cd replaceOutput  
    $ java pegasus.Sort  or  
    $ java pegasus.BinarySearch

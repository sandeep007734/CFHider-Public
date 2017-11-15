***
## func\_call\_v3.14\_JAVA
### This project is used when you need to encrypt the SGXindex
    $ cd func_call_v3.14_JAVA  
    $ make  
    $ ./app input_file_path 128bits-key output_file_path
***
## func\_call\_v3.14\_without\_Encrytion
The unencrypted version that will build the enclave.so enclave.signed.so libSGX.so which will read the matrix in /tmp/SGXindex  
**The operation is similar to the following encrypted version, except that the SGXindex should not be encrypted by func\_call\_v3.14\_JAVA/app**  
***

## func\_call\_v3.17\_Encryption
**The encrypted version that will build the enclave.so enclave.signed.so libSGX.so which will read the matrix in /tmp/SGXindex**  

* encryption with AES ctr mode  
* the matrix path can be modified at func\_call\_v3.17\_Encryption/Encalve/Encalve.cpp line 48  
* the decryption can be modified at func\_call\_v3.17\_Encryption/Encalve/Encalve.cpp line 220  
* ensure the 128bits encryption key in func\_call\_v3.14\_JAVA and the decryption key at func\_call\_v3.17\_Encryption/Encalve/Encalve.cpp line 220 are the same  

### when using this version ensure that your matrix(SGXindex) is encrypted by func\_call\_v3.14\_JAVA/app  
    $ cd func_call_v3.17_Encryption  
    $ make  
    $ cp libSGX.so ~/hadoop/lib/native/Linux-amd64-64  
    $ cp enclave.signed.so ~/hadoop/lib/native/Linux-amd64-64  
    $ cp enclave.so ~/hadoop/lib/native/Linux-amd64-64  
**NOTE: *.so should be in the same dir on every nodes in the cluster.(scp)**   
***

### to bulid these pojects see the README.txt  

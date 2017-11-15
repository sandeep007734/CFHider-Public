#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <time.h>
# include <unistd.h>
# include <pwd.h>

#define MAX_PATH FILENAME_MAX

#include "sgx_urts.h"
#include "App.h"
#include "Enclave_u.h"
#include<fstream>
#include<iomanip>
#include <iostream>
#include <vector>
using namespace std;
#include <cstdarg>
#include "invoker_sgx_invoker.h"


#define ArrayLen 100
//-------------------------------------------------
int hash_int[10000];
double hash_double[10000];
float hash_float[10000];
char hash_char[10000];
long hash_long[10000];
int sgx_use_flag=0;
/* Global EID shared by multiple threads */
sgx_enclave_id_t global_eid = 1;
//-----------------------------------------------
/*
int SGX_hash(char* buf,int line)
{
	char buffer[50];
	//return -10;
	char c=*buf;
	switch(c)
	{
		case 'i':strncpy(buffer,buf+4,44);//int_
			int int_data;
			int_data=atoi(buffer);
			hash_int[line]=int_data;
			break;
		case 'd':strncpy(buffer,buf+7,44);//double_
			double double_data;
			double_data=atof(buffer);
			hash_double[line]=double_data;
			break;
		case 'f':strncpy(buffer,buf+6,44);//float_
			float float_data;
			float_data=atof(buffer);
			hash_float[line]=(float)float_data;
			break;
		case 'c':strncpy(buffer,buf+5,44);//char_
			char char_data;
			char_data=*buffer;
			hash_char[line]=double_data;
			break;
		case 'l':strncpy(buffer,buf+5,44);//long_
			long long_data;
			long_data=atol(buffer);
			hash_long[line]=long_data;
			break;
		case '\0':break;
		default:return -6;
	}
	return 1;
}

*/

//--------------------------------------------


int itoa(int val, char* buf)  
{  
    const unsigned int radix = 10;  
    char* p;  
    unsigned int a;        //every digit  
    int len;  
    char* b;            //start of the digit char  
    char temp;  
    unsigned int u;  
    p = buf;  
    if (val < 0)  
    {  
        *p++ = '-';  
        val = 0 - val;  
    }  
    u = (unsigned int)val;  
    b = p;  
    do  
    {  
        a = u % radix;  
        u /= radix;  
        *p++ = a + '0';  
    } while (u > 0);  
    len = (int)(p - buf);  
    *p-- = 0;  
    do  
    {  
        temp = *p;  
        *p = *b;  
        *b = temp;  
        --p;  
        ++b;  
    } while (b < p);  
    return len;  
}  


/* Application entry */
//error -1 : input type is wrong
//

void SGX_Load_Matrix()
{

}
int SGX_Add_Check()
{
return 0;
}
int SGX_Add_Matrix(int type,int left,char* right,int op)
{
	SGX_Add_Check();
	int add_flag=-1;
	encall_add_matrix(global_eid,&add_flag,type,left,right,op);
	return 1;
}

JNIEXPORT jint JNICALL Java_invoker_sgx_1invoker_init(JNIEnv *env, jclass obj)
{
    /* Initialize the enclave */

	if(sgx_use_flag){
		//printf("already in use\n");
		return 0;
	}
    if(initialize_enclave() < 0){
        printf("init Failed ...\n");
        getchar();
        return -1; 
    }

	//------------load-----------------------
	//void SGX_Load_Matrix();
	int load_flag=-111;
	encall_table_load(global_eid,&load_flag);

	////printf("load flag=%d\n",load_flag);

	sgx_use_flag=1;
	return load_flag;

}
JNIEXPORT jint JNICALL Java_invoker_sgx_1invoker_destroy(JNIEnv *env, jclass obj)
{
	/*if(!sgx_use_flag){
		printf("no enclave in use\n");
	}*/
    //------------------------------destroy------------
    if(SGX_SUCCESS==sgx_destroy_enclave(global_eid)){
	sgx_use_flag=0;
    	printf("Enclave destroy success\n");
	return 0;
    }else{
	printf("Enclave destroy failure\n");
	return -1;
    }

}
//linenum ,typeid...
/*int SGX_Lookup(int line, int* int_array, int lenint,
	double* double_array, int lendouble,
	float* float_array, int lenfloat,
	char* char_array, int lenchar,
	long* long_array, int lenlong)*/
JNIEXPORT jint JNICALL Java_invoker_sgx_1invoker_commit
  (JNIEnv *env, jclass obj, jlong counter, jintArray jintArray, jint intTail, jdoubleArray jdoubleArray, jint doubleTail, jfloatArray jfloatArray, jint floatTail,jlongArray jlongArray, jint longTail, jcharArray jcharArray, jint charTail,jbyteArray jbyteArray, jint byteTail)
{
	//printf("Init enclave...\n");
	
	//init_enclave();
	//------------start----------------------
	int intArray[ArrayLen];
	double doubleArray[ArrayLen];
	float floatArray[ArrayLen];
	char charArray[ArrayLen];
	long longArray[ArrayLen];
	char byteArray[ArrayLen];

	jint *body_i = env->GetIntArrayElements(jintArray, 0);
	for (int i=0; i<ArrayLen; i++)
	{
		intArray[i] = body_i[i];
	}
	env->ReleaseIntArrayElements(jintArray, body_i, 0);

	jdouble *body_d = env->GetDoubleArrayElements(jdoubleArray, 0);
	for (int i=0; i<ArrayLen; i++)
	{
		doubleArray[i] = body_d[i];
	}
	env->ReleaseDoubleArrayElements(jdoubleArray, body_d, 0);

	jfloat *body_f = env->GetFloatArrayElements(jfloatArray, 0);
	for (int i=0; i<ArrayLen; i++)
	{
		floatArray[i] = body_f[i];
	}
	env->ReleaseFloatArrayElements(jfloatArray, body_f, 0);

	jlong *body_l = env->GetLongArrayElements(jlongArray, 0);
	for (int i=0; i<ArrayLen; i++)
	{
		longArray[i] = body_l[i];
	}
	env->ReleaseLongArrayElements(jlongArray, body_l, 0);
	
	jchar *body_c = env->GetCharArrayElements(jcharArray, 0);
	for (int i=0; i<ArrayLen; i++)
	{
		charArray[i] = body_c[i];
	}
	env->ReleaseCharArrayElements(jcharArray, body_c, 0);
	
	jbyte *body_b = env->GetByteArrayElements(jbyteArray, 0);
	for (int i=0; i<ArrayLen; i++)
	{
		byteArray[i] = body_b[i];
	}
	env->ReleaseByteArrayElements(jbyteArray, body_b, 0);
	//------------------new_version--------------
	//printf("Entering enclave...\n");
	//if(counter==NULL){return -10;}
	if(sgx_use_flag!=1){printf("not init yet");return -12;}

	//printf("counter::%ld\n",counter);

//	printf("intlen::%d\t",intTail);
//	printf("dlen::%d\t",doubleTail);
//	printf("flen::%d\t",floatTail);
//	printf("clen::%d\t",charTail);
//	printf("llen::%d\t",longTail);
//	printf("blen::%d\t",intTail);
	int re=999;
//	int w=300;
	//while(w--){
	if(counter==2828){
		if(intArray[0]==0){return 1;}else{return 0;}
	}
	if(counter==2826){
		if(intArray[0]==0){return 1;}else{return 0;}
	}
	sgx_status_t ret=encall_switch_type(global_eid,&re,counter,intArray,intTail,
			doubleArray,doubleTail,
			floatArray,floatTail,
			charArray,charTail,
			longArray,longTail,
			byteArray,byteTail);
	//printf("re==%d\n",re);
	if(ret != SGX_SUCCESS){
		print_error_message(ret);
	}
	//destroy_enclave();
	////printf("~~~~~~~~~~~result:%d\n",re);
    return re;
}


int SGX_CDECL main(int argc, char *argv[])
{
//line type

//int a[]={1,1,1,4,5,6,7,8,9,0};
//SGX_Lookup(1,a,10,NULL,0,NULL,0,NULL,0,NULL,0);
//init_enclave();
double a1[]={1.0,3.0,2.0,4.0,5.0,6.0,7.0,8.0,9.0,0.0};
//printf("result1==%d\n",SGX_Lookup(1,NULL,0,a1,10,NULL,0,NULL,0,NULL,0));

double a11[]={1.0,2.0,2.0,4.0,5.0,6.0,7.0,8.0,9.0,0.0};
//printf("result2==%d\n",SGX_Lookup(1,NULL,0,a11,10,NULL,0,NULL,0,NULL,0));
//destroy_enclave();
/*
float a2[]={1.0,1.0,1.0,4.0,5.0,6.0,7.0,8.0,9.0,0.0};
SGX_Lookup(1,2,NULL,0,NULL,0,a2,10,NULL,0,NULL,0);
char a3[]={'a','b','c','4','5','6','7','8','9','0'};
SGX_Lookup(1,3,NULL,0,NULL,0,NULL,0,a3,10,NULL,0);
long a4[]={1,1,1,4,5,6,7,8,9,0};
SGX_Lookup(1,4,NULL,0,NULL,0,NULL,0,NULL,0,a4,10);
*/
return 0;
}

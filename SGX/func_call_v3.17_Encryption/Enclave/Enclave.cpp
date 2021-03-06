#include <stdarg.h>
#include <stdio.h>	  /* vsnprintf */
# include <unistd.h>
#include <string.h>
#include <sgx_cpuid.h>
#include <stdlib.h>
#include <cstring>
#define Table_Len 100000
#define MAX 100
#include "io/fcntl.h"
#include "io/mman.h"
#include "io/stat.h"
#include "io/stdio.h"
#include "io/stdlib.h"
#include "io/time.h"
#include "io/unistd.h"
#include <sgx_tcrypto.h>

#include "Enclave.h"
#include "Enclave_t.h"  /* print_string */
//----------------struct-------------------
struct Table_attr{
  long length;
  int p2;
};
struct Table_meta{
	int p1;
	int p2;
	int op; 
};

//---------------------global------------------
//type,left-para,right-para,operater
/*
int table1[Table_Len][3]={
  0,1,2,1,
  1,1,double_2.0,2,
  2,3,4,2,
  3,4,5,2,
  4,5,6,3,
  0,6,7,4, 
  1,7,8,5,
  2,8,9,5,
  3,9,10,6,
  4,10,1,6
};*/
 
char file[50]="/tmp/SGXindex";
int hash_int[Table_Len];
double hash_double[Table_Len];
float hash_float[Table_Len];
char hash_char[Table_Len];
long hash_long[Table_Len];
char hash_byte[Table_Len];

int *table=(int*)malloc(sizeof(int)*Table_Len);
Table_attr Enclave_Table={0,0};

/*void set_table_length(int len)
{
	Enclave_Table.length=len;
}*/
//-------------------------global_end----------------
int itoa(int val, char* buf)  
{  
	const unsigned int radix = 10;  
	char* p;  
	unsigned int a;		//every digit  
	int len;  
	char* b;			//start of the digit char  
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

int ecall_ctr_decrypt(uint8_t *sql, 
	const char *sgx_ctr_key, uint8_t *p_dst,int len)
{
	const uint32_t src_len = len;
	uint8_t p_ctr[16]= {'0'};
	const uint32_t ctr_inc_bits = 128;
	uint8_t *sgx_ctr_keys = (uint8_t *)malloc(16*sizeof(char));
	memcpy(sgx_ctr_keys,sgx_ctr_key,16);

	//ocall_print_int(len);
	//ocall_print_string((const char*)sgx_ctr_key);
	sgx_status_t rc;
	uint8_t *p_dsts2 = (uint8_t *)malloc(src_len*sizeof(char));
	//uint8_t *p_dsts=
	rc = sgx_aes_ctr_decrypt((sgx_aes_gcm_128bit_key_t *)sgx_ctr_keys, sql, src_len, p_ctr, ctr_inc_bits, p_dsts2);

	for(int i=0; i<src_len; i++){
		p_dst[i] = p_dsts2[i];
		//ocall_print_string(stdout,"%c", p_dsts2[i]);
	}
	return 0;
}



int encall_hash_readin(char* buf,long line)
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
		case '\0'://ocall_print_string("null in the file");
			break;
		default:
			hash_int[line]=0;
			hash_double[line]=0;
			hash_float[line]=0;
			hash_char[line]=0;
			hash_long[line]=0;
			return -6;
	}
	return 1;
}
Table_meta get_table_meta(long Line)
{
	Table_meta meta;
	meta.p1=*(table+Line*4+1);
	meta.p2=*(table+Line*4+2);
	meta.op=*(table+Line*4+3);
	return meta;
}
char ret[10000000];
long ret_len=0;
long g_line_num=0;
int split_file()
{
	char line[50]={0};
	int k=0;
	long line_num=0;
	ocall_print_string("splitting\n");
	ocall_print_long(ret_len);
	for(long i=0;i<ret_len;i++){
		if(ret[i]=='\n'){
			line[k]=0;
			if(k==0){
				continue;
			}
			encall_read_line(line,k,line_num);
			line_num++;
			k=0;
		}else{
			line[k]=ret[i];
			k++;
		}
	}
	g_line_num=line_num;
	
	ocall_print_long(line_num);
	return 0;
}


int read_table(char* file)
{

	// int src=open(file,O_RDONLY,S_IRUSR);
	// unsigned char s[64];
	// int ret = read(src,s,64);
	// unsigned char ppp[64];
	// ecall_ctr_decrypt(s,"1234567812345678",ppp,64);
	// printf((char*)ppp);
	// return 0;

	memset(ret,0,10000000);
	char* key_t="1234567812345678";

	int reout=open(file,O_RDONLY,S_IRUSR);

//------------read out
	long l=0;
	unsigned char sss[MAX];

	memset(sss,0,MAX);
	unsigned char c[100];
	//while(!reout.eof()){
	long loop2=0;
	long loop=0;
	while(1){
		loop++;
		if(loop%1000==0){
			sleep(0);
		}
		//reout.get(c);
		l=read(reout,c,64);
		//ocall_print_long(l);
		if(l<64){
			break;
		}
		
		//sss[l]=(unsigned char)c;
		//l++;
		if(64==l){
			c[64]=0;
			unsigned char ppp[MAX];
			memset(ppp,0,MAX);
			//ocall_print_string((const char*)c);
			ecall_ctr_decrypt(c,key_t,ppp,64);
			//ocall_print_string((const char*)ppp);
			for(int i=0;i<l;i++){
				//ocall_print_string("s");
				strncat(ret,(const char*)&ppp[i],1);
				//ocall_print_string("e");
			}
			ret_len=ret_len+l;
			l=0;
			//ocall_print_long(ret_len);
			memset(c,0,65);
		}
	}
	if(l<64){
		c[l]=0;
		unsigned char ppp[MAX];
		memset(ppp,0,MAX);
		ecall_ctr_decrypt(c,key_t,ppp,l);
		for(int i=0;i<l;i++){
		strncat(ret,(const char*)&ppp[i],1);
		}
		ret_len=ret_len+l;
		l=0;
		//ocall_print_long(ret_len);
	}
	//ocall_print_string(ret);

//--------------------------------------------------
/*
	while(1){
		char c;
		loop2++;
		if(loop2%100==0){

		ocall_print_long(loop2);
		sleep(0);
		}
		//reout.get(c);
		
		if(read(reout,&c,1)==0){
			break;
		}
		//ocall_print_string("deing\n");
		sss[l]=(unsigned char)c;
		l++;
		if(l%64==0){
			sss[l]=0;
			
			unsigned char ppp[MAX];
			memset(ppp,0,MAX);
			ocall_print_long(l);
			ecall_ctr_decrypt(sss,key_t,ppp,64);
			ocall_print_string((const char*)ppp);
			ocall_print_string("hhh\n");
			for(int i=0;i<l;i++){
				//printf("%c",ppp[i]);
				strncat(ret,(const char*)&ppp[i],1);
			}
			l=0;
		}
	}
	if(l%64!=0){
		sss[l]=0;
		unsigned char ppp[MAX];
		memset(ppp,0,MAX);
		
		ecall_ctr_decrypt(sss,key_t,ppp,l);
		for(int i=0;i<l;i++){
		//printf("%c",ppp[i]);
		strncat(ret,(const char*)&ppp[i],1);
		}
		l=0;
	}
*/
//---------------------------------------



	ocall_print_string("read ok\n");
	split_file();
	return 0;
}

int encall_table_load(void)
{
	long s=0;
	int* msgs=(int*)malloc(sizeof(int)*Table_Len);
	memset(msgs,'\0',sizeof(int)*Table_Len);
	//int src=open("1",O_RDONLY,S_IRUSR);
	//ocall_print_string("enclave is reading file\n");
	read_table(file);
	//ocall_print_int(table[0]);
	////ocall_print_string("reading file ok\n");

	if(Enclave_Table.length==0){
	Enclave_Table.length=g_line_num;
	}
	//ocall_print_long(s);
	//table=(int*)malloc(sizeof(int)*4*Enclave_Table.length);
	//memcpy(table,msgs,4*Enclave_Table.length*sizeof(int));
	//ocall_load_hash();
	return 1;
}


int encall_read_line(char* in_buf,int buf_len,long line)
{
	int read_num=0;
	if(*in_buf<48||*in_buf>57){//not number
		int in_flag=999;
		read_num=0-line;
		int load_flagh=-998;
		encall_hash_readin(in_buf,line);
		//printf("readin_flag=%d\n",load_flagh);
	}else{
		read_num=atoi(in_buf);
	}
	table[line]=read_num;
	Enclave_Table.length++;
	return 0;
}

int encall_print_int(long Line,int* int_array)//---------------------------int
{
	/*TRIPLE*  TABLE=(TRIPLE*)malloc(sizeof(TRIPLE)*10);
	for(int i=0;;i++){
	  *(TABLE+i)
	}
	*/
	if(table==NULL){return -2;}//table not load yet
	if(Line>Enclave_Table.length){
		ocall_print_long(Enclave_Table.length);
		ocall_print_long(g_line_num);
		return -1;
	}
	Table_meta meta=get_table_meta(Line);
	int return_flag=-1;

	int para1;
	if(meta.p1<0){//right para is null
		int line_num=0-meta.p1;
		para1=hash_int[line_num];
	}else{
		para1=int_array[meta.p1];
	}

	int para2;
	if(meta.p2<0){//right para is null
		int line_num=0-meta.p2;
		para2=hash_int[line_num];
	}else{
		para2=int_array[meta.p2];
	}
	switch(meta.op){
	  case 1:return_flag=( para1==para2?1:0);break;
	  case 2:return_flag=( para1!=para2?1:0);break;
	  case 3:return_flag=( para1>para2?1:0);break;
	  case 4:return_flag=( para1<para2?1:0);break;
	  case 5:return_flag=( para1>=para2?1:0);break;
	  case 6:return_flag=( para1<=para2?1:0);break;
	  default:return_flag=-11;
	} 
	return return_flag;
}

int encall_print_double(long Line,double* double_array)//---------------------------double
{
	if(table==NULL){return -2;}//table not load yet
	if(Line>=Enclave_Table.length){return -1;}
	Table_meta meta=get_table_meta(Line);
	double para1;
	double para2;
	int return_flag=-1;
//for(int ii=0;ii<20;ii++){ocall_print_double(hash_double[ii]);}
	if(meta.p1<0){//right para is null
	int line_num=0-meta.p1;
		para1=hash_double[line_num];
	}else{
		para1=double_array[meta.p1];
	}

	if(meta.p2<0){//right para is null
	int line_num=0-meta.p2;
		para2=hash_double[line_num];
	}else{
		para2=double_array[meta.p2];
	}
	//ocall_print_int(meta.p1);
	//ocall_print_int(meta.p2);
	switch(meta.op){
	  case 1:return_flag=( para1==para2?1:0);break;
	  case 2:return_flag=( para1!=para2?1:0);break;
	  case 3:return_flag=( para1>para2?1:0);break;
	  case 4:return_flag=( para1<para2?1:0);break;
	  case 5:return_flag=( para1>=para2?1:0);break;
	  case 6:return_flag=( para1<=para2?1:0);break;
	} 
	return return_flag;
}

int encall_print_float(long Line,float* float_array)//------------------------float
{
	if(table==NULL){return -2;}//table not load yet
	if(Line>=Enclave_Table.length){return -1;}
	Table_meta meta=get_table_meta(Line);	
	float para1;
	float para2;
	int return_flag=-1;


	if(meta.p1<0){//left para is null
		int line_num=0-meta.p1;
		para1=hash_float[line_num];
	}else{
		para1=float_array[meta.p1];
	}

	if(meta.p2<0){//right para is null
		int line_num=0-meta.p2;
		para2=hash_float[line_num];
	}else{
		para2=float_array[meta.p2];
	}

	switch(meta.op){
	  case 1:return_flag=( para1==para2?1:0);break;
	  case 2:return_flag=( para1!=para2?1:0);break;
	  case 3:return_flag=( para1>para2?1:0);break;
	  case 4:return_flag=( para1<para2?1:0);break;
	  case 5:return_flag=( para1>=para2?1:0);break;
	  case 6:return_flag=( para1<=para2?1:0);break;
	} 
	return return_flag;
}

int encall_print_char(long Line,char* char_array)//----------------------char
{
	if(table==NULL){return -2;}//table not load yet

	if(Line>=Enclave_Table.length){return -1;}
	Table_meta meta=get_table_meta(Line);
	char para1;
	char para2;
	int return_flag=-1;

	if(meta.p1<0){//left para is null
		int line_num=0-meta.p1;
		para1=hash_int[line_num];
	}else{
		para1=char_array[meta.p1];
	}

	if(meta.p2<0){//right para is null
		int line_num=0-meta.p2;
		para2=hash_int[line_num];
	}else{
		para2=char_array[meta.p2];
	}

	switch(meta.op){
	  case 1:return_flag=( para1==para2?1:0);break;
	  case 2:return_flag=( para1!=para2?1:0);break;
	  case 3:return_flag=( para1>para2?1:0);break;
	  case 4:return_flag=( para1<para2?1:0);break;
	  case 5:return_flag=( para1>=para2?1:0);break;
	  case 6:return_flag=( para1<=para2?1:0);break;
	} 
	return return_flag;
}

int encall_print_long(long Line,long* long_array)//----------------------------long
{
	if(table==NULL){return -2;}//table not load yet

	if(Line>=Enclave_Table.length){return -1;}
	Table_meta meta=get_table_meta(Line);	
	long para1=long_array[meta.p1];
	long para2=long_array[meta.p2];
	int return_flag=-1;

	if(meta.p1<0){//right para is null
		int line_num=0-meta.p1;
		para1=hash_long[line_num];
	}else{
		para1=long_array[meta.p1];
	}

	if(meta.p2<0){//right para is null
		int line_num=0-meta.p2;
		para2=hash_long[line_num];
	}else{
		para2=long_array[meta.p2];
	}

	switch(meta.op){
	  case 1:return_flag=( para1==para2?1:0);break;
	  case 2:return_flag=( para1!=para2?1:0);break;
	  case 3:return_flag=( para1>para2?1:0);break;
	  case 4:return_flag=( para1<para2?1:0);break;
	  case 5:return_flag=( para1>=para2?1:0);break;
	  case 6:return_flag=( para1<=para2?1:0);break;
	  default:return_flag=-3;
	} 
	return return_flag;
}


int encall_print_byte(long Line,char* byte_array)//----------------------------byte
{
	if(table==NULL){return -2;}//table not load yet

	if(Line>=Enclave_Table.length){return -1;}
	Table_meta meta=get_table_meta(Line);	
	char para1=byte_array[meta.p1];
	char para2=byte_array[meta.p2];
	int return_flag=-1;

	if(meta.p1<0){//right para is null
		int line_num=0-meta.p1;
		para1=hash_int[line_num];
	}else{
		para1=byte_array[meta.p1];
	}

	if(meta.p2<0){//right para is null
		int line_num=0-meta.p2;
		para2=hash_int[line_num];
	}else{
		para2=byte_array[meta.p2];
	}

	switch(meta.op){
	  case 1:return_flag=( para1==para2?1:0);break;
	  case 2:return_flag=( para1!=para2?1:0);break;
	  case 3:return_flag=( para1>para2?1:0);break;
	  case 4:return_flag=( para1<para2?1:0);break;
	  case 5:return_flag=( para1>=para2?1:0);break;
	  case 6:return_flag=( para1<=para2?1:0);break;
	  default:return_flag=-3;
	} 
	return return_flag;
}

//error -999	:input data type donot match matrix
//error -5	  :type not exist
//error -1	  :line number is out of range
//error -2	  :matrix not load yet
//error -3	  :matrix internal data mistake
int encall_switch_type(long Line,int* int_array, int lenint,
	double* double_array, int lendouble,
	float* float_array, int lenfloat,
	char* char_array, int lenchar,
	long* long_array, int lenlong,
	char* byte_array, int lenbyte)
{
	//ocall_print_string("kaka");

	////ocall_print_int(table[0]);
	//ocall_print_string("line:");
	//ocall_print_long(Line);
	//ocall_print_string("type:");
	//ocall_print_int(*(table+Line*4));
	//ocall_print_string("left:");
	//ocall_print_int(*(table+Line*4+1));
	//ocall_print_string("right:");
	//ocall_print_int(*(table+Line*4+2));
	//ocall_print_string("op:");
	//ocall_print_int(*(table+Line*4+3));
	//ocall_print_long(Enclave_Table.length);

	int type=*(table+Line*4);
	//char ca[10];
	//itoa(type,ca);
	//ocall_print_string(ca);
	/*return type;
	if(type!=intype){
		return -999;
	}*/
	//return 0;
	if(type==10){
		//ocall_print_int(type);
		return 0;
	}
	//ocall_print_int(type);
	int return_flag=-1;
	//ocall_print_int(type);
	//ocall_print_double(double_array[1]);
/*
	if(0==Enclave_Table.length){
		ocall_print_string("reloading table");
		encall_table_load();
		ocall_print_string("reload succeed");
	}
*/

	switch(type){
		case 0:return_flag=encall_print_int(Line,int_array);break;
		case 1:return_flag=encall_print_double(Line,double_array);break;
		case 2:return_flag=encall_print_float(Line,float_array);break;
		case 3:return_flag=encall_print_char(Line,char_array);break;
		case 4:return_flag=encall_print_long(Line,long_array);break;
		case 5:return_flag=encall_print_byte(Line,byte_array);break;
		//case 10:return_flag=0;break;
		default:return_flag=-5;
	}
	return return_flag;
}

//int encall_load_hash(int* in_hash_int,double* in_hash_double,float* in_hash_float,char* in_hash_char,long* in_hash_long,char* in_hash_byte)
int encall_load_hash()
{
	//hash_int=(int*)malloc(10000*sizeof(int));
	//hash_double=(double*)malloc(10000*sizeof(double));
	//hash_float=(float*)malloc(10000*sizeof(float));
	//hash_char=(char*)malloc(10000*sizeof(char));
	//hash_long=(long*)malloc(10000*sizeof(long));
	//memcpy(hash_int,in_hash_int,10000);
	//memcpy(hash_double,in_hash_double,10000);
	//memcpy(hash_float,in_hash_float,10000);
	//memcpy(hash_char,in_hash_char,10000);
	//memcpy(hash_long,in_hash_long,10000);
	//memcpy(hash_byte,in_hash_byte,10000);
	//ocall_print_string("hahahahahaha");
	return 1;
}

int encall_add_matrix(int type,int left,char* right,int op)
{
	char buf[50];
	itoa(type,buf);
	ocall_file_add(file,buf,0);

	char buf1[50];
	itoa(left,buf1);
	ocall_file_add(file,buf1,0);
	
	ocall_file_add(file,right,0);

	char buf3[50];
	itoa(op,buf3);
	ocall_file_add(file,buf3,0);
	
	encall_table_load();
	return 1;
}

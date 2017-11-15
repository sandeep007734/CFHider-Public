#ifndef _ENCLAVE_H_
#define _ENCLAVE_H_

#include <stdlib.h>
#include <assert.h>

#if defined(__cplusplus)
extern "C" {
#endif

int encall_print_int(long line,int* int_array);
int encall_print_double(long line,double* double_array);
int encall_print_float(long line,float* float_array);
int encall_print_char(long line,char* char_array);
int encall_print_long(long line,long* long_array);
int encall_table_load(void);
int encall_load_hash();
int encall_switch_type(long Line, int* int_array, int lenint,
			double* double_array, int lendouble,
			float* float_array, int lenfloat,
			char* char_array, int lenchar,
			long* long_array, int lenlong,
			char* byte_array, int lenbyte);
//int encall_print(char *fmt,int num);
int encall_add_matrix(int type,int left,char* right,int op);
//int encall_hash_write(char* buf,int line);
#if defined(__cplusplus)
}
#endif

#endif /* !_ENCLAVE_H_ */

 #include <stdio.h>
#include <string.h>
#include <assert.h>
#include <time.h>
# include <unistd.h>
# include <pwd.h>
#include <string.h>
# define MAX_PATH FILENAME_MAX

#include "sgx_urts.h"
#include "../App.h"
#include "Enclave_u.h"


vector<string> splitEx(char* src1, char* sc)
{
    vector<string> strs;
    string src(src1);
	string separate_character(sc);
    int separate_characterLen = separate_character.size();
    int lastPosition = 0,index = -1;
    while (-1 != (index = src.find(separate_character,lastPosition)))
    {
        strs.push_back(src.substr(lastPosition,index - lastPosition));
        lastPosition = index + separate_characterLen;
    }
    string lastString = src.substr(lastPosition);
    if (!lastString.empty())
        strs.push_back(lastString);
    return strs;
}




/* Application entry */
int SGX_CALL(int ac, char *av[])
{
/*
      char* input[11];
      char NLL='\0';
      if(ac>12){
	printf("too many arguements\n");
	return 2;
      }
      if(ac<4){
	printf("need more arguements\n");
	return -2;
      }
//Check the length of input parameters
	int array[12];
	int i;
	int count=0;
	for (int j;j < ac ;j++)
	{
	    for(i=0;;i++)
	    {
	    if (*(av[j]+i) == ' ' || *(av[j]+i) == '\0')
	    break;
	    }
	    array[count]=i;
	    //if(i==0){argv[j]="0";}
	    if (i> 10){return 2;}
	    count++;
	}
	//printf("argc=%d\n",argc);
	for (i=0;i<ac;i++){
	//printf("argv[%d]=%s--lengh=%d\n",i,argv[i],array[i]);
	input[i]=(char*)malloc(sizeof(char)*10);
	strcpy(input[i],av[i]);
	}
	if(ac<12){
	  for(int k=ac;k<12;k++){
	    input[k]=&NLL;
	  }
	}

	//clock_t start = clock();


	int re=999;
	//encall_print(global_eid,&re,start,5);
	encall_print(global_eid,&re,input[1] ,input[2],input[3],input[4],input[5],input[6],input[7],input[8],input[9],input[10],input[11]);

*/
    return 0;
}



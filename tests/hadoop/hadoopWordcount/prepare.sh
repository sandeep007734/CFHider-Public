hadoop dfs -rmr wc_input

hadoop fs -mkdir wc_input
hadoop fs -put txt/* wc_input

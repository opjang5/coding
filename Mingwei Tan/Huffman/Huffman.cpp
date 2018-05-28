#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>

typedef struct {
	unsigned char uch;
	unsigned long weight;
} TmpNode;

typedef struct Huffman_Node{
	unsigned char uch;
	unsigned long weight;
	char *code;						//Huffman编码
	int parent, lchild, rchild;
} HufNode, *HufTree;

void select(HufNode *huf_tree, unsigned int n, int *s1, int *s2){
	unsigned int i;
	unsigned long min = ULONG_MAX;
	for(i = 0; i < n; ++i)           
		if(huf_tree[i].parent == 0 && huf_tree[i].weight < min){
			min = huf_tree[i].weight;
			*s1 = i;
		}
		huf_tree[*s1].parent=1;
	min=ULONG_MAX;
	for(i = 0; i < n; ++i)            
		if(huf_tree[i].parent == 0 && huf_tree[i].weight < min){
			min = huf_tree[i].weight;
			*s2 = i;
		}
} 
void CreateTree(HufNode *huf_tree, unsigned int char_kinds, unsigned int node_num){// 建立Huffman树
	unsigned int i;
	int s1, s2;
	for(i = char_kinds; i < node_num; ++i)  { 
		select(huf_tree, i, &s1, &s2);
		huf_tree[s1].parent = huf_tree[s2].parent = i; 
		huf_tree[i].lchild = s1; 
		huf_tree[i].rchild = s2; 
		huf_tree[i].weight = huf_tree[s1].weight + huf_tree[s2].weight; 
	} 
}
void HufCode(HufNode *huf_tree, unsigned char_kinds){// 生成Huffman编码
	unsigned int i;
	int cur, next, index;
	char *code_tmp = (char *)malloc(256*sizeof(char));
	code_tmp[256-1] = '\0'; 
	for(i = 0; i < char_kinds; ++i) {
		index = 256-1;
		for(cur = i, next = huf_tree[i].parent; next != 0;cur = next, next = huf_tree[next].parent)  
			if(huf_tree[next].lchild == cur) code_tmp[--index] = '0';// l-0
			else 							 code_tmp[--index] = '1';// r-1
		huf_tree[i].code = (char *)malloc((256-index)*sizeof(char));
		strcpy(huf_tree[i].code, &code_tmp[index]);
	} 
	free(code_tmp);
}
int compress(char *ifname, char *ofname){//压缩 
	unsigned int i,j,char_kinds,node_num,code_len;
	unsigned char char_temp;//8bit
	unsigned long file_len = 0;
	FILE *infile, *outfile;
	HufTree huf_tree;
	char code_buf[256] = "\0";
	TmpNode node_temp,*tmp_nodes =(TmpNode *)malloc(256*sizeof(TmpNode));		
	for(i = 0; i < 256; ++i){
		tmp_nodes[i].weight = 0;
		tmp_nodes[i].uch = (unsigned char)i;
	}
//建树 
	infile = fopen(ifname, "rb");
	if (infile == NULL)return -1;
	fread((char *)&char_temp, sizeof(unsigned char), 1, infile);
	while(!feof(infile)){
		++tmp_nodes[char_temp].weight;
		++file_len;
		fread((char *)&char_temp, sizeof(unsigned char), 1, infile);
	}
	fclose(infile);
	for(i = 0; i < 256-1; ++i)           
		for(j = i+1; j < 256; ++j)
			if(tmp_nodes[i].weight < tmp_nodes[j].weight){
				node_temp = tmp_nodes[i];
				tmp_nodes[i] = tmp_nodes[j];
				tmp_nodes[j] = node_temp;
			}
	for(i = 0; i < 256; ++i)
		if(tmp_nodes[i].weight == 0) 
			break;
	char_kinds = i;
	if (char_kinds == 1){
		outfile = fopen(ofname, "wb");
		fwrite((char *)&char_kinds, sizeof(unsigned int), 1, outfile);
		fwrite((char *)&tmp_nodes[0].uch, sizeof(unsigned char), 1, outfile);
		fwrite((char *)&tmp_nodes[0].weight, sizeof(unsigned long), 1, outfile);
		free(tmp_nodes);
		fclose(outfile);
	}
	else{
		node_num = 2 * char_kinds - 1;//Huffman树结点数 
		huf_tree = (HufNode *)malloc(node_num*sizeof(HufNode));
		for(i = 0; i < char_kinds; ++i) {
			huf_tree[i].uch = tmp_nodes[i].uch; 
			huf_tree[i].weight = tmp_nodes[i].weight;
			huf_tree[i].parent = 0; 
		}	
		free(tmp_nodes);
		for(; i < node_num; ++i)
			huf_tree[i].parent = 0; 
		CreateTree(huf_tree, char_kinds, node_num);
		HufCode(huf_tree, char_kinds);
		outfile = fopen(ofname, "wb");
		fwrite((char *)&char_kinds, sizeof(unsigned int), 1, outfile);
		for(i = 0; i < char_kinds; ++i){
			fwrite((char *)&huf_tree[i].uch, sizeof(unsigned char), 1, outfile);
			fwrite((char *)&huf_tree[i].weight, sizeof(unsigned long), 1, outfile);
		}
		fwrite((char *)&file_len, sizeof(unsigned long), 1, outfile);
		
//压缩
		infile = fopen(ifname, "rb");
		fread((char *)&char_temp, sizeof(unsigned char), 1, infile);
		while(!feof(infile)){
			for(i = 0; i < char_kinds; ++i)
				if(char_temp == huf_tree[i].uch)
					strcat(code_buf, huf_tree[i].code);
			while(strlen(code_buf) >= 8){
				char_temp = '\0';
				for(i = 0; i < 8; ++i){
					char_temp <<= 1;
					if(code_buf[i] == '1')
						char_temp |= 1;
				}
				fwrite((char *)&char_temp, sizeof(unsigned char), 1, outfile);
				strcpy(code_buf, code_buf+8);
			}
			fread((char *)&char_temp, sizeof(unsigned char), 1, infile);
		}
		code_len = strlen(code_buf);
		if(code_len > 0)
		{
			char_temp = '\0';		
			for(i = 0; i < code_len; ++i)
			{
				char_temp <<= 1;		
				if(code_buf[i] == '1')
					char_temp |= 1;
			}
			char_temp <<= 8-code_len;
			fwrite((char *)&char_temp, sizeof(unsigned char), 1, outfile);
		}
		fclose(infile);
		fclose(outfile);
		for(i = 0; i < char_kinds; ++i) free(huf_tree[i].code);
		free(huf_tree);
	}
}
int extract(char *ifname, char *ofname)// 解压
{
	unsigned int i,char_kinds,node_num,root;
	unsigned long file_len,writen_len = 0;
	FILE *infile, *outfile;
	HufTree huf_tree;
	unsigned char code_temp;

	infile = fopen(ifname, "rb");
	if (infile == NULL)return -1;

//重建Huffman树
	fread((char *)&char_kinds, sizeof(unsigned int), 1, infile);
	if (char_kinds == 1){
		fread((char *)&code_temp, sizeof(unsigned char), 1, infile);
		fread((char *)&file_len, sizeof(unsigned long), 1, infile);
		outfile = fopen(ofname, "wb");
		while (file_len--)fwrite((char *)&code_temp, sizeof(unsigned char), 1, outfile);	
		fclose(infile);
		fclose(outfile);
	}
	else{
		node_num = 2 * char_kinds - 1;//计算建树所需结点数 
		huf_tree = (HufNode *)malloc(node_num*sizeof(HufNode));
		for(i = 0; i < char_kinds; ++i){
			fread((char *)&huf_tree[i].uch, sizeof(unsigned char), 1, infile);
			fread((char *)&huf_tree[i].weight, sizeof(unsigned long), 1, infile);
			huf_tree[i].parent = 0;
		}
		for(; i < node_num; ++i)huf_tree[i].parent = 0;
		CreateTree(huf_tree, char_kinds, node_num);
//解码
		fread((char *)&file_len, sizeof(unsigned long), 1, infile);
		outfile = fopen(ofname, "wb");
		root = node_num-1;
		for(;;){
			fread((char *)&code_temp, sizeof(unsigned char), 1, infile);
			for(i = 0; i < 8; ++i){
				if(code_temp & 128)root = huf_tree[root].rchild;
				else
					root = huf_tree[root].lchild;
				if(root < char_kinds){
					fwrite((char *)&huf_tree[root].uch, sizeof(unsigned char), 1, outfile);
					++writen_len;
					if (writen_len == file_len) break;
					root = node_num-1;
				}
				code_temp <<= 1;
			}
			if (writen_len == file_len) break;
		}
		fclose(infile);
		fclose(outfile);
		free(huf_tree);
	}
}

int main(){
	int opt,flag=0;
	char ifname[256];
	char ofname[256];
	for(;;){
		flag = 0;
	input:
		printf("Please input the number of operations:\n\t1: compress\n\t2: extract\n\t3: quit\n");scanf("%d", &opt);
		if (opt == 3) break;
		else{
			printf("Please input the infile  name: ");fflush(stdin);gets(ifname);
			printf("Please input the outfile name: ");fflush(stdin);gets(ofname);
		}
		switch(opt){
			case 1 :printf("Compressing……\n");flag = compress(ifname, ofname);break;		
			case 2 :printf("Extracting ……\n");flag = extract(ifname, ofname);break;
			default:goto input;		
		}
		if (flag == -1)
			printf("Sorry, infile \"%s\" doesn't exist!\n", ifname);
		else
			printf("Operation is done!\n");
	}

	return 0;
}

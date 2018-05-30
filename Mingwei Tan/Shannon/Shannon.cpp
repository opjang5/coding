#include<stdio.h>
#include<iostream>
#include<fstream>
#include<cmath>
#include<string>
#include<vector>
#define N 4
using namespace std;

typedef struct mb{//��ũ����ڵ� 
	unsigned char buf[N];
	int num,lei,k;
	string sha;
}mbit;

bool compare(mbit x,mbit y){//�Ƚ϶����ƴ� 
	for(int i=0;i<N;i++)
		if(x.buf[i]!=y.buf[i])return false;
	return true;
}

void compress(char *path,char *cpath){//ѹ�� 
	FILE *fis = fopen(path,"rb");
	FILE *fos = fopen(cpath,"wb");
	ofstream mabiao("d:/mabiao.txt");
	mbit tmp;
	vector<mbit> a;
	vector<int> tt;
	int i,j;
	while(fread(tmp.buf,sizeof(unsigned char),N,fis)){
		//fwrite(tmp.buf,sizeof(unsigned char),N,fos);
		for(i=0;i<a.size();i++){
			if(compare(a[i],tmp)){
				a[i].num++;
				break;
			}
		}
		if(i==a.size()){
			tmp.num = 1;
			tmp.sha = "";
			a.push_back(tmp);
		}
		tt.push_back(i);
	}//������Ӧ��չ���λ�����ж����ƵĶ�ȡ 
	int p[a.size()];
	for(i=0;i<a.size();i++)p[i]=i;
	for(i=0;i<a.size()-1;i++)
		for(j=i+1;j<a.size();j++)
			if(a[p[i]].num<a[p[j]].num)
				swap(p[i],p[j]);//���� 
	a[p[0]].lei = 0;
	for(i=1;i<a.size();i++)
		a[p[i]].lei = a[p[i-1]].lei+a[p[i-1]].num;//�ۼӸ��� 
	int t;
	double k0=0,hx=0;
	for(i=0;i<a.size();i++){
		a[p[i]].k = ceil(-log10((double)a[p[i]].num/tt.size())/log10(2.0));
		a[p[i]].sha = "";
		t = a[p[i]].lei;
		for(j=0;j<a[p[i]].k;j++){
			t*=2;
			if(t>tt.size()){
				t-=tt.size();
				a[p[i]].sha += "1";
			}
			else
				a[p[i]].sha += "0";
		}
		for(j=0;j<N;j++){
			mabiao<<((a[p[i]].buf[j]>>7)&0x1)<<((a[p[i]].buf[j]>>6)&0x1); 
			mabiao<<((a[p[i]].buf[j]>>5)&0x1)<<((a[p[i]].buf[j]>>4)&0x1);
			mabiao<<((a[p[i]].buf[j]>>3)&0x1)<<((a[p[i]].buf[j]>>2)&0x1);
			mabiao<<((a[p[i]].buf[j]>>1)&0x1)<<((a[p[i]].buf[j]>>0)&0x1);
		}
		mabiao<<"    "<<a[p[i]].sha<<endl;
		k0+=(double)a[p[i]].num/tt.size()*a[p[i]].k;
		hx-=(double)a[p[i]].num/tt.size()*log10((double)a[p[i]].num/tt.size())/log10(2.0);
	}//�������ֺ���� 
	string out = "";
	for(i=0;i<tt.size();i++)
		out+=a[tt[i]].sha;
	fseek(fis,0,SEEK_END);
	int size=ftell(fis);
	//��Ӧ���� 
	cout<<"������     ��"<<hx<<" bit/����"<<endl;
	cout<<"ƽ���볤Ϊ ��"<<k0<<" bit"<<endl;
	cout<<"����Ч��Ϊ ��"<<hx/k0*100<<" %"<<endl; 
	cout<<"ԭ�ļ���С ��"<<size<<" �ֽ�"<<endl;
	cout<<"ѹ�����С ��"<<ceil((double)out.length()/8)<<" �ֽ�"<<endl;
	cout<<"ѹ���� ��"<<(double)ceil((double)out.length()/8)/size*100<<"%"<<endl;
	for(i=out.length();i%8!=0;i++)out+="0";
	unsigned char x;
	//cout<<out<<endl;
	for(i=0;i<out.length();i+=8){
		x=0;
		x+=((out[i+0]-'0')<<7);
		x+=((out[i+1]-'0')<<6);
		x+=((out[i+2]-'0')<<5);
		x+=((out[i+3]-'0')<<4);
		x+=((out[i+4]-'0')<<3);
		x+=((out[i+5]-'0')<<2);
		x+=((out[i+6]-'0')<<1);
		x+=((out[i+7]-'0')<<0);
		//cout<<x<<endl;
		fwrite(&x,sizeof(unsigned char),1,fos);
	}//��� 
	fclose(fis);
	fclose(fos);
	mabiao.close();
}
void express(char *epath,char *cpath){//��ѹ�� 
	FILE *fis = fopen(cpath,"rb");
	FILE *fos = fopen(epath,"wb");
	ifstream mabiao("d:/mabiao.txt");
	vector<string> a;
	vector<string> b;
	string t1,t2;
	while(mabiao>>t1>>t2){
		a.push_back(t1);
		b.push_back(t2);
	}//��ȡ��� 
	string tmp = "";
	unsigned char x;
	int i,j,k;
	while(fread(&x,sizeof(unsigned char),1,fis)){
		for(i=7;i>=0;i--){
			if((x>>i)&0x1 == 1)
				tmp+="1";
			else
				tmp+="0";
		}
	}
	string y = "";
	for(i=0;tmp[i]!='\0';i++){
		y+=tmp[i];
		for(j=0;j<b.size();j++){
			if(b[j]==y){
				//cout<<a[j];
				for(k=0;k<N;k++){
					x=0;
					x+=((a[j][k*8+0]-'0')<<7);
					x+=((a[j][k*8+1]-'0')<<6);
					x+=((a[j][k*8+2]-'0')<<5);
					x+=((a[j][k*8+3]-'0')<<4);
					x+=((a[j][k*8+4]-'0')<<3);
					x+=((a[j][k*8+5]-'0')<<2);
					x+=((a[j][k*8+6]-'0')<<1);
					x+=((a[j][k*8+7]-'0')<<0);
					fwrite(&x,sizeof(unsigned char),1,fos);
				}
				y="";
			}
		}
	}
}
int main(){
	//compress("d:/test.bmp","d:/test_comp.txt");
	//express("d:/test_expr.bmp","d:/test_comp.txt");
	compress("d:/lena.jpg","d:/lena_comp.txt");
	express("d:/lena_expr.jpg","d:/lena_comp.txt");
	return 0;
} 

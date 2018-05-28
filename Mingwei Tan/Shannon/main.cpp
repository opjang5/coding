#include <cmath>
#include <cstdio>
#include <vector>
#include <cstdlib>
#include <fstream>
#include <iostream>

#define N 2

using namespace std;

FILE *fis = fopen(".\\test.bmp","rb");
FILE *fos = fopen(".\\out.txt","wb");

ofstream fout(".\\ctmw.txt");

typedef struct node{
	unsigned char buf[N];
	string sha;
	int tim,lei,l;
}data;

bool compare(data a,data b){
	for(int i=0;i<N;i++)
		if(a.buf[i]!=b.buf[i])
			return false;
	return true;
}

int main(int argc, char *argv[]) 
{
	vector <data> d;
	vector <int> res;
	data tmp;
	int i,j;
	while(fread(tmp.buf,sizeof(unsigned char),N,fis)){
		for(i=0;i<d.size();i++){
			if(compare(d[i],tmp)){
				d[i].tim++;
				break;
			}
		}
		if(i==d.size()){
			d.push_back(tmp);
			d[i].tim=1;
		}
		res.push_back(i);
//		fwrite(tmp.buf,sizeof(unsigned char),N,fos);
	}
	int a[d.size()];
	for(i=0;i<d.size();i++){
		a[i]=i;
		d[i].sha = "";
		d[i].l = ceil(-log10((double)d[i].tim/res.size())/log10(2));
	}
	for(i=0;i<d.size()-1;i++)
		for(j=i+1;j<d.size();j++)
			if(d[a[i]].tim<d[a[j]].tim)
				swap(a[i],a[j]);
	d[a[0]].lei=0;
	for(i=0;i<d[a[0]].l;i++)
		d[a[0]].sha+="0";
	int t;
	for(i=1;i<d.size();i++){
		d[a[i]].lei = d[a[i-1]].lei+d[a[i-1]].tim;
		t=d[a[i]].lei;
		for(j=0;j<d[a[i]].l;j++){
			d[a[i]].sha+=(t*2>=res.size()?"1":"0");
			t = t*2%res.size();
		}
	}
	for(i=0;i<d.size();i++){
		for(j=0;j<N;j++)
			printf("%02x ",d[a[i]].buf[j]);
		printf(" --- %2d --- %d --- %f --- %f ---",
				d[a[i]].tim,d[a[i]].l,(double)d[a[i]].tim/res.size(),(double)d[a[i]].lei/res.size());
		cout<<d[a[i]].sha<<endl;
		fout<<"##";
		fout<<d[i].sha<<"@";
		for(j=0;j<N;j++){
			fout<<((d[i].buf[j]>>7)&0x1)<<((d[i].buf[j]>>6)&0x1)
				<<((d[i].buf[j]>>5)&0x1)<<((d[i].buf[j]>>4)&0x1)
				<<((d[i].buf[j]>>3)&0x1)<<((d[i].buf[j]>>2)&0x1)
				<<((d[i].buf[j]>>1)&0x1)<<((d[i].buf[j]>>0)&0x1);
		}
		fout<<"##";
	}
	string ans = "";
	for(i=0;i<res.size();i++){
		ans+=d[res[i]].sha;
	}
	cout<<ans<<endl;
	
	unsigned char x;
	vector<unsigned char>xx;
	for(i=0;i<ans.length();i+=8){
		x = 0x0;
		for(j=0;j<8 && i+j<ans.length();j++)
			x += ((ans[i+j]=='1')?1:0)<<(7-j);
		//printf("%02x",x);
		xx.push_back(x);
		fwrite(&x,sizeof(unsigned char),1,fos);
	}
	
	return 0;
}

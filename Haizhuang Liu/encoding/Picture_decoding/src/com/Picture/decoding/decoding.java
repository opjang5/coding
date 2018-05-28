package com.Picture.decoding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class decoding {
	private String Path;//读入编码后文件所在位置
	private String CodingPath;//读入码表地址
	private String OutPath;//输出文件位置
	private FileInputStream fin;
	private	BufferedReader bf;
	private FileOutput_p fout;
	private BufferedWriter test;
	private boolean oddflag;
	private Integer endnum;//尾部的字符个数
	private String end;
	public void init(){//初始化 此时读入total 和反向码表
		try {//初始化反向码表
			this.bf=new BufferedReader(new FileReader(this.CodingPath));
			String tmp="";
			while((tmp=bf.readLine())!=null){
				String []ans=tmp.split(":");
				//System.out.println(tmp);
				CodingMap.reverseCodingMap.put(ans[1], ans[0]);//放入反向码表故key为码字 value为Message
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bf.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			/* 读取文件头*/
			this.fin=new FileInputStream(this.Path);
			byte []tmp=new byte[4];
			byte []odd=new byte[1];
			byte end;
			int len=0;
			try {
				/*读出前四个字节拿出需要译码个数 */
				len=fin.read(tmp);
				if(len==4){
					int total=0;
					for(int i=0;i<4;i++){
						//System.out.println((int)tmp[i]);
						total|=((int)tmp[i])<<((3-i)*8)&((255)<<(3-i)*8);
						//System.out.println(((int)tmp[i]));
						//System.out.println(((int)tmp[i])<<((3-i)*8));
					}
					CodingMap.total=total;
				}
				else{
					System.out.println("文件错误");
				}
				/* 读出第二个字节看是否需要处理文件尾部 */
				len=fin.read(odd);
				int oddtmp=(odd[0])&255;
				if(oddtmp==0){
					this.oddflag=false;
				}
				else{
					this.oddflag=true;
					this.endnum=oddtmp;//endtmp一定是2的倍数因为尾部一定是整数字节 一个整数字节有2个字符
					byte []buf=new byte[1];
					String endtmp="";
					int []tmp1=new int[2];
					tmp1[0]=15;tmp1[1]=15;
					for(int i=0;i<oddtmp/2;i++){
						fin.read(buf);
						tmp1[1]=(buf[0])&15;//取出低四位
						tmp1[0]=((buf[0]>>4))&15;//取出高四位
						/* 0存高四位 1存低四位 */
						String ans=Integer.toHexString(tmp1[0])+Integer.toHexString(tmp1[1]);
						endtmp+=ans;
					}
					this.end=endtmp;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(CodingMap.total);
		if(this.oddflag){
			System.out.println("尾部有剩余,剩余量为"+this.endnum+"字符");
		}
		else{
			System.out.println("无剩余");
		}
		/*
		for(String key:CodingMap.reverseCodingMap.keySet()){
			System.out.println(CodingMap.reverseCodingMap.get(key)+":"+key);
		}*/
	}
	public decoding(String Path,String CodingPath,String OutPath){
		this.Path=Path;
		this.CodingPath=CodingPath;
		this.OutPath=OutPath;
		this.oddflag=false;
		this.endnum=0;
		this.init();//fin已经初始化
		fout=new FileOutput_p(this.OutPath);
		try {
			test=new BufferedWriter(new FileWriter("D:\\Document\\USTB\\大二\\大二下\\信息论与编码\\实验\\测试\\测试1译码后.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("异常");
			e.printStackTrace();
		}
	}
	public void output(){
		byte[]content=new byte[1];
		int k=0;
		int len=0;
		System.out.println("开始解码###############################################");
		try {
			String keytmp="";
			boolean breakflag=false;
			while((len=fin.read(content))!=-1){
				int []tmp=new int[2];
				tmp[0]=15;tmp[1]=15;
				tmp[1]=(content[0])&15;//取出低四位
				tmp[0]=((content[0]>>4))&15;//取出高四位
				String ans=Integer.toHexString(tmp[0])+Integer.toHexString(tmp[1]);
				//System.out.println(ans);
				//test.write("##########");
				keytmp+=ans;
				//System.out.println(keytmp);
				String value=null;
				/*if(k>=9860){
					//k=907115;
					System.out.println(k);
				}*/
				for(int i=1;i<=keytmp.length();i++){
					if((value=CodingMap.reverseCodingMap.get(keytmp.substring(0, i)))!=null){
						//System.out.println(keytmp);
						/* 可以译码 */
						test.write(value);
						test.flush();
						/* 译码时要考虑字符长度 */
						int kk=value.length();
						while(kk!=0){
							if(kk>=4){
								/* 四个字符 */
								fout.output(value.substring(0, 4));
								value=value.substring(4);
								kk-=4;
							}
							else{
								/* 超过四个字符 */
								fout.output(value.substring(0, kk));
								kk=0;
							}
						}
						k++;
						//test.write(keytmp.substring(0, i));
						//System.out.println(k);
						if(k>=CodingMap.total){
							breakflag=true;
							break;
						}
						if(i<keytmp.length()){
							keytmp=keytmp.substring(i);
							if(keytmp==null)keytmp="";
							i=0;//重新从头进行 从0二非1原因在于i++会被执行
						}
						else {
							keytmp="";//重新清空
						}
						
					}
				}
				if(breakflag==true)break;
			}
			/* 如果文件尾部还有剩余 */
			if(this.oddflag){
				/* 尾部输出一个字节一个字节进行输出 */
				int len1=(int)Math.ceil((double)(this.endnum)/2);
				String end=this.end;
				for(int i=0;i<len1;i++){
					this.fout.output(end.substring(0, 2));
					end=end.substring(2);
				}
			}
			/*if(keytmp.length()!=0){
				for(int j=0;j<keytmp.length()&&keytmp.length()!=0;j++){
					String value=null;
					for(int i=j+1;i<=keytmp.length()&&keytmp.length()!=0;i++){
						String search=keytmp.substring(j, i);
						if((value=CodingMap.reverseCodingMap.get(search))!=null){
							//System.out.println(keytmp);
							fout.output(value);
							k++;
							System.out.println(k);
							if(k>=CodingMap.total){
								break;
							}
							if(i<keytmp.length()){
								keytmp=keytmp.substring(i);
								i=j;//重新从头进行
							}
							else {
								keytmp="";//重新清空
							}
							
						}
					}
				
				}
			}*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("解码完成###############################################");
		System.out.println(k);
		try {
			fout.getFout().close();
			test.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

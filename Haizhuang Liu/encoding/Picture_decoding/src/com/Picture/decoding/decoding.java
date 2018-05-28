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
	private String Path;//���������ļ�����λ��
	private String CodingPath;//��������ַ
	private String OutPath;//����ļ�λ��
	private FileInputStream fin;
	private	BufferedReader bf;
	private FileOutput_p fout;
	private BufferedWriter test;
	private boolean oddflag;
	private Integer endnum;//β�����ַ�����
	private String end;
	public void init(){//��ʼ�� ��ʱ����total �ͷ������
		try {//��ʼ���������
			this.bf=new BufferedReader(new FileReader(this.CodingPath));
			String tmp="";
			while((tmp=bf.readLine())!=null){
				String []ans=tmp.split(":");
				//System.out.println(tmp);
				CodingMap.reverseCodingMap.put(ans[1], ans[0]);//���뷴������keyΪ���� valueΪMessage
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
			/* ��ȡ�ļ�ͷ*/
			this.fin=new FileInputStream(this.Path);
			byte []tmp=new byte[4];
			byte []odd=new byte[1];
			byte end;
			int len=0;
			try {
				/*����ǰ�ĸ��ֽ��ó���Ҫ������� */
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
					System.out.println("�ļ�����");
				}
				/* �����ڶ����ֽڿ��Ƿ���Ҫ�����ļ�β�� */
				len=fin.read(odd);
				int oddtmp=(odd[0])&255;
				if(oddtmp==0){
					this.oddflag=false;
				}
				else{
					this.oddflag=true;
					this.endnum=oddtmp;//endtmpһ����2�ı�����Ϊβ��һ���������ֽ� һ�������ֽ���2���ַ�
					byte []buf=new byte[1];
					String endtmp="";
					int []tmp1=new int[2];
					tmp1[0]=15;tmp1[1]=15;
					for(int i=0;i<oddtmp/2;i++){
						fin.read(buf);
						tmp1[1]=(buf[0])&15;//ȡ������λ
						tmp1[0]=((buf[0]>>4))&15;//ȡ������λ
						/* 0�����λ 1�����λ */
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
			System.out.println("β����ʣ��,ʣ����Ϊ"+this.endnum+"�ַ�");
		}
		else{
			System.out.println("��ʣ��");
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
		this.init();//fin�Ѿ���ʼ��
		fout=new FileOutput_p(this.OutPath);
		try {
			test=new BufferedWriter(new FileWriter("D:\\Document\\USTB\\���\\�����\\��Ϣ�������\\ʵ��\\����\\����1�����.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("�쳣");
			e.printStackTrace();
		}
	}
	public void output(){
		byte[]content=new byte[1];
		int k=0;
		int len=0;
		System.out.println("��ʼ����###############################################");
		try {
			String keytmp="";
			boolean breakflag=false;
			while((len=fin.read(content))!=-1){
				int []tmp=new int[2];
				tmp[0]=15;tmp[1]=15;
				tmp[1]=(content[0])&15;//ȡ������λ
				tmp[0]=((content[0]>>4))&15;//ȡ������λ
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
						/* �������� */
						test.write(value);
						test.flush();
						/* ����ʱҪ�����ַ����� */
						int kk=value.length();
						while(kk!=0){
							if(kk>=4){
								/* �ĸ��ַ� */
								fout.output(value.substring(0, 4));
								value=value.substring(4);
								kk-=4;
							}
							else{
								/* �����ĸ��ַ� */
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
							i=0;//���´�ͷ���� ��0����1ԭ������i++�ᱻִ��
						}
						else {
							keytmp="";//�������
						}
						
					}
				}
				if(breakflag==true)break;
			}
			/* ����ļ�β������ʣ�� */
			if(this.oddflag){
				/* β�����һ���ֽ�һ���ֽڽ������ */
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
								i=j;//���´�ͷ����
							}
							else {
								keytmp="";//�������
							}
							
						}
					}
				
				}
			}*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("�������###############################################");
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

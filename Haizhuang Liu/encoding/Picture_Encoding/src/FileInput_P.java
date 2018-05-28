import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class FileInput_P {
	private String Path;
	private String OutPath;
	private String EncodingPath;
	private FileInputStream fileins;
	private Encoding encoding;
	private BufferedWriter test;//��λ���ڽ��ļ����16���ƶ�дȻ�����ַ�������Ӷ�ʵ�ֲ��Ĺ���
	private int calcnum;//�������ü�������λһͳ�ƣ�Ȼ����б��루Ĭ���ĸ��� ��������ҪΪ2�ı��� һ���ֽ�2λ16����
	private boolean remainflag;//���ڱ�־�ļ��ֽ��ǲ���ʣ���
	private int remainnum;
	private String end;//��¼�ļ�β������λ�����ֽ�
	public FileInput_P(String path,String OutPath,String EncodingPath){
		this.Path=path;
		this.OutPath=OutPath;
		this.EncodingPath=EncodingPath;
		this.calcnum=4;
		this.encoding=new Encoding(EncodingPath,this.calcnum);
		this.remainnum=0;
		/* Ĭ��ʣ���־Ϊfalse */
		this.remainflag=false;
		try {
			fileins=new FileInputStream(this.Path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			test=new BufferedWriter(new FileWriter("D:\\Document\\USTB\\���\\�����\\��Ϣ�������\\ʵ��\\����\\����1ԭ.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public FileInput_P(String path,String OutPath,String EncodingPath,int calcnum){
		this(path, OutPath, EncodingPath);
		this.calcnum=calcnum;
		this.encoding=new Encoding(EncodingPath,this.calcnum);
	}
	public String getPath() {
		return Path;
	}
	public void setPath(String path) {
		this.Path = path;
	}
	public FileInputStream getFileins() {
		return fileins;
	}
	public void setFileins(FileInputStream fileins) {
		this.fileins = fileins;
	}
	public void setFileins(String Path) {
		try {
			this.fileins = new FileInputStream(Path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void encoding(){
		int len=0;
		byte[]content=new byte[this.calcnum/2];
		int i=0;//����ʹ�ÿ����������
		try {
			System.out.println("��ʼͳ��......");
			while((len=this.fileins.read(content))!=-1){
				/* ��λ�ÿ���ʣ����� */
				int []tmp=new int[this.calcnum];
				for(int i1=0;i1<this.calcnum;i1++){
					tmp[i1]=15;
				}
				String ans="";
				if(len==this.calcnum/2){
					/* ��ȡ���趨���ֽ� */
					for(int i1=0;i1<this.calcnum/2;i1++){
						tmp[2*i1]=((content[i1]>>4))&tmp[2*i1];//����λ
						tmp[2*i1+1]=((content[i1]<<4)>>4)&tmp[2*i1+1];//����λ
						ans+=Integer.toHexString(tmp[2*i1])+Integer.toHexString(tmp[2*i1+1]);
					}
					if(Counting.counting.get(ans)==null){//����this.calcnumλһ����
						Counting.counting.put(ans, 1);//����counting �н���ͳ��
						Counting.total++;
					}
					else{
						Counting.counting.put(ans,Counting.counting.get(ans)+1);
						Counting.total++;
					}
				}
				else{
					/* �ļ���ʣ���ֽ� */
					for(int i1=0;i1<len;i1++){
						tmp[2*i1]=((content[i1]>>4))&tmp[2*i1];//����λ
						tmp[2*i1+1]=((content[i1]<<4)>>4)&tmp[2*i1+1];//����λ
						ans+=Integer.toHexString(tmp[2*i1])+Integer.toHexString(tmp[2*i1+1]);
					}
					this.end=ans;
					this.remainflag=true;
					this.remainnum=len*2;//һ���ֽ���2��16�����ַ�
				}
				test.write(ans);
				/*System.out.print(Integer.toHexString(tmp[0]));
				System.out.print(Integer.toHexString(tmp[1]));
				System.out.print(Integer.toHexString(tmp[2]));
				System.out.print(Integer.toHexString(tmp[3]));*/
				//System.out.print(ans);
				//System.out.println();
				//System.out.print(' ');//��ֹ����̨�������ٶ�
				/*if(i%8==7){
					System.out.println();
				}*/
				i++;
		
				//if(i==1000)break;
				//System.out.println(new String(content,"ASCII"));
			}
			System.out.println("����ͳ��##############################");
			System.out.println("total:"+Counting.total);
			if(this.remainflag==false){
				this.encoding.setPrelength(this.calcnum*Counting.total);//��¼����ǰ�ַ�
			}
			else{
				/* ʣ����������β */
				this.encoding.setPrelength(this.calcnum*Counting.total+this.remainnum);//��¼����ǰ�ַ�
			}
			this.encoding.Shannon();//������ũ�����������
			if(this.remainflag==true){
				this.encoding.setremainflag(true);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				this.fileins.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			this.fileins=new FileInputStream(this.Path);//���½��ж��� ��ʼ���б������
			len=0;
			FileOutput_p fout=new FileOutput_p(this.OutPath);
			//fout.test_output();
			fout.outputhead(Counting.total,this.remainflag,this.remainnum);//���4���ֽڵ�ͷ �д���total
			/* ���β����ʣ����ں�����ʣ���� */
			/* û�������ʣ���� */
			if(this.remainflag){
				int lenend=end.length();
				while(lenend!=0){
					if(lenend>=4){
						fout.output(end.substring(0, 4));
						end=end.substring(4);
						lenend-=4;
					}
					else{
						fout.output(end);
						lenend=0;
					}
				}
			}
			System.out.println("��ʼд��################################################");
			try {
				int i1=0;int kk=0;//ͳ���������4λһ�����ȵ�λ
				String outtmp="";String out="";
				while((len=this.fileins.read(content))!=-1){
					int []tmp=new int[this.calcnum];
					for(int i11=0;i11<this.calcnum;i11++){
						tmp[i11]=15;
					}
					String ans="";
					//System.out.println(len);
					if(len==this.calcnum/2){
						for(int i11=0;i11<this.calcnum/2;i11++){
							tmp[2*i11]=((content[i11]>>4))&tmp[2*i11];//����λ
							tmp[2*i11+1]=((content[i11]<<4)>>4)&tmp[2*i11+1];//����λ
							ans+=Integer.toHexString(tmp[2*i11])+Integer.toHexString(tmp[2*i11+1]);//�õ�message
						}
						ans=this.encoding.getFcodetable().get(ans);//����������еõ�����
						//System.out.println(this.encoding.getLastlength());
						//System.out.println(ans.length());
						this.encoding.setLastlength(this.encoding.getLastlength()+ans.length());
						//kk+=ans.length();
						outtmp+=ans;
						kk=outtmp.length();
						i1++;
						if(i1<this.encoding.getPrelength()/this.calcnum){//���һ�ֽ�֮ǰ2�ı�����ʱ�����
							while(kk!=0){
								if(kk>=4){//��4��Ϳ�ʼ��ȡ��� ����С����������һ����
									out=outtmp.substring(0,4);//��ȡǰ�ĸ��ַ� 2���ֽڽ������
									outtmp=outtmp.substring(4);
									fout.output(out);
									kk-=4;
								}
								else{
									break;
								}
							}
							
						}
						else{//�������һ���ֽڱ������
							if(kk<=8){//��8Сֱ�����
								fout.output(outtmp);
								kk=0;
							}
							else{
								while(kk!=0){
									if(kk>=4){//��8����8���˸���ȡ���
										out=outtmp.substring(0,4);
										fout.output(out);
										outtmp=outtmp.substring(4);
										kk=outtmp.length();
									}
									else{
										if(kk>0)
											fout.output(outtmp);//һֱ�����
										//outtmp=outtmp.substring(outtmp.length());
										kk=0;
									}
								}
							}
						}
					}
				}
				try{
					System.out.println("����д��################################################");
					fout.getFout().close();
					test.close();
					double prelength=this.encoding.getPrelength();
					double lastlength=this.encoding.getLastlength();
					this.encoding.setCodingEfficiency((prelength-lastlength)/prelength);
					System.out.println("Prelength:"+this.encoding.getPrelength());
					System.out.println("Lastlength:"+this.encoding.getLastlength());
					System.out.println("CodingEfficiency:"+this.encoding.getCodingEfficiency());
				}
				catch(IOException e){
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.fileins.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

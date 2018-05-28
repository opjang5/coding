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
	private BufferedWriter test;//此位用于将文件变成16进制读写然后按照字符输出，从而实现查阅功能
	private int calcnum;//用于设置几个进制位一统计，然后进行编码（默认四个） 此数字需要为2的倍数 一个字节2位16进制
	private boolean remainflag;//用于标志文件字节是不是剩余个
	private int remainnum;
	private String end;//纪录文件尾部不足位数的字节
	public FileInput_P(String path,String OutPath,String EncodingPath){
		this.Path=path;
		this.OutPath=OutPath;
		this.EncodingPath=EncodingPath;
		this.calcnum=4;
		this.encoding=new Encoding(EncodingPath,this.calcnum);
		this.remainnum=0;
		/* 默认剩余标志为false */
		this.remainflag=false;
		try {
			fileins=new FileInputStream(this.Path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			test=new BufferedWriter(new FileWriter("D:\\Document\\USTB\\大二\\大二下\\信息论与编码\\实验\\测试\\测试1原.txt"));
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
		int i=0;//测试使用控制输出次数
		try {
			System.out.println("开始统计......");
			while((len=this.fileins.read(content))!=-1){
				/* 此位置考虑剩余情况 */
				int []tmp=new int[this.calcnum];
				for(int i1=0;i1<this.calcnum;i1++){
					tmp[i1]=15;
				}
				String ans="";
				if(len==this.calcnum/2){
					/* 能取满设定个字节 */
					for(int i1=0;i1<this.calcnum/2;i1++){
						tmp[2*i1]=((content[i1]>>4))&tmp[2*i1];//高四位
						tmp[2*i1+1]=((content[i1]<<4)>>4)&tmp[2*i1+1];//低四位
						ans+=Integer.toHexString(tmp[2*i1])+Integer.toHexString(tmp[2*i1+1]);
					}
					if(Counting.counting.get(ans)==null){//按照this.calcnum位一编码
						Counting.counting.put(ans, 1);//传入counting 中进行统计
						Counting.total++;
					}
					else{
						Counting.counting.put(ans,Counting.counting.get(ans)+1);
						Counting.total++;
					}
				}
				else{
					/* 文件是剩余字节 */
					for(int i1=0;i1<len;i1++){
						tmp[2*i1]=((content[i1]>>4))&tmp[2*i1];//高四位
						tmp[2*i1+1]=((content[i1]<<4)>>4)&tmp[2*i1+1];//低四位
						ans+=Integer.toHexString(tmp[2*i1])+Integer.toHexString(tmp[2*i1+1]);
					}
					this.end=ans;
					this.remainflag=true;
					this.remainnum=len*2;//一个字节是2个16进制字符
				}
				test.write(ans);
				/*System.out.print(Integer.toHexString(tmp[0]));
				System.out.print(Integer.toHexString(tmp[1]));
				System.out.print(Integer.toHexString(tmp[2]));
				System.out.print(Integer.toHexString(tmp[3]));*/
				//System.out.print(ans);
				//System.out.println();
				//System.out.print(' ');//禁止控制台输出提高速度
				/*if(i%8==7){
					System.out.println();
				}*/
				i++;
		
				//if(i==1000)break;
				//System.out.println(new String(content,"ASCII"));
			}
			System.out.println("结束统计##############################");
			System.out.println("total:"+Counting.total);
			if(this.remainflag==false){
				this.encoding.setPrelength(this.calcnum*Counting.total);//纪录编码前字符
			}
			else{
				/* 剩余情况计入结尾 */
				this.encoding.setPrelength(this.calcnum*Counting.total+this.remainnum);//纪录编码前字符
			}
			this.encoding.Shannon();//进行香农编码生成码表
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
			this.fileins=new FileInputStream(this.Path);//重新进行读入 开始进行编码输出
			len=0;
			FileOutput_p fout=new FileOutput_p(this.OutPath);
			//fout.test_output();
			fout.outputhead(Counting.total,this.remainflag,this.remainnum);//输出4个字节的头 中存入total
			/* 如果尾部有剩余跟在后边输出剩余量 */
			/* 没有则不输出剩余量 */
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
			System.out.println("开始写入################################################");
			try {
				int i1=0;int kk=0;//统计输出长度4位一个长度单位
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
							tmp[2*i11]=((content[i11]>>4))&tmp[2*i11];//高四位
							tmp[2*i11+1]=((content[i11]<<4)>>4)&tmp[2*i11+1];//低四位
							ans+=Integer.toHexString(tmp[2*i11])+Integer.toHexString(tmp[2*i11+1]);//得到message
						}
						ans=this.encoding.getFcodetable().get(ans);//从正向码表中得到码字
						//System.out.println(this.encoding.getLastlength());
						//System.out.println(ans.length());
						this.encoding.setLastlength(this.encoding.getLastlength()+ans.length());
						//kk+=ans.length();
						outtmp+=ans;
						kk=outtmp.length();
						i1++;
						if(i1<this.encoding.getPrelength()/this.calcnum){//最后一字节之前2的倍数的时候输出
							while(kk!=0){
								if(kk>=4){//比4大就开始截取输出 比四小继续加入下一码字
									out=outtmp.substring(0,4);//截取前四个字符 2个字节进行输出
									outtmp=outtmp.substring(4);
									fout.output(out);
									kk-=4;
								}
								else{
									break;
								}
							}
							
						}
						else{//读到最后一个字节必须输出
							if(kk<=8){//比8小直接输出
								fout.output(outtmp);
								kk=0;
							}
							else{
								while(kk!=0){
									if(kk>=4){//比8大先8个八个截取输出
										out=outtmp.substring(0,4);
										fout.output(out);
										outtmp=outtmp.substring(4);
										kk=outtmp.length();
									}
									else{
										if(kk>0)
											fout.output(outtmp);//一直到最后
										//outtmp=outtmp.substring(outtmp.length());
										kk=0;
									}
								}
							}
						}
					}
				}
				try{
					System.out.println("结束写入################################################");
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

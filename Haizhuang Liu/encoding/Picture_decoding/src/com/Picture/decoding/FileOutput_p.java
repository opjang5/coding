package com.Picture.decoding;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileOutput_p {
	private String Path;
	private FileOutputStream fout;
	public FileOutput_p(){
		
	}
	public FileOutput_p(String Path){
		this.Path=Path;
		try {
			fout=new FileOutputStream(Path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private byte[] HexStringtobyte(String Hex){//返回值中从0开始为高位1开始为低位
		Integer tmp=0;
		tmp=Integer.valueOf(Hex, 16);
		if(Hex.length()%2==0){//偶数时直接进行输出
			int length=Hex.length()/2;
			byte []ans=new byte[length];
			for(int i=length-1;i>=0;i--){
				ans[length-1-i]=(byte) (tmp>>(i*8)&(255));
			}
			return ans;
		}
		else{
			int length=(Hex.length()-1)/2+1;//最后一个之后四位二进制
			byte []ans=new byte[length];
			for(int i=length-1;i>=0;i--){
				if(i!=0){//不到最后一个四位二进制之前都能凑齐一个字节
					ans[length-1-i]=(byte) (tmp>>(i*8)&(255));
				}
				else{
					ans[i]=(byte) ((tmp&(15))<<4);//剩余最后四位 最后一字节高四位为此低四位用0000补充
					//最后通过total进行统计
				}
			}
			return ans;
		}
	}
	private byte[] Integertobyte(Integer a){
		int tmp=a;
		byte []ans=new byte[4];
		for(int i=3;i>=0;i--){
			ans[3-i]=(byte) ((tmp>>(8*i))&(255));
		}
		return ans;
	}
	public void test_Integertobyte(Integer a){
		byte []ans=this.Integertobyte(a);
		Integer tmp=0;
		for(int i=0;i<ans.length;i++){
			System.out.println(ans[i]);
			tmp=tmp|((int)ans[i])<<(ans.length-1-i);
		}
		System.out.println(tmp);
	}
	public void output(String o){
		byte []content=this.HexStringtobyte(o);
		/*if(content!=null){*/
			try {
				content=this.HexStringtobyte(o);
				this.fout.write(content, 0, content.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				content=this.HexStringtobyte(o);
				e.printStackTrace();
			}
		/*}*/
	}
	public void outputhead(Integer a){
		byte []content=this.Integertobyte(a);
		try {
			this.fout.write(content, 0, content.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public FileOutputStream getFout() {
		return fout;
	}
	public void test_output(){
		this.output("000");
	}
}

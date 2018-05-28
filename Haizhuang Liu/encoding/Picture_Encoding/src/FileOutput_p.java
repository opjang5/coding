/*
 * ������Խ����Ż��Ӷ���ʡwhile��ʹ��
 * */
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
	private byte[] HexStringtobyte(String Hex){//����ֵ�д�0��ʼΪ��λ1��ʼΪ��λ
		Integer tmp=0;
		/* �˴�������������ܱ�8λ�� */
		tmp=Integer.valueOf(Hex, 16);
		if(Hex.length()%2==0){//ż��ʱֱ�ӽ������
			int length=Hex.length()/2;
			byte []ans=new byte[length];
			for(int i=length-1;i>=0;i--){
				ans[length-1-i]=(byte) (tmp>>(i*8)&(255));
			}
			return ans;
		}
		else{
			int length=(Hex.length()-1)/2+1;//���һ��֮����λ������
			byte []ans=new byte[length];
			for(int i=length-1;i>=0;i--){
				if(i!=0){//�������һ����λ������֮ǰ���ܴ���һ���ֽ�
					ans[length-1-i]=(byte) (tmp>>(i*8)&(255));
				}
				else{
					ans[i]=(byte) ((tmp&(15))<<4);//ʣ�������λ ���һ�ֽڸ���λΪ�˵���λ��0000����
					//���ͨ��total����ͳ��
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
				this.fout.write(content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				content=this.HexStringtobyte(o);
				e.printStackTrace();
			}
		/*}*/
	}
	/* ��û�иĳɱ䳤����֮ǰ������boolean���ڿ��Է�����дΪ֧��ʣ������ */
	public void outputhead(Integer a,boolean b,int remainnum){
		/* ͷǰ�ĸ��ֽڰ���������� ������ֽڱ�ʾ����ǰ�Ƿ�Ϊ���� */
		/* ���Ϊ0����,����0�����������β��ʣ���ֽ��� */
		/* ���β���ֽ� */
		byte []content=this.Integertobyte(a);
		try {
			this.fout.write(content);
			if(b){
				/* bΪtrue����β������ʣ�� */
				byte buf=(byte)(remainnum);//�����ַ� һ���ֽ�
				this.fout.write(buf);
			}
			else{
				byte buf=0x00;
				this.fout.write(buf);
			}
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

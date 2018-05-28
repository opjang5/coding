import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Encoding {
	private List<CodingMap> codingmaps=new ArrayList();
	private Set<String> codingwords=new HashSet<String>();
	private Map<String,String>fcodetable=new HashMap();//正向码表
	private Map<String,String>rcodetable=new HashMap();//反向码表 用于解码
	private Double kbar=0.0;//用于计算平均码长
	private Double Hx=0.0;//用于计算信源熵
	private Double efficiency=0.0;
	private Double CodingEfficiency=0.0;
	private Integer Prelength=0;
	private Integer Lastlength=0;
	private String CodingPath;
	private FileOutputStream fout; 
	private boolean remainflag;
	private Integer calcnum;
	public void init(){//编码前一定进行运行
		//System.out.println("------------------------------------------------------------------");
		//System.out.println("total:"+Counting.total);
		for(String key:Counting.counting.keySet()){//初始化计算概率值
			Double tmp=(double) Counting.counting.get(key)/Counting.total;
			//System.out.println(key+":"+tmp+" "+Counting.counting.get(key));
			this.codingmaps.add(new CodingMap(key,null,tmp));
			this.Hx+=-(tmp*Math.log10(tmp))/Math.log10(2);
		}
		try {
			fout=new FileOutputStream(this.CodingPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Encoding(String CodingPath,Integer calcnum){
		this.CodingPath=CodingPath;
		this.remainflag=false;
		this.calcnum=calcnum;
	}
	private String IntegertoHexString(Integer a,int k){
		int tmp=a;
		int anstmp=0;
		String ans ="";
		for(int i=k-1;i>=0;i--){//从高位到低位
			anstmp=tmp>>(4*i)&15;
			ans+=Integer.toHexString(anstmp);
		}
		return ans;
	}
	public void Shannon(){
		this.init();
		Collections.sort(this.codingmaps);//第一步进行排序
		/*System.out.println("------------------------------------------------------------------");
		for(CodingMap cdm:this.codingmaps){
			System.out.println(cdm.getMessage()+":"+cdm.getP());
		}*/
		int i=0;
		CodingMap last=new CodingMap();
		System.out.println("开始进行编码#############################################################");
		for(CodingMap cdm:this.codingmaps){
			//System.out.println(i);
			cdm.setIndex(i);//设置序号值
			cdm.setPa(last.getPa()+last.getP());//设置累加概率
			int k=(int)Math.ceil(-Math.log10(cdm.getP())/Math.log10(16));//计算码字长度
			cdm.setK(k);//设置码字长度
			Integer codingwordtmp;
			codingwordtmp=(int)(cdm.getPa()*Math.pow(16,k));
			String cdtmp=this.IntegertoHexString(codingwordtmp, k);//生成码字
			cdm.setCodeWord(cdtmp);//设置码字
			this.codingwords.add(cdtmp);//用于验证编码是否失真
			this.kbar+=cdm.getP()*cdm.getK();
			last=cdm;
			this.fcodetable.put(cdm.getMessage(), cdm.getCodeWord());//生成正向码表
			this.rcodetable.put(cdm.getCodeWord(), cdm.getMessage());//生成反向码表
			try {
				/* txt输出需要加入\r到头再用\n进行换行 */
				/* 此处用基本文件流输出码表 */
				fout.write((cdm.getMessage()+":"+cdm.getCodeWord()+'\r'+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		this.efficiency=this.Hx/(this.kbar*Math.log10(this.calcnum*4)/Math.log10(2));
		this.CodingWordOutPut();
		System.out.println("total:"+this.codingmaps.size());
		System.out.println("codingwords:"+this.codingwords.size());
		System.out.println("H(X):"+this.Hx);
		System.out.println("Kbar:"+this.kbar);
		System.out.println("efficiency:"+this.efficiency);
		try {
			this.fout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void CodingWordOutPut(){
		for(CodingMap cdm:this.codingmaps){
			//System.out.println(cdm.getIndex()+'\t'+cdm.getMessage()+'\t'+cdm.getP()+'\t'+cdm.getPa()+'\t'+cdm.getK()+'\t'+cdm.getCodeWord());
			System.out.println(cdm.getMessage()+"        "+cdm.getK()+"        "+cdm.getCodeWord());
		}
	}
	public List<CodingMap> getCodingmaps() {
		return codingmaps;
	}
	public Map<String, String> getFcodetable() {
		return fcodetable;
	}
	public Double getCodingEfficiency() {
		return CodingEfficiency;
	}
	public void setCodingEfficiency(Double codingEfficiency) {
		CodingEfficiency = codingEfficiency;
	}
	public Integer getPrelength() {
		return Prelength;
	}
	public void setPrelength(int prelength) {
		Prelength = prelength;
	}
	public Integer getLastlength() {
		return Lastlength;
	}
	public void setLastlength(Integer lastlength) {
		Lastlength = lastlength;
	}
	public boolean isremainflag() {
		return remainflag;
	}
	public void setremainflag(boolean remainflag) {
		this.remainflag = remainflag;
	}
}

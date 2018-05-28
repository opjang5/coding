
public class CodingMap implements Comparable<CodingMap> {
	private String Message;//原消息
	private String CodeWord;//码字
	private Double p;//概率
	private Double pa;//累加概率
	private int index;//序号
	private int k;//码长
	
	public CodingMap(String Message,String CodeWord,Double p){
		this.Message=Message;
		this.CodeWord=CodeWord;
		this.p=p;
		this.pa=0.0;
	}
	public CodingMap(){
		this.pa=0.0;
		this.p=0.0;
		this.CodeWord=null;
		this.index=0;
		this.Message=null;
	}
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	public Double getP() {
		return p;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Double getPa() {
		return pa;
	}
	public void setPa(Double pa) {
		this.pa = pa;
	}
	public void setP(Double p) {
		this.p = p;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public String getCodeWord() {
		return CodeWord;
	}
	public void setCodeWord(String codeWord) {
		CodeWord = codeWord;
	}
	@Override
	public int compareTo(CodingMap o) {
		// TODO Auto-generated method stub
		if(this.p>o.p){//按照从大到小进行排列
			return -1;
		}
		else if(this.p<o.p){
			return 1;
		}
		return 0;
	}
}

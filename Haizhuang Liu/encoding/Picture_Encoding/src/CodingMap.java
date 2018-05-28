
public class CodingMap implements Comparable<CodingMap> {
	private String Message;//ԭ��Ϣ
	private String CodeWord;//����
	private Double p;//����
	private Double pa;//�ۼӸ���
	private int index;//���
	private int k;//�볤
	
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
		if(this.p>o.p){//���մӴ�С��������
			return -1;
		}
		else if(this.p<o.p){
			return 1;
		}
		return 0;
	}
}

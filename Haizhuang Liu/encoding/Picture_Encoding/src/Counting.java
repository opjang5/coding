
import java.util.HashMap;
import java.util.Map;


public class Counting {
	public static Map<String,Integer> counting=new HashMap<>();//���ڼ������
	public static Integer total=0;//����ͳ��������������Ϊ����2�ֽ� ���ͷд���
	public static void intercept(){//�������н������ ͳ����
		for(String key:counting.keySet()){
			System.out.println(key+':'+(double)counting.get(key)/total+"    "+counting.get(key));
		}
	}
}

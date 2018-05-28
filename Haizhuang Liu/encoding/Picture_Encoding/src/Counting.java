
import java.util.HashMap;
import java.util.Map;


public class Counting {
	public static Map<String,Integer> counting=new HashMap<>();//用于计算概率
	public static Integer total=0;//用于统计总数，此总数为所有2字节 输出头写这个
	public static void intercept(){//遍历所有进行输出 统计量
		for(String key:counting.keySet()){
			System.out.println(key+':'+(double)counting.get(key)/total+"    "+counting.get(key));
		}
	}
}

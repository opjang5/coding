package com.Picture.decoding;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			decoding dc=new decoding("D:\\Document\\USTB\\大二\\大二下\\信息论与编码\\实验\\测试\\timg.opjang5",
				"D:\\Document\\USTB\\大二\\大二下\\信息论与编码\\实验\\测试\\timg码表.txt",
				"D:\\Document\\USTB\\大二\\大二下\\信息论与编码\\实验\\测试\\timg译码.bmp");
		dc.output();
		/*String a="1234";
		String b=a.substring(0, 0);
		if(b!=null){
			System.out.println(1);
		}		
		a=a.substring(4);
		System.out.println(a);
		System.out.println(b);*/
	}

}

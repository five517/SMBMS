package com.leetcode;


import java.util.ArrayList;

public class testString {
    public static void main(String[] args) {
//        String pp="8zz7";
//        char[] chars = pp.toCharArray();
//        for (int i = 0; i < pp.length(); i++) {
//            System.out.println(pp.charAt(i));
//            System.out.println(pp.substring(i,i+1));
//            System.out.println(chars[i]);
//        }
//        System.out.println(pp.concat("7777"));
//        System.out.println(pp.compareTo("7777"));
//        System.out.println((int)('s'));
//        System.out.println((char)(115));
//        System.out.println(pp.hashCode());
//        System.out.println(pp.contains("8z"));
//        System.out.println(pp.endsWith("zz7"));
//        int []a={1,2,3,4,5,6};
//        boolean c= Arrays.asList(a).contains(1);
        ArrayList a=new ArrayList();
        int len=a.size();
        for (int i = 0; i <10 ; i++) {
            a.add(i);

        }
        System.out.println(a);
        Object o = a.get(1);
        a.remove(0);
        System.out.println(a);
        int [][]temp = new int[100][100];
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 9; j++) {
                temp[i][j]=i*j;
            }
        }
        System.out.println(temp.length);
        System.out.println(temp[0].length);
    }

}

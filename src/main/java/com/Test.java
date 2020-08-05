package com;

/**
 * @author: 屈子威
 * @create: 2020-08-05
 * @description: 测试一把~
 **/
public class Test {
    public static void main(String[] args) {
        SimpleHashMap<String, String> map = new SimpleHashMap<>();
        map.put("张三", "好帅");
        map.put("李四", "好丑");
        map.put("王五", "好穷");
        map.put("赵六", "好矮");
        map.put("残月", "好大");
        map.put("123", "456");
        map.put("456", "789");
        map.put("789", "8910");
        map.put("91011", "111213");
        System.out.println(map);
        System.out.println("当前map的size是："+map.size());
        map.remove("张三");
        System.out.println(map);
        System.out.println("当前map的size是："+map.size());
        System.out.println(map.get("残月"));
        map.put("残月", "有雕");
        System.out.println(map.get("残月"));
        System.out.println(map);
    }
}

package com;

import java.util.Arrays;

/**
 * @author ：quziwei
 * @date ：05/08/2020
 * @description：简单的HashMap实现
 */
public class SimpleHashMap<K, V> {
    /**
     * 默认Hash桶数量为16
     */
    private static final int DEFAULT_BUCKET = 1 << 4;

    /**
     * 默认负载因子
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 定义一个hash桶,初始为null,在new对象的时候或者是第一次put的时候我们对他进行一个赋值
     */
    private Node<K, V>[] nodes;

    /**
     * 定义一个扩容阈值
     */
    private int threshold;

    /**
     * 定义一个hash桶
     */
    private int bucket;


    /**
     * 定义一个size方便我们计算一共有多少个节点(Node)
     */
    private volatile int size = 0;

    /**
     * 获取map的长度
     */
    public int size() {
        return size;
    }

    /**
     * 通过构造方法对我们的Hash桶和扩容阈值进行一个初始化操作
     */
    public SimpleHashMap() {
        this.bucket = DEFAULT_BUCKET;
        this.threshold = (int) (bucket * DEFAULT_LOAD_FACTOR);
        this.nodes = new Node[this.bucket];
    }

    /**
     * 写一个put方法
     */
    public void put(K k, V v) {
        //计算一个hash值
        int hash = hash(k.hashCode());
        //计算出数组下标
        int index = index(hash);
        //判断我们的Key是否已经存在链表当中,如果存在则替换oldValue为newValue
        for (Node<K, V> n = nodes[index]; n != null; n = n.next) {
            //对当前操作的链表加一个锁
            synchronized (n) {
                if (n.hash == hash && n.k.equals(k) || n.k == k) {
                    n.v = v;
                    return;
                }
            }
        }

        //先将我们的数据放进node中
        Node<K, V> x = new Node<>();
        x.hash = hash;
        x.k = k;
        x.v = v;
        //把节点x通过resize方法进行存放
        resize(x, index);
    }

    /**
     * 对key.hashCode()进行二次hash
     */
    private int hash(int hash) {
        return hash ^ (hash >>> 16);
    }

    /**
     * 判断是否需要扩容，如果需要扩容则进行扩容操作，如果不扩容就进行添加操作
     * 因为resize方法的所有操作必须保证原子性，所以直接在方法上加synchronized
     */
    private synchronized void resize(Node<K, V> x, int index) {
        Node<K, V> oldNode = nodes[index];
        //判断是否达到扩容要求，如果size超过了扩容阈值并且hash桶对应的链表不为空，则执行扩容逻辑
        if (size >= threshold && oldNode != null) {
            this.size = 0;
            //记录旧的Hash桶
            Node<K, V>[] old = nodes;
            //重新对Hash桶分配一块内存空间，扩容大小为原来的两倍
            this.bucket = bucket * 2;
            this.nodes = new Node[bucket];
            //重新计算一个扩容阈值
            this.threshold = (int) (bucket * DEFAULT_LOAD_FACTOR);
            //将老的数据存放到新的hash桶中
            for (Node<K, V> node : old) {
                while (node != null) {
                    int i = index(node.hash);
                    Node<K, V> temp = nodes[i];
                    //判断链表是否为空，如果为空，直接插入,否则将新的链表的next指向老的链表
                    if (temp == null) {
                        this.nodes[i] = node;
                    } else {
                        //将数据插入到新的链表当中，使用一个辅助指针next指向node的下一个节点
                        Node<K,V> next = node.next;
                        node.next = temp;
                        this.nodes[i] = node;
                        node = next;
                    }
                    size++;
                }
            }
        } else {
            //直接使用头插法
            x.next = oldNode;
            this.nodes[index] = x;
            size++;
        }
    }

    /**
     * 根据key获取value
     */
    public V get(K k) {
        if (k == null) {
            return null;
        }
        int hash = hash(k.hashCode());
        int index = index(hash);
        Node<K, V> list = nodes[index];
        while (list != null) {
            if (list.hash == hash && list.k.equals(k)) {
                //说明找到了这个K
                return list.v;
            }
            list = list.next;
        }
        return null;
    }

    /**
     * 删除一个节点
     */
    public void remove(K k) {
        if (k == null) {
            return;
        }
        int hash = hash(k.hashCode());
        int index = index(hash);
        Node<K, V> list = nodes[index];
        synchronized (list) {
            if (list != null && list.next == null) {
                nodes[index] = null;
                size--;
                return;
            }
            for (Node<K, V> n = list; n != null; n = n.next) {
                if (n.next.hash == hash && n.next.k.equals(k)) {
                    n.next = n.next.next;
                    size--;
                    break;
                }
            }
        }
    }

    /**
     * 计算hash桶的数组下标
     */
    private int index(int hash) {
        return hash & (bucket - 1);
    }


    @Override
    public String toString() {
        return Arrays.toString(nodes).replaceAll("null, ", "").replaceAll(", null", "").replaceAll(",null", "");
    }


}

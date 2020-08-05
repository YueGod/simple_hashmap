package com;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author ：quziwei
 * @date ：05/08/2020 01:41
 * @description：链表的每个节点
 */
@Getter
@Setter
public class Node<K, V> {
    /**
     * hash值
     */
    int hash;

    /**
     * Key
     */
    volatile K k;

    /**
     * value
     */
    volatile V v;

    /**
     * 指向下一个节点
     */
    Node<K, V> next;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?, ?> node = (Node<?, ?>) o;
        return hash == node.hash &&
                Objects.equals(k, node.k) &&
                Objects.equals(v, node.v) &&
                Objects.equals(next, node.next);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, k, v, next);
    }

    @Override
    public String toString() {
        return "{" + k + "=" + v + "," + next + "}";
    }
}

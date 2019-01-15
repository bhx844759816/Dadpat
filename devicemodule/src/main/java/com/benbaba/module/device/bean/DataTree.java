package com.benbaba.module.device.bean;

import java.util.List;

/**
 * 二级列表
 *
 * @param <K>
 * @param <V>
 */
public class DataTree<K, V> {

    private K groupItem;
    private boolean isExpand;
    private List<V> subItems;

    public DataTree(K groupItem,boolean isExpand, List<V> subItems) {
        this.groupItem = groupItem;
        this.isExpand = isExpand;
        this.subItems = subItems;
    }

    public K getGroupItem() {
        return groupItem;
    }

    public List<V> getSubItems() {
        return subItems;
    }

    public boolean isExpand() {
        return isExpand;
    }
}

package com.xjtu.dependency.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

/**
 * 前端可视化
 *
 * @author haozichen
 * @data 2020/12/1 12:39
 */

public class DependencyData {
    private String name;
    private List<DependencyData> children;
    private ItemStyle itemStyle;
    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DependencyData> getChildren() {
        return children;
    }

    public void setChildren(List<DependencyData> children) {
        this.children = children;
    }

    public ItemStyle getItemStyle() {
        return itemStyle;
    }

    public void setItemStyle(ItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }

    public DependencyData(String name, ItemStyle itemStyle, Integer value) {
        this.name = name;
        this.itemStyle = itemStyle;
        this.value = value;
    }

    public DependencyData(String name, ItemStyle itemStyle) {
        this.name = name;
        this.itemStyle = itemStyle;
    }

    public DependencyData() {
    }
}

package com.xjtu.dependency.domain;

import java.util.List;

/**
 * 前端可视化
 *
 * @author haozichen
 * @data 2020/12/3 10:29
 */

public class GraphData {
    private String name;
    private int symbolSize=70;
    private String des;
    private int category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSymbolSize() {
        return symbolSize;
    }

    public void setSymbolSize(int symbolSize) {
        this.symbolSize = symbolSize;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
    public GraphData(String name, int symbolSize, String des, int category) {
        this.name = name;
        this.symbolSize = symbolSize;
        this.des = des;
        this.category = category;
    }

    public GraphData(String name, int symbolSize, int category) {
        this.name = name;
        this.symbolSize = symbolSize;
        this.category = category;
    }

}

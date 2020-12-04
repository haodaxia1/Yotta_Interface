package com.xjtu.dependency.domain;

/**
 * 前端可视化
 *
 * @author haozichen
 * @data 2020/12/3 10:29
 */

public class GraphLink {
    private String source;
    private String target;
    private String name;
    private String des;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public GraphLink(String source, String target, String name, String des) {
        this.source = source;
        this.target = target;
        this.name = name;
        this.des = des;
    }
}

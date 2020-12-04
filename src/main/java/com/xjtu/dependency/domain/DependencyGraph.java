package com.xjtu.dependency.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 前端可视化
 *
 * @author haozichen
 * @data 2020/12/3 10:29
 */

public class DependencyGraph {
   private List<GraphData> data;
   private List<GraphLink> links;

    public List<GraphData> getData() {
        return data;
    }

    public void setData(List<GraphData> data) {
        this.data = data;
    }

    public List<GraphLink> getLinks() {
        return links;
    }

    public void setLinks(List<GraphLink> links) {
        this.links = links;
    }

    public DependencyGraph(List<GraphData> data, List<GraphLink> links) {
        this.data = data;
        this.links = links;
    }
}

package com.xjtu.dependency.domain;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemStyle {
    private String color;
    private long facetId = -1;

    private long groupId = -1;
    private String[] colors = {"#ebb40f", "#e1c315", "#b09733", "#187a2f", "#a2b029", "#718933", "#3aa255", "#a2bb2b", "#62aa3c", "#03a653", "#038549", "#28b44b",
            "#a3a830", "#7ac141", "#5e9a80", "#0aa3b5"};

    public static List<List<String>> select = new ArrayList<>();
    public static List<String> selectChild = new ArrayList<>();

    public static void init() {
        String[] fruity1 = {"#da1d23", "#dd4c51", "#e62969", "#ef2d36", "#c94a44", "#b53b54", "#dd4c51", "#e73451", "#e65656"};
        String[] fruity2 = {"#ebb40f", "#e1c315", "#fde404", "#e2631e", "#9ea718", "#e1c315", "#8eb646", "#c1ba07", "#b09733", "#ba9232"};
        String[] fruity3 = {"#187a2f", "#718933", "#a2b029", "#3aa255", "#a2bb2b", "#62aa3c", "#03a653", "#038549", "#28b44b", "#a3a830", "#7ac141"};
        String[] fruity4 = {"#0aa3b5", "#9db2b7", "#beb276", "#e65832", "#744e03", "#a3a36f", "#c9b583", "#978847", "#9d977f", "#cc7b6a"};
        String[] fruity5 = {"#c94930", "#caa465", "#dfbd7e", "#be8663", "#b9a449", "#899893", "#894810", "#ddaf61", "#b7906f", "#eb9d5f"};
        String[] fruity6 = {"#ad213e", "#794752", "#cc3d41", "#b14d57", "#c78936", "#8c292c", "#e5762e", "#a16c5a", "#9d977f", "#cc7b6a"};
        String[] fruity7 = {"#ae341f", "#d78823", "#da5c1f", "#f89a80", "#f37674", "#e75b68", "#d0545f", "#c78936", "#8c292c", "#e5762e"};
        select.add(Arrays.asList(fruity1));
        select.add(Arrays.asList(fruity2));
        select.add(Arrays.asList(fruity3));
        select.add(Arrays.asList(fruity4));
        select.add(Arrays.asList(fruity5));
        select.add(Arrays.asList(fruity6));
        selectChild = Arrays.asList(fruity7);
    }

    public ItemStyle(int c) {
        c = c % 10;
        color = selectChild.get(c);
    }

    public ItemStyle(int c, int c2) {
        c = c % 6;
        List<String> selectColor = select.get(c);
        int size = selectColor.size();
        c2 = c2 % size;
        this.color = selectColor.get(c2);
    }

    public ItemStyle(int c, int c2, long facetId) {
        c = c % 6;
        List<String> selectColor = select.get(c);
        int size = selectColor.size();
        c2 = c2 % size;
        this.color = selectColor.get(c2);
        this.facetId = facetId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getFacetId() {
        return facetId;
    }

    public void setFacetId(long facetId) {
        this.facetId = facetId;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.system.enums;

/**
 *
 * @author wmao
 */
public enum YouzanMenu {
    YOUZAN_MENU_LIST(2001, "有赞清关", "0");
    
    private int index;
    
    private String name;
    
    private String visiable;
    
    YouzanMenu(int index, String name, String visiable){
        this.index = index;
        this.name = name;
        this.visiable = visiable;
    }

    /**
     * @return the visiable
     */
    public String getVisiable() {
        return visiable;
    }

    /**
     * @param visiable the visiable to set
     */
    public void setVisiable(String visiable) {
        this.visiable = visiable;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}

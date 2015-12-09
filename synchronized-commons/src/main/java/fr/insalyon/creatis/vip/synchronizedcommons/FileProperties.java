/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.synchronizedcommons;

/**
 *
 * @author Nouha Boujelben
 */
public class FileProperties {

    private long size;
    private String md5sum;

    public FileProperties(long size, String md5sum) {
        this.size = size;
        this.md5sum = md5sum;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }
}

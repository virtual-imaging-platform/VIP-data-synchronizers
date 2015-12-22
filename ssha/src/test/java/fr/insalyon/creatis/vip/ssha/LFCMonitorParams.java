/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

import static fr.insalyon.creatis.vip.ssha.TransferTest.ua;

/**
 *
 * @author Nouha Boujelben
 */
public class LFCMonitorParams {

    int numberOfFilesTransferredToLFC;
    long sizeOfFilesTransferredToLFC;
    int numberOfFilesDeletedInLFC;
    long sizeOfFilesDeletedInLFC;

    public LFCMonitorParams(int numberOfFilesTransferredToLFC, long sizeOfFilesTransferredToLFC, int numberOfFilesDeletedInLFC, long sizeOfFilesDeletedInLFC) {
        this.numberOfFilesTransferredToLFC = numberOfFilesTransferredToLFC;
        this.sizeOfFilesTransferredToLFC = sizeOfFilesTransferredToLFC;
        this.numberOfFilesDeletedInLFC = numberOfFilesDeletedInLFC;
        this.sizeOfFilesDeletedInLFC = sizeOfFilesDeletedInLFC;
    }

    public int getNumberOfFilesTransferredToLFC() {
        return numberOfFilesTransferredToLFC;
    }

    public long getSizeOfFilesTransferredToLFC() {
        return sizeOfFilesTransferredToLFC;
    }

    public int getNumberOfFilesDeletedInLFC() {
        return numberOfFilesDeletedInLFC;
    }

    public long getSizeOfFilesDeletedInLFC() {
        return sizeOfFilesDeletedInLFC;
    }

    public void setNumberOfFilesTransferredToLFC(int numberOfFilesTransferredToLFC) {
        this.numberOfFilesTransferredToLFC = numberOfFilesTransferredToLFC;
    }

    public void setSizeOfFilesTransferredToLFC(long sizeOfFilesTransferredToLFC) {
        this.sizeOfFilesTransferredToLFC = sizeOfFilesTransferredToLFC;
    }

    public void setNumberOfFilesDeletedInLFC(int numberOfFilesDeletedInLFC) {
        this.numberOfFilesDeletedInLFC = numberOfFilesDeletedInLFC;
    }

    public void setSizeOfFilesDeletedInLFC(long sizeOfFilesDeletedInLFC) {
        this.sizeOfFilesDeletedInLFC = sizeOfFilesDeletedInLFC;
    }

}

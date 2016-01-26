/*
Copyright 2015

CREATIS
CNRS UMR 5220 -- INSERM U1044 -- Université Lyon 1 -- INSA Lyon

Authors

Nouha Boujelben (nouha.boujelben@creatis.insa-lyon.fr)
Tristan Glatard (tristan.glatard@creatis.insa-lyon.fr)

This software is a daemon for file synchronization between SFTP
servers and the LCG File Catalog (LFC).

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can use,
modify and/ or redistribute the software under the terms of the
CeCILL-B license as circulated by CEA, CNRS and INRIA at the following
URL "http://www.cecill.info".

As a counterpart to the access to the source code and rights to copy,
modify and redistribute granted by the license, users are provided
only with a limited warranty and the software's author, the holder of
the economic rights, and the successive licensors have only limited
liability.

In this respect, the user's attention is drawn to the risks associated
with loading, using, modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean that it is complicated to manipulate, and that also
therefore means that it is reserved for developers and experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards
their requirements in conditions enabling the security of their
systems and/or data to be ensured and, more generally, to use and
operate it in the same conditions as regards security.

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-B license and that you accept its terms.
*/

package fr.insalyon.creatis.vip.synchronizedcommons;

/**
 * Bean that represents a synchronization.
 *
 * @author Tristan Glatard,
 * @author Nouha Boujelben
 */
public class Synchronization {

    private final String email;
    private boolean validated;
    private final boolean synchFailed;
    private final String syncedLFCDir;
    private final TransferType transferType;
    private final boolean deleteFilesfromSource; 
    private final boolean checkFilesContent;
    private int numberOfFilesTransferredToLFC;
    private long sizeOfFilesTransferredToLFC;
    private int numberOfFilesTransferredToDevice;
    private long sizeOfFilesTransferredToDevice;
    private int numberOfFilesDeletedInLFC;
    private long sizeOfFilesDeletedInLFC;
    private int numberOfFilesDeletedInDevice;
    private long sizeOfFilesDeletedInDevice;

    public Synchronization(String email, boolean validated, boolean synchFailed, String syncedLFCDir, TransferType transferType, boolean deleteFilesfromSource,boolean checkFilesContent, int numberOfFilesTransferredToLFC, long sizeOfFilesTransferredToLFC, int numberOfFilesTransferredToDevice, long sizeOfFilesTransferredToDevice, int numberOfFilesDeletedInLFC, long sizeOfFilesDeletedInLFC, int numberOfFilesDeletedInDevice, long sizeOfFilesDeletedInDevice) {
        this.email = email;
        this.validated = validated;
        this.syncedLFCDir = syncedLFCDir;
        this.synchFailed = synchFailed;
        this.transferType = transferType;
        this.deleteFilesfromSource = deleteFilesfromSource;
        this.checkFilesContent=checkFilesContent;
        this.numberOfFilesTransferredToLFC = numberOfFilesTransferredToLFC;
        this.sizeOfFilesTransferredToLFC = sizeOfFilesTransferredToLFC;
        this.numberOfFilesTransferredToDevice = numberOfFilesTransferredToDevice;
        this.sizeOfFilesTransferredToDevice = sizeOfFilesTransferredToDevice;
        this.numberOfFilesDeletedInLFC = numberOfFilesDeletedInLFC;
        this.sizeOfFilesDeletedInLFC = sizeOfFilesDeletedInLFC;
        this.numberOfFilesDeletedInDevice = numberOfFilesDeletedInDevice;
        this.sizeOfFilesDeletedInDevice = sizeOfFilesDeletedInDevice;

    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getEmail() {
        return email;
    }

    public boolean isValidated() {
        return validated;
    }

    public String getSyncedLFCDir() {
        return syncedLFCDir;
    }

    public boolean getSynchronizationFailed() {
        return synchFailed;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public boolean getDeleteFilesfromSource() {
        return deleteFilesfromSource;
    }

    public int getNumberOfFilesTransferredToLFC() {
        return numberOfFilesTransferredToLFC;
    }

    public void setNumberOfFilesTransferredToLFC(int numberOfFilesTransferredToLFC) {
        this.numberOfFilesTransferredToLFC = numberOfFilesTransferredToLFC;
    }

    public long getSizeOfFilesTransferredToLFC() {
        return sizeOfFilesTransferredToLFC;
    }

    public void setSizeOfFilesTransferredToLFC(long sizeOfFilesTransferredToLFC) {
        this.sizeOfFilesTransferredToLFC = sizeOfFilesTransferredToLFC;
    }

    public int getNumberOfFilesTransferredToDevice() {
        return numberOfFilesTransferredToDevice;
    }

    public void setNumberOfFilesTransferredToDevice(int numberOfFilesTransferredToDevice) {
        this.numberOfFilesTransferredToDevice = numberOfFilesTransferredToDevice;
    }

    public long getSizeOfFilesTransferredToDevice() {
        return sizeOfFilesTransferredToDevice;
    }

    public void setSizeOfFilesTransferredToDevice(long sizeOfFilesTransferredToDevice) {
        this.sizeOfFilesTransferredToDevice = sizeOfFilesTransferredToDevice;
    }

    public int getNumberOfFilesDeletedInLFC() {
        return numberOfFilesDeletedInLFC;
    }

    public void setNumberOfFilesDeletedInLFC(int numberOfFilesDeletedInLFC) {
        this.numberOfFilesDeletedInLFC = numberOfFilesDeletedInLFC;
    }

    public long getSizeOfFilesDeletedInLFC() {
        return sizeOfFilesDeletedInLFC;
    }

    public void setSizeOfFilesDeletedInLFC(long sizeOfFilesDeletedInLFC) {
        this.sizeOfFilesDeletedInLFC = sizeOfFilesDeletedInLFC;
    }

    public int getNumberOfFilesDeletedInDevice() {
        return numberOfFilesDeletedInDevice;
    }

    public void setNumberOfFilesDeletedInDevice(int numberOfFilesDeletedInDevice) {
        this.numberOfFilesDeletedInDevice = numberOfFilesDeletedInDevice;
    }

    public long getSizeOfFilesDeletedInDevice() {
        return sizeOfFilesDeletedInDevice;
    }

    public void setSizeOfFilesDeletedInDevice(long sizeOfFilesDeletedInDevice) {
        this.sizeOfFilesDeletedInDevice = sizeOfFilesDeletedInDevice;
    }
    
    
     public boolean isCheckFilesContent() {
        return checkFilesContent;
    }
    @Override
    public String toString() {
        return "{" + email + " ; " + syncedLFCDir + "}";
    }

}

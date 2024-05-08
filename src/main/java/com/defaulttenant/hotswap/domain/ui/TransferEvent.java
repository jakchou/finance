package com.defaulttenant.hotswap.domain.ui;

import java.util.List;

public class TransferEvent {
    public List<String> source;
    public List<String> target;
    public List<String> transfer;
    public List<String> transferValues;

    public List<String> getSource() {
        return source;
    }

    public void setSource(List<String> source) {
        this.source = source;
    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }

    public List<String> getTransfer() {
        return transfer;
    }

    public void setTransfer(List<String> transfer) {
        this.transfer = transfer;
    }

    public List<String> getTransferValues() {
        return transferValues;
    }

    public void setTransferValues(List<String> transferValues) {
        this.transferValues = transferValues;
    }
}

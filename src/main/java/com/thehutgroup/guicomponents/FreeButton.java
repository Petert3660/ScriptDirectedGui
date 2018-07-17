package com.thehutgroup.guicomponents;

import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;

/**
 * Created by thomsonp on 05/01/2017.
 */
public class FreeButton extends JButton {

    public FreeButton(String value, int x, int y) {
        this.setText(value);
        this.setBounds(x, y, 80, 30);
    }

    @Autowired
    public FreeButton(String value, int x, int y, int size) {
        this.setText(value);
        this.setBounds(x, y, size, 30);
    }

    public void alter(String value, int x, int y, int newSize) {
        this.setText(value);
        this.setBounds(x, y, newSize, 30);
    }

    public String getButtonText() { return this.getText(); }
}

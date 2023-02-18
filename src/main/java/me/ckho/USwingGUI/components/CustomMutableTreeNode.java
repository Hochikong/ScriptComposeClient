package me.ckho.USwingGUI.components;

import javax.swing.tree.DefaultMutableTreeNode;

public class CustomMutableTreeNode extends DefaultMutableTreeNode {
    private String sV = "";
    public CustomMutableTreeNode(){}

    public CustomMutableTreeNode(Object userObject, String storageValue){
        super(userObject);
        sV = storageValue;
    }

    public String getsV() {
        return sV;
    }
}

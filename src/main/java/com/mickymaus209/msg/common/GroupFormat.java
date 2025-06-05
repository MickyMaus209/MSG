package com.mickymaus209.msg.common;

public class GroupFormat {
    private final String senderFormat;
    private final String receiverFormat;
    private final String groupName;
    private final String permission;

    public GroupFormat(String senderFormat, String receiverFormat, String groupName, String permission) {
        this.senderFormat = senderFormat;
        this.receiverFormat = receiverFormat;
        this.groupName = groupName;
        this.permission = permission;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getPermission() {
        return permission;
    }


    public String getReceiverFormat() {
        return receiverFormat;
    }

    public String getSenderFormat() {
        return senderFormat;
    }
}

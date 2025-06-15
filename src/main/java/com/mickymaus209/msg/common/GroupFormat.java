package com.mickymaus209.msg.common;

public class GroupFormat {
    private final String senderFormat;
    private final String receiverFormat;
    private final String groupName;
    private final String permission;

    /**
     * Represents the message formatting configuration for a specific user group.
     * <p>
     * This includes different formats for sender and receiver messages,
     * the group name, and the required permission to use the format.
     *
     * @param senderFormat   the format used for messages sent by this group
     * @param receiverFormat the format used for messages received by this group
     * @param groupName      the name of the group
     * @param permission     the permission required to use this format
     */
    public GroupFormat(String senderFormat, String receiverFormat, String groupName, String permission) {
        this.senderFormat = senderFormat;
        this.receiverFormat = receiverFormat;
        this.groupName = groupName;
        this.permission = permission;
    }

    /**
     * Returns the name of the group.
     *
     * @return the group name
     */
    @SuppressWarnings("unused")
    public String getGroupName() {
        return groupName;
    }

    /**
     * Returns the permission required to use this group format.
     *
     * @return the permission string
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Returns the format used for received messages in this group.
     *
     * @return the receiver format string
     */
    public String getReceiverFormat() {
        return receiverFormat;
    }

    /**
     * Returns the format used for sent messages in this group.
     *
     * @return the sender format string
     */
    public String getSenderFormat() {
        return senderFormat;
    }
}

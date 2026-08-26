package com.green.yp.message.data.model;

public record AppMessageRecord(MessageMeta meta, Message message, Long unreadCount) {}

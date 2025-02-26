package com.cobuy.constant;

public enum ManageStatus {
    PENDING("대기중"),
    ACCEPTED("수락됨"),
    REJECTED("거절됨");

    private final String description;
    private ManageStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}

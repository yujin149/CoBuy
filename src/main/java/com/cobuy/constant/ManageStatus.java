package com.cobuy.constant;

public enum ManageStatus {
    PENDING("대기중", "요청대기중"),
    ACCEPTED("수락됨", "요청수락"),
    REJECTED("거절됨", "요청거절"),
    CANCELED("취소됨", "취소됨");

    private final String description;
    private final String message;

    ManageStatus(String description, String message) {
        this.description = description;
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public String getMessage() {
        return message;
    }
}

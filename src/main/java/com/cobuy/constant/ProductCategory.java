package com.cobuy.constant;

import lombok.Getter;

@Getter
public enum ProductCategory {
    FURNITURE("가구/인테리어"),
    BOOKS("도서"),
    DIGITAL("디지털/가전"),
    LIFESTYLE("생활/건강"),
    SPORTS("스포츠/레저"),
    FOOD("식품"),
    LEISURE("여가/생활편의"),
    BABY("출산/육아"),
    FASHION("패션의류"),
    ACCESSORIES("패션잡화"),
    BEAUTY("화장품/미용");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // toString 메서드 오버라이드하여 화면에 표시될 텍스트 지정
    @Override
    public String toString() {
        return displayName;
    }
}

package com.cobuy.constant;

public enum ProductCategory {
    INTERIOR("가구/인테리어"),
    BOOKS("도서"),
    DIGITAL("디지털/가전"),
    HEALTH("생활/건강"),
    SPORTS("스포츠/레저"),
    FOOD("식품"),
    LIFE("여가/생활편의"),
    BABY("출산/육아"),
    FASHION("패션의류"),
    GOODS("패션잡화"),
    COSMETICS("화장품/미용");

    private  final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }


}

$(function() {
    $(".adminMenuWrap .depth2").hide();
    $(".adminMenuWrap .menuWrap > li > a").click(function(e) {
        let $parentLi = $(this).parent("li");

        // 서브 메뉴가 있는 경우에만 슬라이드 효과를 적용
        if ($parentLi.children(".depth2").length > 0) {
            e.preventDefault();

            $(".adminMenuWrap .menuWrap > li").removeClass("active");
            $parentLi.addClass("active");

            $(".adminMenuWrap .depth2").stop().slideUp();
            $parentLi.children(".depth2").stop().slideDown();
        }
    });
});

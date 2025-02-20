$(function(){
    $(".adminMenuWrap .depth2").hide();
    $(".adminMenuWrap .menuWrap > li > a").click(function(e) {
        e.preventDefault();
        let $parentLi = $(this).parent("li");

        $(".adminMenuWrap .menuWrap > li").removeClass("active");
        $parentLi.addClass("active");

        $(".adminMenuWrap .depth2").stop().slideUp();

        $parentLi.children(".depth2").stop().slideDown();
    });
});

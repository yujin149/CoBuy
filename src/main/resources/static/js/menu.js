$(function(){
    $('.headerWrap .menuBtn').click(function(){
       $('.menuWrap').css("display","block");
    });

    $(".menuWrap .closeBtn").click(function(){
        $('.menuWrap').css("display","none");
    });
});
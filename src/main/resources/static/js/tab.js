$(function(){
    $(".tabWrap li a").click(function(){
       let tabId = $(this).attr("href");
        //alert(tabId);

       $(".tabWrap li a").removeClass("active");
       $(this).addClass("active");

       $(".tabCont").removeClass("active");
       $(tabId).addClass("active");
    });
});
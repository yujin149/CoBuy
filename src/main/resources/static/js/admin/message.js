$(function(){
    /*안읽은 메세지 표시*/
    $('.messageWrap .messageBox .listWrap .noRead').each(function() {
        let noRead = $(this).text().trim();

        // 값이 0이거나 null, 빈 문자열인 경우
        if (noRead == "0" || noRead === "null" || noRead === "") {
            $(this).css("display", "none");
        }
    });


    /*리스트 클릭 시*/
    $(".messageWrap .messageBox .listWrap li").click(function(){
        $(".messageWrap .messageBox .listWrap li").removeClass("on");
        $(this).addClass("on");

        $(".messageWrap .listWrap").addClass("active");
        $(".messageWrap .massageRoom").addClass("active");
    });

    $(".messageWrap .closeBtn").click(function(){
        $(".messageWrap .messageBox .listWrap li").removeClass("on");
        $(".messageWrap .listWrap").removeClass("active");
        $(".messageWrap .massageRoom").removeClass("active");
    });
    
});
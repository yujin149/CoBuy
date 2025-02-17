$(function(){
    $('.chatWrap .chatList .noti').each(function() {
        let noti = $(this).text().trim();

        // 값이 0이거나 null, 빈 문자열인 경우
        if (noti == "0" || noti === "null" || noti === "") {
            $(this).css("display", "none");
        }
    });
});
$(function () {
    let today = new Date();
    let yyyy = today.getFullYear();
    let mm = today.getMonth() + 1; // 월은 0부터 시작하므로 1을 더함
    let dd = today.getDate();

    // 날짜 형식이 "yyyy-mm-dd"가 되도록 만듦
    if (mm < 10) mm = '0' + mm; // 월이 한 자릿수일 경우 앞에 0을 추가
    if (dd < 10) dd = '0' + dd; // 일이 한 자릿수일 경우 앞에 0을 추가

    let formattedDate = yyyy + '-' + mm + '-' + dd;

    // day 부분에 오늘 날짜 넣기
    $(".orderWrap .day").text(formattedDate);


    /*순위에 효과주기*/
    let index = 0;  // 현재 li 항목을 추적하는 변수
    let totalItems = $(".rankList .rank li").length; // 총 항목 수

    // 첫 번째 항목에 'on' 클래스를 추가
    function addClassToNextItem() {
        $(".rankList .rank li").removeClass('on');
        $(".rankList .rank li").eq(index).addClass('on');

        index++; // 다음 항목으로 이동

        // 마지막 항목에 도달했으면 첫 번째 항목으로 돌아가기
        if (index >= totalItems) {
            index = 0;
        }
    }

    // 1초마다 addClassToNextItem 함수 실행
    setInterval(addClassToNextItem, 1000);  // 1000ms(1초) 간격으로 실행


});
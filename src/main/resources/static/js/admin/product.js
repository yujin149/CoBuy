$(function() {
    // "직접입력" 버튼 클릭 시
    $(".prodWriteWrap .codeBtn").click(function() {
        var $ranCodeInput = $("input[name='ranCode']");
        var $inputCode = $("input[name='inputCode']");
        var $codeBtn = $(".prodWriteWrap .codeBtn");


        if ($ranCodeInput.hasClass("active")) {
            $ranCodeInput.removeClass("active");
            $inputCode.addClass("active");

            // 버튼 텍스트 변경
            $codeBtn.text("자동생성");

        } else {
            $ranCodeInput.addClass("active");
            $inputCode.removeClass("active");

            // 버튼 텍스트를 "직접입력"으로 돌아감
            $codeBtn.text("직접입력");
        }
    });

    /*옵션선택*/
    $("input[name='opt']").change(function() {
        if ($("#optON").is(":checked")) {
            $(".setOpt").addClass("active");
        } else {
            $(".setOpt").removeClass("active");
        }
    });

    /*시작일 설정*/
    let today = new Date();
    let dd = String(today.getDate()).padStart(2, '0');
    let mm = String(today.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 1을 더함
    let yyyy = today.getFullYear();

    today = yyyy + '-' + mm + '-' + dd;  // YYYY-MM-DD 형식으로 변환

    $('.startDate').val(today);  // 날짜를 input에 설정


    /*url 복사 */
    $(".copyBtn").click(function() {
        // input 요소 선택
        let copyText = $(this).siblings(".urlInput");

        // 텍스트 선택
        copyText.select();
        //setSelectionRange(start, end)는 텍스트 입력 필드나 텍스트 영역에서 텍스트 선택 범위를 지정하는 함수
        //start: 선택의 시작 위치.
        //end: 선택의 끝 위치.
        copyText[0].setSelectionRange(0, 99999);

        // 클립보드에 복사
        document.execCommand("copy");

        // 알림
        alert("URL이 복사되었습니다: " + copyText.val());
    });

});

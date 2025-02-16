$(function(){
    // 증가 버튼 클릭
    $('.countBox .plus').click(function() {
        let currentValue = $('#quantity').val();
        $('#quantity').val(parseInt(currentValue) + 1);
    });

    // 감소 버튼 클릭
    $('.countBox .minus').click(function() {
        let currentValue = $('#quantity').val();
        if (currentValue > 1) {
            $('#quantity').val(parseInt(currentValue) - 1);
        } else {
            alert("최소 구매 수량은 1개입니다.");
        }
    });

    // 직접 입력 시 0 이하의 값이 들어가면 경고
    $('#quantity').on('input', function() {
        if ($(this).val() < 1) {
            alert("최소 구매 수량은 1개입니다.");
            $(this).val(1);
        }
    });
});
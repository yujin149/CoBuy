document.addEventListener('DOMContentLoaded', function() {
    const checkIdButton = document.querySelector('.checkId'); //중복확인버튼
    const form = document.querySelector('form');
    let isIdChecked = false; // 중복 확인 여부
    
    // CSRF 토큰 가져오기
    const csrfMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    
    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
    };
    
    // CSRF 토큰이 있는 경우에만 헤더에 추가
    if (csrfMeta && csrfHeaderMeta) {
        const token = csrfMeta.getAttribute('content');
        const header = csrfHeaderMeta.getAttribute('content');
        headers[header] = token;
    }
    
    checkIdButton.addEventListener('click', function(e) {
        e.preventDefault();
        //사용자가 입력한 아이디 가져오기
        const adminId = document.querySelector('input[name="adminId"]').value;


        // 아이디 유효성 검사
        if (!adminId) {
            alert('아이디를 입력해주세요.');
            return;
        }
        
        // 아이디 길이 검사 (6~20자)
        if (adminId.length < 6 || adminId.length > 20) {
            alert('아이디는 6자에서 20자 사이로 입력해주세요.');
            return;
        }
        
        // 아이디 형식 검사 (영문, 숫자만 허용)
        if (!/^[a-zA-Z0-9]+$/.test(adminId)) {
            alert('아이디는 영문과 숫자만 사용 가능합니다.');
            return;
        }

        //fetch()사용하여 서버에 아이디 중복 여부를 확인하는 POST요청을 보낸다.
        fetch('/admin/checkId', {
            method: 'POST',
            headers: headers,
            body: 'adminId=' + encodeURIComponent(adminId) //아이디값포함
        })
            // 서버에 응답이 오면 응답 상태가 정상인지 체크한다.
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
            //중복된 아이디가 있다면 경고 메세지
        .then(data => {
            console.log('Response data:', data);
            if (data.isDuplicate) {
                alert('이미 사용중인 아이디입니다.');
                isIdChecked = false;
            } else {
                //사용가능한 아이디라면
                alert('사용 가능한 아이디입니다.');
                isIdChecked = true;
                // idChecked 필드 값을 true로 설정
                document.querySelector('input[name="idChecked"]').value = "true";
            }
        })
        .catch(error => {
            console.error('Error details:', error);
            alert('중복 확인 중 오류가 발생했습니다.');
            isIdChecked = false;
        });
    });

    // 아이디 입력 필드 값이 변경되면 중복 확인 초기화
    //새로운 아이디를 입력하면 중복확인을 해야한다.
    document.querySelector('input[name="adminId"]').addEventListener('input', function() {
        isIdChecked = false;
        document.querySelector('input[name="idChecked"]').value = "false";
    });

    // 폼 제출 시 중복 확인 여부 체크
    form.addEventListener('submit', function(e) {
        if (!isIdChecked) {
            e.preventDefault();
            alert('아이디 중복 확인을 해주세요.');
        }
    });
}); 
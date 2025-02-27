// 현재 매칭된 파트너 ID를 저장할 변수
let currentMatchedPartnerId = null;

// 이미 표시된 파트너의 ID를 저장할 배열
let shownPartnerIds = [];

// 매칭 모달 초기화 및 데이터 로드
function initializeMatchModal(role) {
    const currentUserId = document.getElementById('currentUserId').value;
    const endpoint = role === 'ADMIN' ? '/seller/search' : '/admin/search';

    // CSRF 토큰 가져오기
    const token = document.getElementById("csrf-token").value;
    const header = document.getElementById("csrf-header").value;

    // 검색 파라미터 설정
    const params = new URLSearchParams();

    if (role === 'ADMIN') {
        params.append('searchType', '인플루언서명');
        params.append('sellerNickName', ''); // 빈 검색어로 전체 검색
    } else {
        params.append('searchType', '쇼핑몰명');
        params.append('adminShopName', ''); // 빈 검색어로 전체 검색
    }
    params.append('currentUserId', currentUserId);

    console.log('Fetching from endpoint:', endpoint, 'with params:', params.toString());

    // API 호출
    fetch(`${endpoint}?${params.toString()}`, {
        headers: {
            [header]: token,
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Received data:', data);
            if (data && data.currentUserCategories && data.partners) {
                // 현재 사용자의 카테고리와 파트너 목록 분리
                const currentUserCategories = data.currentUserCategories;
                const partners = data.partners;

                console.log('Current user categories:', currentUserCategories);
                console.log('Available partners:', partners);

                if (partners.length > 0) {
                    // 카테고리가 일치하는 파트너만 필터링
                    const matchedPartners = filterMatchingPartners(partners, currentUserCategories);
                    console.log('Matched partners:', matchedPartners);

                    if (matchedPartners.length > 0) {
                        // 아직 보여주지 않은 파트너만 필터링
                        const remainingPartners = matchedPartners.filter(partner =>
                            !shownPartnerIds.includes(role === 'ADMIN' ? partner.sellerId : partner.adminId)
                        );

                        if (remainingPartners.length > 0) {
                            displayRandomPartner(remainingPartners, role);
                        } else {
                            alert('더 이상 매칭할 파트너가 없습니다.');
                            $('.matchModal').hide();
                            shownPartnerIds = [];
                        }
                    } else {
                        showNoMatchMessage(role);
                    }
                } else {
                    showNoMatchMessage(role);
                }
            } else {
                showNoMatchMessage(role);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showErrorMessage(role);
        });
}

// 카테고리가 일치하는 파트너 필터링
function filterMatchingPartners(partners, currentUserCategories) {
    if (!currentUserCategories || !Array.isArray(currentUserCategories) || currentUserCategories.length === 0) {
        console.warn('No valid categories found for current user');
        return [];
    }

    const matchedPartners = partners.filter(partner => {
        console.log('Checking partner:', partner);

        // 파트너의 카테고리 데이터 확인
        const partnerCategories = partner.productCategories;
        console.log('Partner productCategories:', partnerCategories);

        // 파트너의 카테고리가 유효한지 확인
        if (!partnerCategories || !Array.isArray(partnerCategories) || partnerCategories.length === 0) {
            console.warn('No valid categories found for partner:', partner.sellerId || partner.adminId);
            return false;
        }

        // 카테고리 매칭 확인
        const matchingCategories = currentUserCategories.filter(userCategory =>
            partnerCategories.includes(userCategory)  // 서버에서 이미 정확한 카테고리 문자열을 받으므로 단순 비교
        );

        console.log('Matching categories:', matchingCategories);
        return matchingCategories.length > 0;
    });

    console.log('Total partners before filtering:', partners.length);
    console.log('Matched partners after filtering:', matchedPartners.length);

    return matchedPartners;
}

// 현재 사용자의 카테고리 가져오기
function getCurrentUserCategories() {
    let categoriesElements;
    let source = '';

    // 현재 페이지에서 내 정보의 카테고리만 정확하게 선택
    categoriesElements = document.querySelectorAll('#myInfo .productCategories li, .myInfo .productCategories li');
    console.log('1. My categories selector 결과:', {
        found: categoriesElements.length > 0,
        elements: Array.from(categoriesElements).map(el => el.textContent)
    });

    if (categoriesElements.length > 0) {
        source = '#myInfo .productCategories li';
    } else {
        // 백업 선택자: 현재 사용자 영역의 카테고리
        categoriesElements = document.querySelectorAll('.currentUser .category li, .myProfile .category li');
        console.log('2. Backup selector 결과:', {
            found: categoriesElements.length > 0,
            elements: Array.from(categoriesElements).map(el => el.textContent)
        });

        if (categoriesElements.length > 0) {
            source = '.currentUser .category li';
        }
    }

    // 카테고리를 찾지 못한 경우 디버깅
    if (!categoriesElements.length) {
        console.log('DOM 구조 확인:', {
            myInfo: document.querySelector('#myInfo, .myInfo'),
            productCategories: document.querySelectorAll('.productCategories'),
            allCategories: document.querySelectorAll('[class*="category"]')
        });
        console.error('카테고리 요소를 찾을 수 없습니다.');
        return [];
    }

    // 중복 제거하고 카테고리 추출
    const categories = Array.from(new Set(
        Array.from(categoriesElements)
            .map(li => li.textContent.trim())
            .filter(category => category && category.length > 0)
    ));

    console.log('최종 카테고리 추출 결과:', {
        source: source,
        categories: categories
    });

    return categories;
}

// 랜덤 파트너 표시
function displayRandomPartner(partners, role) {
    const randomIndex = Math.floor(Math.random() * partners.length);
    const partner = partners[randomIndex];
    const modalClass = role === 'ADMIN' ? '.matchSellerModal' : '.matchAdminModal';
    const modal = document.querySelector(modalClass);

    console.log('Displaying partner:', partner);
    console.log('Using modal:', modalClass);

    if (!modal) {
        console.error('모달을 찾을 수 없습니다.');
        return;
    }

    currentMatchedPartnerId = role === 'ADMIN' ? partner.sellerId : partner.adminId;

    // 표시된 파트너 ID 저장
    shownPartnerIds.push(currentMatchedPartnerId);

    // 모달 내용 업데이트
    const titleElement = modal.querySelector('.titWrap .tit');
    const categoryElement = modal.querySelector('.category');
    const infoBoxElement = modal.querySelector('.infoBox');
    const snsLinkElement = modal.querySelector('.snsLink');

    if (titleElement) {
        const name = role === 'ADMIN' ? partner.sellerNickName : partner.adminShopName;
        titleElement.textContent = name || '이름 없음';
        console.log('Updated title:', name);
    }

    if (categoryElement) {
        const categoryHtml = partner.productCategories
            .map(cat => `<li>${cat}</li>`).join('');
        categoryElement.innerHTML = categoryHtml;
        console.log('Updated categories:', categoryHtml);
    }

    if (infoBoxElement) {
        console.log('Partner data:', partner);

        let contents;
        if (role === 'ADMIN') {
            contents = partner.sellerContents;
        } else {
            contents = partner.adminContents;
        }

        // 빈 값 체크를 더 엄격하게 수정
        infoBoxElement.textContent = (contents !== undefined && contents !== null && contents !== '')
            ? contents
            : '소개글이 없습니다.';

        console.log('Role:', role);
        console.log('Contents field:', contents);
    }

    if (snsLinkElement) {
        const url = role === 'ADMIN' ? partner.sellerUrl : partner.adminUrl;
        if (url) {
            // URL에 프로토콜이 있는지 확인하고 없으면 추가
            const formattedUrl = url.startsWith('http://') || url.startsWith('https://')
                ? url
                : `https://${url}`;

            snsLinkElement.href = formattedUrl;
            snsLinkElement.textContent = url; // 표시되는 텍스트는 원래 URL 유지
            snsLinkElement.style.pointerEvents = 'auto';
            snsLinkElement.style.cursor = 'pointer';
            snsLinkElement.target = '_blank'; // 새 탭에서 열기
        } else {
            snsLinkElement.removeAttribute('href');
            snsLinkElement.textContent = '등록된 URL이 없습니다.';
            snsLinkElement.style.pointerEvents = 'none';
            snsLinkElement.style.cursor = 'default';
        }
    }

    // 모달 표시
    $(modalClass).show();
}

// 매칭 없음 메시지 표시
function showNoMatchMessage(role) {
    const modalClass = role === 'ADMIN' ? '.matchSellerModal' : '.matchAdminModal';
    const modal = document.querySelector(modalClass);
    if (modal) {
        modal.querySelector('.modalCont').innerHTML =
            '<p class="none">매칭 가능한 파트너가 없습니다.</p>';
        $(modalClass).show();
    }
}

// 에러 메시지 표시
function showErrorMessage(role) {
    const modalClass = role === 'ADMIN' ? '.matchSellerModal' : '.matchAdminModal';
    const modal = document.querySelector(modalClass);
    if (modal) {
        modal.querySelector('.modalCont').innerHTML =
            '<p class="none">매칭 중 오류가 발생했습니다.</p>';
        $(modalClass).show();
    }
}

// 이벤트 리스너 등록
$(document).ready(function() {
    // 매칭 버튼 클릭 이벤트
    $('.matchAdmin, .matchSeller').click(function() {
        const role = $(this).hasClass('matchAdmin') ? 'SELLER' : 'ADMIN';
        // 새로운 매칭 시작 시 shownPartnerIds 초기화
        shownPartnerIds = [];
        initializeMatchModal(role);
    });

    // 다시 매칭하기 버튼 클릭 이벤트
    $('.rematchBtn').click(function() {
        const role = $(this).closest('.matchAdminModal').length > 0 ? 'SELLER' : 'ADMIN';
        initializeMatchModal(role);
    });

    // 협업 요청하기 버튼 클릭 이벤트
    $('.requestMatchBtn').click(function() {
        if (!currentMatchedPartnerId) {
            alert('매칭된 파트너가 없습니다.');
            return;
        }

        const currentUserId = $('#currentUserId').val();
        const role = $(this).closest('.matchAdminModal').length > 0 ? 'SELLER' : 'ADMIN';

        // 협업 요청 API 호출
        fetch('/manage/request', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [document.getElementById("csrf-header").value]: document.getElementById("csrf-token").value
            },
            body: `requesterId=${currentUserId}&targetId=${currentMatchedPartnerId}&role=${role}`
        })
            .then(response => {
                if (response.ok) {
                    alert('협업 요청이 전송되었습니다.');
                    // 매칭 모달 닫고 요청 모달 열기
                    $('.matchModal').hide();
                    $('.requestModal').show();
                    // 요청 목록 새로고침
                    loadRequestModalData();
                } else {
                    alert('협업 요청 전송에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('협업 요청 처리 중 오류가 발생했습니다.');
            });
    });

    // 닫기 버튼 클릭 이벤트
    $('.closeBtn').click(function() {
        $(this).closest('.modal').hide();
    });
});

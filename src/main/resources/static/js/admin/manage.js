function updatePendingRequestCount() {
    const currentUserId = document.getElementById('currentUserId').value;
    const currentUserRole = document.getElementById('currentUserRole').value;

    fetch(`/manage/pending-count?userId=${currentUserId}&role=${currentUserRole}`)
        .then(response => response.json())
        .then(count => {
            const alarmNum = document.querySelector('.alarmNum');
            if (alarmNum) {
                if (count > 0) {
                    alarmNum.textContent = count;
                    alarmNum.style.display = 'flex';
                } else {
                    alarmNum.style.display = 'none';
                }
            }
        })
        .catch(error => console.error('Error:', error));
}

// 검색 관련 변수 추가
let searchType = '전체';
let searchKeyword = '';

// 검색 폼 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', function() {
    updatePendingRequestCount();
    // 파트너 리스트 페이지인 경우에만 실행
    if (document.querySelector('.manageWrap')) {
        loadPartnerList();
        setupUrlCopy();
        setupSearch(); // 검색 설정 추가
    }
    // 주기적으로 카운트 업데이트 (5분마다)
    setInterval(updatePendingRequestCount, 300000);
});

let partnerCurrentPage = 0;
const partnerItemsPerPage = 10;

// 검색 설정 함수
function setupSearch() {
    const searchForm = document.querySelector('.searchWrap form');
    if (!searchForm) return;

    const searchSelect = searchForm.querySelector('select');
    const searchInput = searchForm.querySelector('input');

    // 현재 페이지가 어떤 관리 페이지인지 확인
    const isShopManage = document.querySelector('.shopMangeWrap');
    const isSellerManage = document.querySelector('.sellerMangeWrap');

    // select 옵션 설정
    if (isShopManage) {
        searchSelect.innerHTML = `
            <option value="전체">전체</option>
            <option value="쇼핑몰명">쇼핑몰명</option>
            <option value="카테고리">카테고리</option>
        `;
    } else if (isSellerManage) {
        searchSelect.innerHTML = `
            <option value="전체">전체</option>
            <option value="인플루언서명">인플루언서명</option>
            <option value="카테고리">카테고리</option>
        `;
    }

    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        searchType = searchSelect.value;
        searchKeyword = searchInput.value.trim();
        loadPartnerList(0); // 검색 시 첫 페이지부터 시작
    });
}

function loadPartnerList(page = 0) {
    const currentUserId = document.getElementById('currentUserId').value;
    const currentUserRole = document.getElementById('currentUserRole').value;

    fetch(`/api/manage/partners?userId=${currentUserId}&role=${currentUserRole}&page=${page}&size=${partnerItemsPerPage}`)
        .then(response => response.json())
        .then(data => {
            const tbody = document.querySelector('.tableWrap tbody');
            if (!data.content || data.content.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5">등록된 파트너가 없습니다.</td></tr>';
                renderPartnerPagination(0, 1);
                return;
            }

            // 검색 필터링 적용
            let filteredContent = data.content;
            if (searchKeyword) {
                filteredContent = data.content.filter(partner => {
                    const partnerInfo = currentUserRole === 'ADMIN' ? {
                        name: partner.sellerNickName || '',
                        categories: partner.sellerCategories || []
                    } : {
                        name: partner.adminShopName || '',
                        categories: partner.adminCategories || []
                    };

                    switch (searchType) {
                        case '전체':
                            return partnerInfo.name.toLowerCase().includes(searchKeyword.toLowerCase()) ||
                                partnerInfo.categories.some(cat =>
                                    cat.toLowerCase().includes(searchKeyword.toLowerCase()));
                        case '쇼핑몰명':
                        case '인플루언서명':
                            return partnerInfo.name.toLowerCase().includes(searchKeyword.toLowerCase());
                        case '카테고리':
                            return partnerInfo.categories.some(cat =>
                                cat.toLowerCase().includes(searchKeyword.toLowerCase()));
                        default:
                            return true;
                    }
                });
            }

            if (filteredContent.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5">검색 결과가 없습니다.</td></tr>';
                renderPartnerPagination(0, 1);
                return;
            }

            // 전체 아이템 수 계산
            const totalItems = filteredContent.length;

            tbody.innerHTML = filteredContent.map((partner, index) => {
                // 역순 번호 계산
                const reverseNumber = totalItems - (page * partnerItemsPerPage) - index;

                const partnerInfo = currentUserRole === 'ADMIN' ? {
                    name: partner.sellerNickName || '이름 없음',
                    url: partner.sellerUrl || '#',
                    categories: partner.sellerCategories || []
                } : {
                    name: partner.adminShopName || '이름 없음',
                    url: partner.adminUrl || '#',
                    categories: partner.adminCategories || []
                };

                // 카테고리 목록이 배열인지 확인하고 처리
                const categoryList = Array.isArray(partnerInfo.categories)
                    ? partnerInfo.categories
                    : [];

                return `
                    <tr>
                        <td>${reverseNumber}</td>
                        <td><p class="name">${partnerInfo.name}</p></td>
                        <td>
                            <ul class="category">
                                ${categoryList.map(cat => `<li>${cat}</li>`).join('')}
                            </ul>
                        </td>
                        <td>
                            <div class="link">
                                <input type="text" value="${partnerInfo.url}" class="urlInput" readonly>
                                <a href="javascript:void(0)" class="copyBtn">URL 복사</a>
                                <a href="${formatUrl(partnerInfo.url)}" target="_blank" class="moveBtn">바로가기</a>
                            </div>
                        </td>
                        <td>
                            <div class="btnBox">
                                <a href="javascript:void(0)" class="info${currentUserRole === 'ADMIN' ? 'Seller' : 'Admin'}" 
                                   onclick="showPartnerInfo('${partner.id}')">상세보기</a>
                                <a href="javascript:void(0)" class="deleteBtn" 
                                   onclick="cancelPartnership('${partner.id}')">삭제</a>
                            </div>
                        </td>
                    </tr>
                `;
            }).join('');

            // 필터링된 결과에 맞춰 페이지네이션 업데이트
            const totalPages = Math.ceil(totalItems / partnerItemsPerPage);
            renderPartnerPagination(totalPages, page + 1);
        })
        .catch(error => {
            console.error('Error loading partner list:', error);
            const tbody = document.querySelector('.tableWrap tbody');
            tbody.innerHTML = '<tr><td colspan="5">데이터를 불러오는데 실패했습니다.</td></tr>';
        });
}

function renderPartnerPagination(totalPages, currentPage) {
    const pagination = document.querySelector('.manageWrap .pagination');
    if (!pagination) return;

    totalPages = Math.max(1, totalPages); // 최소 1페이지 보장
    let paginationHTML = `
        <li class="page-item page-arrow page-prev">
            <a class="page-link" href="javascript:void(0)" 
               onclick="${currentPage > 1 ? `loadPartnerList(${currentPage - 2})` : ''}"
               style="cursor: ${currentPage <= 1 ? 'default' : 'pointer'}"
               ${currentPage <= 1 ? 'disabled' : ''}>이전 페이지</a>
        </li>
        <li class="page-item page-num">
    `;

    for (let i = 1; i <= totalPages; i++) {
        paginationHTML += `
            <a class="page-link ${i === currentPage ? 'active' : ''}" 
               href="javascript:void(0)" 
               onclick="loadPartnerList(${i - 1})">${i}</a>
        `;
    }

    paginationHTML += `
        </li>
        <li class="page-item page-arrow page-next">
            <a class="page-link" href="javascript:void(0)" 
               onclick="${currentPage < totalPages ? `loadPartnerList(${currentPage})` : ''}"
               style="cursor: ${currentPage >= totalPages ? 'default' : 'pointer'}"
               ${currentPage >= totalPages ? 'disabled' : ''}>다음 페이지</a>
        </li>
    `;

    pagination.innerHTML = paginationHTML;
}

function formatUrl(url) {
    if (!url) return '#';
    if (!/^https?:\/\//i.test(url)) {
        return 'https://' + url;
    }
    return url;
}

function cancelPartnership(manageId) {
    if (!confirm('협업을 취소하시겠습니까?')) return;

    fetch(`/api/manage/cancel?manageId=${manageId}`, {
        method: 'POST',
        headers: {
            [document.getElementById('csrf-header').value]: document.getElementById('csrf-token').value
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('취소 처리 중 오류가 발생했습니다.');
            }
            return response.json();
        })
        .then(data => {
            alert('협업이 취소되었습니다.');
            loadPartnerList();
        })
        .catch(error => alert(error.message));
}

function setupUrlCopy() {
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('copyBtn')) {
            const input = e.target.previousElementSibling;
            input.select();
            document.execCommand('copy');
            alert('URL이 복사되었습니다: ' + input.value);
        }
    });
}
// 파트너 상세 정보 모달 표시
function showPartnerInfo(partnerId) {
    const currentUserRole = document.getElementById('currentUserRole').value;

    fetch(`/api/manage/partner/${partnerId}`)
        .then(response => response.json())
        .then(partner => {
            // 모달 요소 선택 (ADMIN/SELLER에 따라 다른 모달 선택)
            const modalClass = currentUserRole === 'ADMIN' ? '.infoSellerModal' : '.infoAdminModal';
            const modal = document.querySelector(modalClass);

            if (modal) {
                // 파트너 정보에 따라 모달 내용 업데이트
                const partnerInfo = currentUserRole === 'ADMIN' ? {
                    name: partner.sellerNickName,
                    categories: partner.sellerCategories,
                    contents: partner.sellerContents || '소개글이 없습니다.',
                    url: partner.sellerUrl
                } : {
                    name: partner.adminShopName,
                    categories: partner.adminCategories,
                    contents: partner.adminContents || '소개글이 없습니다.',
                    url: partner.adminUrl
                };

                // 모달 내용 업데이트
                modal.querySelector('.titWrap .tit').textContent = partnerInfo.name;
                modal.querySelector('.category').innerHTML =
                    partnerInfo.categories.map(cat => `<li>${cat}</li>`).join('');
                modal.querySelector('.infoBox').textContent = partnerInfo.contents;

                const snsLink = modal.querySelector('.snsLink');
                const formattedUrl = formatUrl(partnerInfo.url);
                snsLink.href = formattedUrl;
                snsLink.textContent = partnerInfo.url;

                // 모달 표시
                modal.style.display = 'block';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('파트너 정보를 불러오는데 실패했습니다.');
        });
}
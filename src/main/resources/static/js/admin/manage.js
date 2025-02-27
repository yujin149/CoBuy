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

// 페이지 로드 시 실행할 함수들
document.addEventListener('DOMContentLoaded', function() {
    updatePendingRequestCount();
    // 파트너 리스트 페이지인 경우에만 실행
    if (document.querySelector('.manageWrap')) {
        loadPartnerList();
        setupUrlCopy();
    }
    // 주기적으로 카운트 업데이트 (5분마다)
    setInterval(updatePendingRequestCount, 300000);
});

let partnerCurrentPage = 0;
const partnerItemsPerPage = 10;

function loadPartnerList(page = 0) {
    const currentUserId = document.getElementById('currentUserId').value;
    const currentUserRole = document.getElementById('currentUserRole').value;

    fetch(`/api/manage/partners?userId=${currentUserId}&role=${currentUserRole}&page=${page}&size=${partnerItemsPerPage}`)
        .then(response => response.json())
        .then(data => {
            const tbody = document.querySelector('.tableWrap tbody');
            if (!data.content || data.content.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5">등록된 파트너가 없습니다.</td></tr>';
                renderPartnerPagination(0, 1); // 데이터가 없어도 1페이지는 보이도록
                return;
            }

            tbody.innerHTML = data.content.map((partner, index) => {
                const partnerInfo = currentUserRole === 'ADMIN' ? {
                    name: partner.sellerNickName,
                    url: partner.sellerUrl,
                    categories: partner.sellerCategories
                } : {
                    name: partner.adminShopName,
                    url: partner.adminUrl,
                    categories: partner.adminCategories
                };

                return `
                    <tr>
                        <td>${index + 1 + (page * partnerItemsPerPage)}</td>
                        <td><p class="name">${partnerInfo.name}</p></td>
                        <td>
                            <ul class="category">
                                ${partnerInfo.categories.map(cat => `<li>${cat}</li>`).join('')}
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

            renderPartnerPagination(data.totalPages, page + 1);
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
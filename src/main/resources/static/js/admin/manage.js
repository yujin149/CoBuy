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

// 페이지 로드 시 카운트 업데이트
document.addEventListener('DOMContentLoaded', updatePendingRequestCount);

// 주기적으로 카운트 업데이트 (선택사항, 5분마다)
setInterval(updatePendingRequestCount, 300000);
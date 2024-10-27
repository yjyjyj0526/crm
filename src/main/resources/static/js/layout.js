document.addEventListener('DOMContentLoaded', function() {
        console.log("DOM fully loaded and parsed.");

        // `htmlUserId` 요소 확인
        const htmlUserIdElement = document.getElementById('htmlUserId');
        console.log("HTML에서의 user_id: ", htmlUserIdElement ? htmlUserIdElement.textContent : "undefined");

        console.log("Inside layout.js, userId:", userId); // JS가 사용자 ID를 잘 가져왔는지 확인

        const sidebar = document.querySelector('#sidebar');
        const hamburger = document.querySelector('.hamburger');
        const content = document.querySelector('main.content');
        const header = document.querySelector('header');
        const profileIcon = document.querySelector('.profile-icon');
        const profileMenu = document.querySelector('#profileMenu');

        hamburger.addEventListener('click', function() {
                sidebar.classList.toggle('expanded');
                content.classList.toggle('collapsed');
                header.classList.toggle('expanded');
        });

        profileIcon.addEventListener('click', function(e) {
                e.stopPropagation();  // 이벤트 버블링 방지
                profileIcon.classList.toggle('active');
        });

        document.addEventListener('click', function(e) {
                // 클릭된 영역이 프로필 아이콘이나 프로필 메뉴가 아닌 경우
                if (!profileIcon.contains(e.target) && profileIcon.classList.contains('active')) {
                        profileIcon.classList.remove('active');
                }
        });

        // 프로필 이미지 불러오기
        fetchProfileImage(userId); // userId 변수를 사용
});

function fetchProfileImage(user_id) {
        fetch(`/user/details/${user_id}`, {
                method: 'GET',
                headers: {
                        'Content-Type': 'application/json'
                }
        })
            .then(response => response.json())
            .then(data => {
                    if (data.profile_image_base64) {
                            const profileImage = document.getElementById('profileImage');
                            profileImage.src = `data:image/png;base64,${data.profile_image_base64}`;
                    }
            })
            .catch(err => console.error('Error fetching profile image:', err));
}
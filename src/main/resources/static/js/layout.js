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

        // 애니메이션 비활성화
        sidebar.classList.add('no-transition');
        content.classList.add('no-transition');
        header.classList.add('no-transition');

        // 페이지 로드 시 로컬 스토리지로부터 상태를 불러오기
        if (localStorage.getItem("sidebarState") === "expanded") {
                sidebar.classList.add('expanded');
                content.classList.add('collapsed');
                header.classList.add('expanded');
        }

        // 애니메이션 활성화 (다음 프레임에서)
        requestAnimationFrame(() => {
                sidebar.classList.remove('no-transition');
                content.classList.remove('no-transition');
                header.classList.remove('no-transition');
        });

        // 햄버거 버튼 클릭 시 상태 토글 및 로컬 스토리지에 저장
        hamburger.addEventListener('click', function() {
                if (sidebar.classList.contains('expanded')) {
                        sidebar.classList.remove('expanded');
                        content.classList.remove('collapsed');
                        header.classList.remove('expanded');
                        localStorage.setItem("sidebarState", "collapsed");
                } else {
                        sidebar.classList.add('expanded');
                        content.classList.add('collapsed');
                        header.classList.add('expanded');
                        localStorage.setItem("sidebarState", "expanded");
                }
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
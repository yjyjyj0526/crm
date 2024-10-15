document.getElementById('sidebarToggle').addEventListener('click', function() {
        const sidebar = document.getElementById('sidebar');
        const navbar = document.querySelector('.navbar');
        const mainContent = document.getElementById('main-content');

        sidebar.classList.toggle('collapsed');
        navbar.classList.toggle('collapsed');
        mainContent.classList.toggle('collapsed');

        // 버튼 아이콘 변경
        const icon = this.querySelector('i');
        icon.classList.toggle('bi-chevron-left');
        icon.classList.toggle('bi-chevron-right');

        // 접힌 상태에서 아이콘 변경
        if (sidebar.classList.contains('collapsed')) {
                icon.classList.remove('bi-chevron-left');
                icon.classList.add('bi-chevron-right'); // 접혔을 때 아이콘을 '>'로 변경
        } else {
                icon.classList.remove('bi-chevron-right');
                icon.classList.add('bi-chevron-left'); // 펼쳐졌을 때 아이콘을 '<'로 변경
        }
});

// 하위 메뉴 토글 기능
function toggleSubMenu(menuId) {
        const menu = document.getElementById(menuId);
        menu.classList.toggle('show');
}

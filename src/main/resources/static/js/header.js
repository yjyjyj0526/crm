const sidebar = document.getElementById('sidebar');
const header = document.querySelector('header');
const content = document.querySelector('.content');

function toggleSidebar() {
        const isExpanded = sidebar.classList.toggle('expanded'); // 사이드바의 'expanded' 클래스 토글
        header.classList.toggle('expanded', isExpanded); // 헤더의 'expanded' 클래스 토글
        content.classList.toggle('expanded', isExpanded); // 콘텐츠의 'expanded' 클래스 토글
}

function toggleSubMenu(menuId) {
        const menu = document.getElementById(menuId);
        menu.classList.toggle('collapse'); // 서브메뉴의 'collapse' 클래스 토글
}
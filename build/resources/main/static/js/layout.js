// layout.js
document.addEventListener('DOMContentLoaded', function() {
        const sidebar = document.querySelector('#sidebar');
        const hamburger = document.querySelector('.hamburger');
        const content = document.querySelector('main.content');
        const header = document.querySelector('header');

        hamburger.addEventListener('click', function() {
                sidebar.classList.toggle('expanded');
                content.classList.toggle('collapsed');
                header.classList.toggle('expanded');
        });
});
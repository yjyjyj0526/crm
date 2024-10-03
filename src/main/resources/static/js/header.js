    document.getElementById('sidebarToggle').addEventListener('click', function() {
        var sidebar = document.getElementById('sidebar');
        var mainContent = document.getElementById('main-content');
        var navbar = document.querySelector('.navbar');

        sidebar.classList.toggle('collapsed');
        mainContent.classList.toggle('collapsed');
        navbar.classList.toggle('collapsed');

        this.classList.toggle('collapsed');
    });
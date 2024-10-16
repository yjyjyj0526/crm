$(document).ready(function() {
    $('#registerButton').on('click', function() {
        // 슬라이드 효과를 위한 등록 폼 보이기
        $('#registrationForm').slideToggle();
        // 리스트의 너비 조정
        $('#userTable').toggleClass('shrink');
    });

    $('#cancelButton').on('click', function() {
        // 등록 폼 숨기기
        $('#registrationForm').slideUp();
        // 리스트의 너비 원래대로 돌리기
        $('#userTable').removeClass('shrink');
    });
});

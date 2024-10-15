$(document).ready(function() {
    // 바이트 길이를 제한하는 함수
    function limitByteLength(element, maxBytes) {
        element.on('input', function() {
            let text = element.val();
            let encodedText = new TextEncoder().encode(text);
            if (encodedText.length > maxBytes) {
                while (encodedText.length > maxBytes) {
                    text = text.slice(0, -1);
                    encodedText = new TextEncoder().encode(text);
                }
                element.val(text);
            }
        });
    }

    // 입력값 패턴을 제한하는 함수
    function restrictInputToPattern(element, pattern, errorElement) {
        element.on('input', function() {
            const regex = new RegExp(pattern);
            if (!regex.test(element.val())) {
                element.val(element.val().replace(/[^a-zA-Z0-9]/g, ''));
                errorElement.show();
            } else {
                errorElement.hide();
            }
        });
    }

    // 사용자 ID에 대한 패턴 및 바이트 제한 적용
    restrictInputToPattern($('#user_id'), '^[a-zA-Z0-9]*$', $('#userIDError'));
    limitByteLength($('#user_id'), 20);
    limitByteLength($('#user_name'), 20);
    limitByteLength($('#password'), 50);

    // AJAX 요청 처리
    $('#joinForm').on('submit', function(event) {
        event.preventDefault(); // 기본 제출 방지

        const formData = {
            user_id: $('#user_id').val(),
            user_name: $('#user_name').val(),
            password: $('#password').val(),
            phone_number: $('#phone_number').val(),
            department: $('#department').val(),
            authority: $('#authority').val(),
            register_member_id: $('input[name="register_member_id"]').val()
        };

        $.ajax({
            type: 'POST',
            url: '/user/join',
            contentType: 'application/json',
            data: JSON.stringify(formData), // JSON 형식으로 데이터 전송
            success: function(response) {
                $('#successModal').modal('show');
                $('#successModal').on('hidden.bs.modal', function () {
                    window.location.href = '/'; // 성공 시 리디렉션
                });
            },
            error: function(xhr) {
                console.log(xhr.responseText); // 오류 메시지 출력
                if (xhr.status === 409) {
                    $('#userExistsModal').modal('show'); // 사용자 ID가 존재하는 경우
                } else {
                    $('#errorModal').modal('show'); // 일반 오류
                }
            }
        });
    });
});

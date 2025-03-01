$(function(){
    tinymce.init({
        selector : 'textarea#editorTxt',
        plugins: 'advlist autolink lists link image charmap print preview searchreplace wordcount visualblocks code',
        toolbar: 'undo redo | formatselect | bold italic underline strikethrough | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image | code',
        menubar: false,
        file_picker_types: 'image',  // 이미지 파일만 선택
        height:300,
        content_css: '//www.tiny.cloud/css/codepen.min.css',  // 에디터의 스타일
        images_upload_url: '/admin/upload/editor/image',
        automatic_uploads: true,
        images_upload_handler: function (blobInfo, success, failure) {
            var formData = new FormData();
            formData.append('file', blobInfo.blob());

            // CSRF 토큰 가져오기
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");

            $.ajax({
                url: '/admin/upload/editor/image',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                beforeSend: function(xhr) {
                    if(token && header) {
                        xhr.setRequestHeader(header, token);
                    }
                },
                success: function(response) {
                    success(response.location);
                },
                error: function() {
                    failure('이미지 업로드에 실패했습니다.');
                }
            });
        }
    });
});


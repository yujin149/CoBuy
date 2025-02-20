<!-- TinyMCE init 스크립트 추가 -->
$(function(){
    tinymce.init({
        selector : 'textarea#editorTxt',
        plugins: 'advlist autolink lists link image charmap print preview searchreplace wordcount visualblocks code',
        toolbar: 'undo redo | formatselect | bold italic underline strikethrough | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image | code',
        menubar: false,
        file_picker_types: 'image',  // 이미지 파일만 선택
        height:300,
        content_css: '//www.tiny.cloud/css/codepen.min.css',  // 에디터의 스타일
    });
});


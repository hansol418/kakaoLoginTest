document.getElementById("imageInput").addEventListener("change", function() {
    const file = this.files[0];

    if (file) {
        // 이미지 미리보기 기능
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById("previewImage").src = e.target.result;
        }
        reader.readAsDataURL(file);

        // FormData 생성 및 이미지 업로드 처리
        const formData = new FormData();
        formData.append("image", file);

        fetch("/classify", {
            method: "POST",
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    alert(data.error);
                } else {
                    // predictedLabel만 표시
                    document.getElementById("predictedLabel").textContent = `분석 결과: ${data.predictedLabel}`;
                    // YouTube URL 업데이트
                    document.getElementById("videoFrame").src = data.videoUrl;
                }
            })
            .catch(error => console.error("Error:", error));
    }
});

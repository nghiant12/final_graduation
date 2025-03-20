// Image preview
function previewImage(input) {
    let file = input.files[0];
    let preview = input.closest(".mb-3").querySelector("img#preview");

    if (file) {
        let reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.style.display = "block";
        };
        reader.readAsDataURL(file);
    } else {
        preview.src = "";
        preview.style.display = "none";
    }
}

document.body.addEventListener("change", function (event) {
    if (event.target.matches("input[type='file']")) {
        previewImage(event.target);
    }
});
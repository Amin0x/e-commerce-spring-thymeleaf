document.addEventListener("DOMContentLoaded", function() {
    
    const deleteButtons = document.querySelectorAll('button[data-delete]');
    
    function handleSubmit() {
        const formData = {
            name: $('#name').val(),
            description: $('#description').val(),
            price: $('#price').val(),
            initPrice: $('#initPrice').val(),
            category: $('#category').val(),
            sku: $('#sku').val(),
            stock: $('#stock').val(),
            totalSold: $('#totalSold').val(),
            active: $("#active").is(":checked") ? "true" : "false",
            enabled: $("#enabled").is(":checked") ? "true" : "false"
        };
        
        $.ajax({
            url: '/admin/api/products/' + productId,
            method: "PUT",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function(data) {
                console.log('Product updated successfully!');
                // Redirect or update UI as needed
            },
            error: function(response) {
                console.error('Failed to update product:', response.statusText);
            }
        });
    }
    

    $('#editProductForm').on('submit', function(event){
        event.preventDefault();
        event.stopPropagation();

        handleSubmit();
    });

    deleteButtons.forEach(handleDeleteButtonsClick);
    function handleDeleteButtonsClick(b) {
        $(b).on('click', function(e) {
            e.preventDefault();
            
            console.log('Delete image with ID:');
    
            // Perform AJAX request to delete image from server
            $.ajax({
                url: "/admin/products/images/" + productId,
                method: "DELETE",
                contentType: "application/json",
                data: JSON.stringify({
                    imageId: $(button).data('delete'),
                    id: productId
                }),
                success: function(res) {
                    console.log('Image deleted successfully.');
                    $(b).parent().remove();
                },
                error: function(err) {
                    console.error('Failed to delete image:', err.statusText);
                    console.log('Failed to delete image.');
                }
            });
        });
    }
    

    $('#fileInput').on('change', function(event){
        uploadImage(event.target.files[0]);
    });

    $('#dropZone').on('click', function(event) {
        event.preventDefault();
        $('#fileInput').click();
    });

    $('#dropZone').on('dragover', function(event) {
        event.preventDefault();
        $('#dropZone').css('border-color', 'green');
    });

    $('#dropZone').on('dragleave', function() {
        $('#dropZone').css('border-color', '#ccc');
    });

    $('#dropZone').on('drop', function(event) {
        event.preventDefault();
        $(this).css('border-color', '#ccc');

        var files = event.dataTransfer.files;
        //fileInput.files = files;
        uploadImage(files.item(0));
    });


    function uploadImage(file) {
        var formData = new FormData();
        formData.append('file', file);
    
        $.ajax({
            url: '/admin/products/images/upload?id=' + productId,
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            xhr: function() {
                var xhr = $.ajaxSettings.xhr();
                if (xhr.upload) {
                    xhr.upload.addEventListener('progress', function(event) {
                        var percent = (event.loaded / event.total) * 100;
                        //updateProgressBars(percent);
                    }, false);
                }
                return xhr;
            },
            success: function(data) {
                console.log(data);
                $('#productImages').empty();
                images = data;
                images.forEach(function(image) {
                    const $div = $('<div></div>', { class: 'image', 'data-image': image.id });
                    const $img = $('<img>', { src: '/admin/images/' + image.image, alt: image.altText });
                    const $button = $('<button></button>', { class: 'delete-btn', 'data-delete': image.id }).text('Delete');
                    $div.append($img).append($button);
                    $('#productImages').append($div);
                    handleDeleteButtonsClick($button);
                });
                //resetProgressBars('success');
            },
            error: function() {
                //resetProgressBars('failed');
            }
        });
    }
    

    $('#changePrimaryImage').on('click', function(event) {
        event.preventDefault();
        $('#filePrimaryImage').click();
    });

    $('#filePrimaryImage').on('change', function(event){
        
        const formData = new FormData();
        formData.append('file', event.target.files[0]);
        $.ajax({
            url: "/admin/products/images/upload/primary?id=" + productId,
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            success: function(response) {
                console.log('Primary image updated successfully!');
                // Update UI with new primary image
                $('#productPrimaryImage').attr('src', '/admin/images/' + response.image);
            },
            error: function(error) {
                console.log(error);
                console.error('Error updating primary image:', error);
            }
        });  
    });

});
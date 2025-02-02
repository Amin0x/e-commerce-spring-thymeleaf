document.addEventListener("DOMContentLoaded", function() {
    let productId =  '';
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('fileInput');
    const deleteButtons = document.querySelectorAll('button[data-delete]');
    const url = '/admin/api/products/' + 1 ;/*[[${product.id}]]*/
    console.log(url);

    document.getElementById('editProductForm').addEventListener('submit', function(event){
        event.preventDefault();
        event.stopPropagation();

        const formData = {
            name: document.getElementById('name').value,
            description: document.getElementById('description').value,
            price: document.getElementById('price').value,
            initPrice: document.getElementById('initPrice').value,
            category: document.getElementById('category').value,
            sku: document.getElementById('sku').value,
            stock: document.getElementById('stock').value,
            totalSold: document.getElementById('totalSold').value,
            active: document.getElementById("active").checked? "true":"false",
            enabled: document.getElementById("enabled").checked? "true":"false",
            images: [],
        }
        
        
        fetch(url, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formData),
        })
        .then(response => {
            if(response.ok) {
                response.json();
                console.log('Product updated successfully!');
                // Redirect or update UI as needed
            } else {
                Promise.reject(response.status);
                console.error('Failed to update product:', response.statusText);
                alert('Failed to update product.');
            }

        })
        .then(data => {
            alert('Product updated successfully!');
            // Redirect or update UI as needed
        })
        .catch(error => {
            console.log(error);
            console.error('Error updating product:', error);
            alert('Failed to update product.');
        });
    });

    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            var imageId = this.getAttribute('data-delete');
            console.log('Delete image with ID:', imageId);

            // Perform AJAX request to delete image from server
            fetch(url + "/delete-image", { 
                method: "post", 
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: {"imageId": imageId}
            })
            .then(res => res.json())
            .then(data => {
                console.log('Image deleted successfully.');
                console.log(data);
                button.parentElement.remove();
            }).catch(err => {
                console.log(err);
                console.log('Failed to delete image.');
            });
        });
    });

    fileInput.addEventListener('change', function(event){
        uploadImage(event.target.files[0]);
    });

    dropZone.addEventListener('click', function(event) {
        event.preventDefault();
        fileInput.click();
    });

    dropZone.addEventListener('dragover', function(event) {
        event.preventDefault();
        dropZone.style.borderColor = 'green';
    });

    dropZone.addEventListener('dragleave', function() {
        dropZone.style.borderColor = '#ccc';
    });

    dropZone.addEventListener('drop', function(event) {
        event.preventDefault();
        dropZone.style.borderColor = '#ccc';

        var files = event.dataTransfer.files;
        fileInput.files = files;
        uploadImage(files.item(0));
    });

    function uploadImage(file) {
        
        var formData = new FormData();
        formData.append('file', file);

        var xhr = new XMLHttpRequest();
        xhr.open('POST', '/admin/upload', true);

        xhr.upload.addEventListener('progress', function(event) {
            var percent = (event.loaded / event.total) * 100;
            //updateProgressBars(percent);
        });

        xhr.onload = function() {
            if (xhr.status === 200) {
                const div = document.createElement('div');
                const img = document.createElement('img');
                const button = document.createElement('button');
                div.classList.add("image");
                div.setAttribute("data-image", xhr.responseText);
                img.setAttribute("src", xhr.responseText);
                img.setAttribute("alt", xhr.responseText);
                button.setAttribute("data-delete", xhr.responseText);
                div.appendChild(img);
                div.appendChild(button);
                document.getElementById('productImages').appendChild(div);
                //resetProgressBars('success');
            } else {
                
                //resetProgressBars('failed');
            }
        };
        
        xhr.send(formData);
    }
});
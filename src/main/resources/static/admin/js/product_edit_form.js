document.addEventListener("DOMContentLoaded", function() {
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('fileInput');
    const deleteButtons = document.querySelectorAll('button[data-delete]');
    
    function handleSubmit() {
        const formData = {
            image: document.getElementById('name').value,
            description: document.getElementById('description').value,
            price: document.getElementById('price').value,
            initPrice: document.getElementById('initPrice').value,
            category: document.getElementById('category').value,
            sku: document.getElementById('sku').value,
            stock: document.getElementById('stock').value,
            totalSold: document.getElementById('totalSold').value,
            active: document.getElementById("active").checked? "true":"false",
            enabled: document.getElementById("enabled").checked? "true":"false",
        }
        
        
        fetch('/admin/api/products/' + productId, {
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
            }

        })
        .then(data => {
            console.log('Product updated successfully!');
            // Redirect or update UI as needed
        })
        .catch(error => {
            console.log(error);
            console.error('Error updating product:', error);
        });
    }

    document.getElementById('editProductForm').addEventListener('submit', function(event){
        event.preventDefault();
        event.stopPropagation();

        handleSubmit();
    });

    deleteButtons.forEach(handleDeleteButtonsClick);

    function handleDeleteButtonsClick(button) {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            
            console.log('Delete image with ID:');

            // Perform AJAX request to delete image from server
            fetch("/admin/products/images/" + productId, { 
                method: "delete",
                headers: {
                    "Content-Type": "application/json"
                }, 
                body: JSON.stringify({
                    imageId: button.getAttribute('data-delete')*1, 
                    id: productId
                })
            })
            .then(res => {
                if(!res.ok) {
                    Promise.reject(res.status);
                    console.error('Failed to delete image:', res.statusText);
                    return;
                }
               
                console.log('Image deleted successfully.');
                button.parentElement.remove();
                
            })
           .catch(err => {
                console.log(err);
                console.log('Failed to delete image.');
            });
        });
    };

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
        xhr.open('POST', '/admin/products/images/upload?id=' + productId, true);

        xhr.upload.addEventListener('progress', function(event) {
            var percent = (event.loaded / event.total) * 100;
            //updateProgressBars(percent);
        });

        xhr.onload = function() {
            if (xhr.status === 200) {
                const data = JSON.parse(xhr.responseText);
                document.getElementById('productImages').innerHTML = '';
                console.log(data);
                images = [];
                images = data;
                images.forEach(function(image) {

                    const div = document.createElement('div');
                    const img = document.createElement('img');
                    const button = document.createElement('button');
                    div.classList.add("image");
                    div.setAttribute("data-image", image.id);
                    img.setAttribute("src","/admin/images/" + image.image);
                    img.setAttribute("alt", image.altText);
                    button.setAttribute("data-delete", image.id);
                    button.classList.add("delete-btn");
                    button.textContent = "Delete";
                    div.appendChild(img);
                    div.appendChild(button);
                    document.getElementById('productImages').appendChild(div);
                    handleDeleteButtonsClick(button);
                });
                
                //resetProgressBars('success');
            } else {
                
                //resetProgressBars('failed');
            }
        };
        
        xhr.send(formData);
    }

    /*const fetchImages = () => {
        fetch(url + "/images")
       .then(response => response.json())
       .then(data => {
        data.forEach(image => {
            const div = document.createElement('div');
            const img = document.createElement('img');
            const button = document.createElement('button');
            div.classList.add("image");
            div.setAttribute("data-image", image);
            img.setAttribute("src", image);
            img.setAttribute("alt", image);
            button.setAttribute("data-delete", image);
            div.appendChild(img);
            div.appendChild(button);
            document.getElementById('productImages').appendChild(div);
        });
  
       })
       .catch(error => {
            console.log(error);
            console.error('Error fetching images:', error);
        });
    };*/
});
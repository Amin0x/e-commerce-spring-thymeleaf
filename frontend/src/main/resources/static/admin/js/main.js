document.addEventListener("DOMContentLoaded", function(event) {
});

function categoriesCreateForm() {

    const form = document.querySelector('form');
    const submitBtn = document.querySelector('#submitButton');
    const formMessage = document.getElementById('formMessage');
    const formMessageText = document.getElementById('formMessageText');

    function handleSubmitButtonClick(event) {
        event.preventDefault();
        const submitBtn = event.target;
        const form = submitBtn.closest('form');        
        const formData = new FormData(form);

        if(!validateFormInput(formData)){
            formMessage.classList.add('show', 'error');
            return false;
        } 

        submitBtn.disabled = true;
        formMessage.classList.remove('show', 'success', 'error');
        formMessageText.textContent = '';

        const url = '/admin/categories';
        fetch(url, {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                const imagePreview = document.getElementById('imagePreview');
                if(imagePreview){
                    imagePreview.src = '';
                }
                formMessage.classList.remove('error');
                formMessage.classList.add('show', 'success');
                formMessageText.innerHTML = '<p>Category created successfully.<p>';
                form.reset();
                submitBtn.disabled = false;
            } else {
                throw new Error('Failed to create category.');
            }
        })
        .catch(error => {
            formMessage.classList.add('error','show');
            formMessageText.innerHTML = `<p>${error.message}<p>`;
            submitBtn.disabled = false;
        });
    }
    
    submitBtn.addEventListener('click', function(event) {
        handleSubmitButtonClick(event);
    });

    function validateFormInput(formData) {
        let isValid = true;

        if (formData.get('name') === '') {
            formMessage.classList.add('show');
            formMessageText.innerHTML += '<p>Name is required.<p>';
            isValid = false;
        }

        if (formData.get('description') === '') {
            formMessage.classList.add('show');
            formMessageText.innerHTML += 'Description is required.';
            isValid = false;
        }

        if (formData.get('image') === null) {
            formMessage.classList.add('show');
            formMessageText.innerHTML += 'Image is required.';
            isValid = false;
        }
        return isValid;
    }
}

function previewImage(event) {
    const preview = document.getElementById('preview');
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result || '';
            preview.style.display = 'block';
        };
        reader.readAsDataURL(file);
    } else {
        preview.src = '#';
        preview.style.display = 'none';
    }
}

function categoriesEditForm() {
    const buttonSubmit = document.getElementById("buttonSubmit");
    const formEle = document.getElementById("editForm");
    const id = document.getElementById("productId").value;
    const url = '/admin/categories/' + id;
    console.log(url);

    function handleSubmit(e) {
        event.preventDefault();
        buttonSubmit.disabled = true; // Disable the button to prevent multiple submissions
        buttonSubmit.textContent = "Saving..."; // Change button text

        const formData = new FormData(formEle);
        formData.delete("image"); // Remove the image field from FormData
        formData.delete("id"); // Remove the id field from FormData
        
        if(document.getElementById("active").checked){
            formData.set("active", "true");
        } else {
            formData.set("active", "false");
        }

        fetch(url, {
            method: "PUT",
            //body: JSON.stringify(Object.keys(formData),
            body: formData,
        })
        .then(response => response.json())
        .then(data => {
            buttonSubmit.disabled = false; // Re-enable the button
            buttonSubmit.innerHTML = "Save Changes"; // Reset button text
            console.log("Product updated successfully:", data);
            // Redirect or update UI as needed
            window.location = "/admin/categories/" + id;
        })
        .catch(error => {
            console.error('Error updating product:', error);
        });
    }

    formEle.addEventListener('submit', function(event){
        handleSubmit(event);
    });
}

function categoriesUploadFile(){
    const dropArea = document.getElementById("imageDropArea");
    const fileInput = document.getElementById("image");
    const previewImage = document.getElementById("previewImage");
    const imageView = document.getElementById("imageView");
    const id = document.getElementById("productId").value;

    dropArea.addEventListener("click", () => fileInput.click());

    dropArea.addEventListener("dragover", (event) => {
        event.preventDefault();
        dropArea.style.backgroundColor = "#d9e9ff";
    });

    dropArea.addEventListener("dragleave", () => {
        dropArea.style.backgroundColor = "rgb(226, 236, 254)";
    });

    dropArea.addEventListener("drop", (event) => {
        event.preventDefault();
        dropArea.style.backgroundColor = "rgb(226, 236, 254)";
        const file = event.dataTransfer.files[0];
        handleFile(file);
    });

    fileInput.addEventListener("change", () => {
        const file = fileInput.files[0];
        handleFile(file);
    });

    function handleFile(file) {
        if (file && file.type.startsWith("image/")) {
            const reader = new FileReader();
            reader.onload = (e) => {
                previewImage.src = e.target.result;
                previewImage.style.display = "block";
                imageView.style.flexDirection = "column";
                handleFileUpload(file); // Call the upload function here
            };
            reader.readAsDataURL(file);
        } else {
            alert("Please upload a valid image file.");
        }
    }

    async function handleFileUpload(file) {
        const formData = new FormData();
        formData.append("image", file);

        try {
            const response = await fetch("/admin/categories/"+id+"/image", {
                method: "POST",
                body: formData,
            });

            if (response.ok) {
                const result = await response.json();
                console.log("Uploaded file URL:", result.fileUrl);
                
            } else {
                console.error("Upload error:", await response.text());
            }
        } catch (error) {
            console.error("Error during file upload:", error);
        }
    }

}


function categoriesHomePage() {
    document.querySelector('button').addEventListener('click', function() {
        let elm = document.getElementById('createCategoryModel');
        if (elm.style.display === 'none') {
            elm.style.display = 'block';
        } else {
            elm.style.display = 'none';
        }
    });

    document.getElementById('CancelButton').addEventListener('click', function() {
        document.getElementById('createCategoryModel').style.display = 'none';
    });

    document.querySelectorAll('.delete-button').forEach(function(elm) {
        elm.addEventListener('click', function(e) {
            e.preventDefault();
            const id = elm.getAttribute('data-delete');
            const fd = new FormData();
            fd.append('id', id);
            fetch('/admin/categories/'+id, {
                method: 'delete',
                body: fd,
            })
            .then(response => {
                if(response.status === 200){
                    return response.json();
                }

                throw new Error("cant delete category");
            })
            .then(data => {
                console.log(data);
                elm.closest('tr').remove();
            })
            .catch(error => {
                console.log(error);
            })
        });
    });

    document.getElementById('createCategoryForm').addEventListener('submit', function(e) {
        e.preventDefault();
        let fd = new FormData(this);
        fetch(this.action, {
            method: 'POST',
            body: fd
        }).then(function(response) {
            return response.json();
        }).then(function(data) {
            console.log(data);
        }).catch(function(err) {
            console.error(err);
        });
    });

    document.querySelectorAll('[data-menu]').forEach(function(elm) {
        elm.addEventListener('click', function(e) {
            e.preventDefault();
            const id = e.target.getAttribute('data-menu');
            console.log('Menu button clicked, id = ' + id);
        });
    });
}

function cityCreatePage(){
    const state = document.getElementById('state');
    const country = document.getElementById('country');
    state.innerHTML = '';
    state.disabled = true;

    country.addEventListener('change', (e)=>{
        if(country.value == null || country.value == '') return false;

        const url = '/admin/address/state?cid=' + country.value;
        
        fetch(url,{
            method: 'get',
        })
        .then(response => {
            if(!response.ok) throw new Error('');
            response.json();
        })
        .then(data =>{
            if(data !== undefined && Array.isArray(data)){
                Array.from(data).forEach(element => {
                    const option = document.createElement('option');
                    option.value = element.id;
                    option.textContent = element.name;
                    state.appendChild(option);
                });
                state.disabled = false;
            }
        })
        .catch(error => {
            console.log(error);
        })
    });


            document.getElementById('cityForm').addEventListener('submit', function(e) {
                e.preventDefault();

                // Clear previous error messages
                clearErrorsMessages();
                

                let isValid = validateInputs();

                // If any validation fails, return early
                if (!isValid) {
                    return false;
                }

                console.log(document.getElementById('cityid').value)
                const formData = {
                    id: document.getElementById('cityid').value,
                    name: document.getElementById('name').value,
                    nameAr: document.getElementById('nameAr').value,
                    estimatedDelivery: document.getElementById('estimatedDelivery').value,
                    deliveryPrice: document.getElementById('deliveryPrice').value,
                    deliveryPriority: document.getElementById('deliveryPriority').value,
                    stateId: document.getElementById('state').value,
                    countryId: document.getElementById('country').value,
                };

                document.querySelector('button[type=submit]').disabled = true;
                let url = '/admin/address/city';
                if(document.getElementById('cityid').value !== null){
                    url = '/admin/address/city/update';
                }

                fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error adding city!');
                    }
                    return response.json();
                })
                .then(data => {                    
                    const popover = document.createElement('div');
                    popover.className = 'popover';
                    popover.textContent = 'City added successfully!';
                    document.body.appendChild(popover);
                    setTimeout(() => {
                        popover.style.transition = 'opacity 0.5s';
                        popover.style.opacity = '0';
                        setTimeout(() => popover.remove(), 500);
                    }, 3000);
                 
                    document.getElementById('cityid').value = data.id;
                    document.querySelector('button[type=submit]').disabled = false;
                })
                .catch(error => {
                    console.log(error.message);
                    document.querySelector('button[type=submit]').disabled = false;
                });
            });

    function validateInputs() {
        let isValid = true;

        // Validate city name
        const cityName = document.getElementById('name').value;
        if (cityName === undefined || cityName.trim().length === 0) {
            document.getElementById('nameError').textContent = 'City name is required.';
            isValid = false;
        }
        else if (cityName.length < 3) {
            document.getElementById('nameError').textContent = 'City name must be at least 3 characters long.';
            isValid = false;
        }
        // Validate city name in Arabic
        const cityNameAr = document.getElementById('nameAr').value;
        if (cityNameAr === undefined || cityNameAr.trim().length === 0) {
            document.getElementById('nameArError').textContent = 'City name (Arabic) is required.';
            isValid = false;
        }
        else if (cityNameAr.trim().length < 3) {
            document.getElementById('nameArError').textContent = 'City name (Arabic) must be at least 3 characters long.';
            isValid = false;
        }
        // Validate estimated delivery
        const estimatedDelivery = document.getElementById('estimatedDelivery').value;
        if (estimatedDelivery === undefined || estimatedDelivery.length === 0) {
            document.getElementById('estimatedDeliveryError').textContent = 'Estimated delivery is required.';
            isValid = false;
        }
        if (!/^\d+$/.test(estimatedDelivery)) {
            document.getElementById('estimatedDeliveryError').textContent = 'Estimated delivery must be a number.';
            isValid = false;
        }
        // Validate delivery price
        const deliveryPrice = document.getElementById('deliveryPrice').value;
        if (deliveryPrice === undefined || deliveryPrice.length === 0) {
            document.getElementById('deliveryPriceError').textContent = 'Delivery price is required.';
            isValid = false;
        }
        if (!/^\d+(?:\.\d{1,2})?$/.test(deliveryPrice)) {
            document.getElementById('deliveryPriceError').textContent = 'Delivery price must be a valid number.';
            isValid = false;
        }
        // Validate delivery priority
        const deliveryPriority = document.getElementById('deliveryPriority').value;
        if (deliveryPriority === undefined || deliveryPriority.length === 0) {
            document.getElementById('deliveryPriorityError').textContent = 'Delivery priority is required.';
            isValid = false;
        }

        // Validate country selection
        const countryId = document.getElementById('country').value;
        if (countryId === undefined || countryId.length === 0) {
            document.getElementById('countryError').textContent = 'Select a country.';
            isValid = false;
        }
        // Validate state selection
        const stateId = document.getElementById('state').value;
        if (stateId === undefined || stateId.length === 0) {
            //document.getElementById('stateError').textContent = 'Select a state.';
            //isValid = false;
        }
        return isValid;
    }

    function clearErrorsMessages() {
        document.getElementById('nameError').textContent = '';
        document.getElementById('nameArError').textContent = '';
        document.getElementById('estimatedDeliveryError').textContent = '';
        document.getElementById('deliveryPriceError').textContent = '';
        document.getElementById('deliveryPriorityError').textContent = '';
        document.getElementById('stateError').textContent = '';
    }
}


function userCreateForm(){
    let image = null;

    function init() {
        document.getElementById('avatar').addEventListener('change', async function() {
            handleAvatarChange(this.files[0]);
        });

        document.getElementById('avatarButton').addEventListener('click', function() {
            document.getElementById('avatar').click();
        });
        
        document.getElementById('submitButton').addEventListener('click', function(event) {
            event.preventDefault();
            // if(formValidation() == false) {
            //     return;
            // }
            handleFormSubmit();            
        });
    }

    function formValidation(){
        const name = document.getElementById('firstName').value;
        const lastname = document.getElementById('lastName').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        //const confirmPassword = document.getElementById('confirmPassword').value;
        //const date = document.getElementById('date').value;

        if(name === ''){
            document.getElementById('firstName').classList.add('is-invalid');
            document.getElementById('firstName').closest('div div').classList.add('is-invalid');
            return false;
        }

        if(lastname === ''){
            document.getElementById('lastName').classList.add('is-invalid');
            document.getElementById('lastName').closest('div div').classList.add('is-invalid');
            return false;
        }

        if(email === ''){
            document.getElementById('email').classList.add('is-invalid');
            document.getElementById('email').closest('div div').classList.add('is-invalid');
            return false;
        }

        if(password === '' || password.length < 8){
            document.getElementById('password').classList.add('is-invalid');
            return false;
        }

    

        if(!validateEmail(email)){
            alert('Please enter a valid email address');
            return false;
        }

        return true;
    }

    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

   
    
    function handleFormSubmit() {
        const form = document.getElementById('createUserForm');
        const submit = document.getElementById('submitButton');

        submit.disabled = true;

        const day = document.getElementById('birthdayDay').value;
        const month = document.getElementById('birthdayMonth').value;
        const year = document.getElementById('birthdayYear').value;

        if (!day || !month || !year) {
            alert('Please select a valid date.');
            return;
        }

        const date = new Date(year, month - 1, day);

        const data = {
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            role: document.getElementById('role').value,
            enabled: document.getElementById('enabled').value,
            birthDate: new Intl.DateTimeFormat("en-US", { dateStyle: "short", }).format(date),
            avatar: document.getElementById('avatar').files[0],
        };

        const formData = new FormData();
        Object.keys(data).forEach(key => {
            if(data[key] !== undefined) {
                formData.append(key, data[key]);
            }
        });

        fetch('/admin/users', {
            method: 'POST',
            body: formData,
        })
        .then(response => {
            if (response.ok || response.status != 500) {
                return response.json();
            }
            
            throw new Error('');
        })
        .then(data => {
            console.log('Success:', data);
            submit.disabled = false;
            if (data.status == 'succes') {
                document.getElementById('createUserForm').reset();
            }
                        
        })
        .catch((error) => {
            console.error('Error:', error);
            submit.disabled = false;
        });
    }

    function handleAvatarChange(file) {                
        if (!file) {
            return;
        }

        const fileReader = new FileReader();
        fileReader.onload = function(event) {
            image = event.target.result;
            const avatarButton = document.getElementById('avatarButton');
            if (avatarButton && image) {
                avatarButton.textContent = '';
                avatarButton.style.display = 'block';
                avatarButton.style.backgroundImage = `url('${image}')`;
                avatarButton.style.backgroundSize = '100px';//'cover';
                avatarButton.style.backgroundPosition = 'center';
                avatarButton.style.backgroundRepeat = 'no-repeat';
                avatarButton.style.border = '2px solid #000';                
            }
        };

        fileReader.readAsDataURL(file);
    }

    return {init, formValidation, handleFormSubmit, handleAvatarChange};
}

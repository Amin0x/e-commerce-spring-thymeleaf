
function handleSignupFormSubmit(e) {

    e.preventDefault();

    var email = document.getElementById('email');
    var password = document.getElementById('password');
    var confirmPassword = document.getElementById('confirmPassword');

    if (password.value.length < 8 || password.value === '') {
        password.classList.add('error');
        return false;
    }

    if (password.value !== confirmPassword.value || confirmPassword.value == '') {
        confirmPassword.classList.add('error');
        return false;
    }

    if (email.value == '') {
        email.classList.add('error');
        return false;
    }

    fetch("/signup", {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email.value, password: email.value, confirmPassword: confirmPassword.value }),
    }).then(response => {
        if (response.status == 200) {
            return response.json()
        }

        return Promise.reject(response.statusText)
    })
        .then(data => {
            console.log(data);
            if (data.status === "success") {
                window.location.href = "http://localhost/";
            }
        })
}

function handleLoginSubmit(e) {
    e.preventDefault();
    const formData = new FormData(e.target);

    fetch("/login", {
        method: "POST",
        body: formData,
    })
        .then(res => {
            return (res.status == 200 ? re.json() : Promise.reject(res.statusText))
        })
        .then(data => {
            if (data.status === "success") { return data }
        })
        .catch(
            error => console.error("Error:", error)
        );
}

function handleAddToCartClick(e) {
    e.preventDefault();
    const el = e.target;
    addToCart(el.getAttribute('data-add-cart'));
}

function addToCart(productId) {
    const fd = new FormData();
    fd.append('productId', productId);
    fd.append('quantity', 1);

    fetch('/carts/addToCart', {
        method: 'POST',
        body: fd,
    }).then(response => response.json()).then(data => {
        if (data !== undefined) {
            console.log('added to cart successfully : ' + productId);
            document.querySelector('#cartItemsCount').textContent(data.totalItems);
        } else {
            console.log('failed to add to cart : ' + productId);
        }
    }).catch(error => {
        console.error('Error:', error);
    });
}

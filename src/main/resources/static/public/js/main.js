
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

    const data = { 
        email: email.value, 
        password: email.value, 
        confirmPassword: confirmPassword.value 
    };
    const url = '/signup';
    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
    }).then(response => {
        if (response.status == 200) {
            return response.json()
        }

        return Promise.reject(response.statusText)
    })
    .then(data => {
        console.log(data);
        if (data.status === "success") {
            window.location.href = data.url;
        }
    })
}

function handleLoginSubmit(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const url = '/login';

    fetch(url, {
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

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-add-cart]').forEach((element) => {
        element.addEventListener('click', (event) => {
            event.preventDefault();
            const productId = element.getAttribute('data-add-cart');
            const quantity = 1;
            addToCart(productId, quantity);
        });
    });
});

function addToCart(productId, quantity) {
    const url = '/carts/addToCart';
    const data = {
        productId: productId,
        quantity: quantity,
    };

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    })
    .then((response) => response.json())
    .then((data) => {
        console.log('Cart update response:', data);
        if(data.status == 200) {
            const cartItemsCount = document.getElementById('cartItemsCount');
            if (cartItemsCount && data.totalItems) {
                cartItemsCount.textContent = data.totalItems;
            }
        }
    })
    .catch((error) => console.error('Error:', error));
}

function initializeSubscription() {
    document.addEventListener("DOMContentLoaded", function () {
        document.getElementById("subscribeButton").addEventListener("click", function (event) {
            event.preventDefault(); // Prevent the default form submission
            subscribe(); // Call the subscribe function
        });
    });

    function subscribe() {
        var email = document.getElementById("email").value;
        var name = document.getElementById("name").value;
        var url = "/subscription/subscribe";
        let formData = new FormData();
        formData.append("email", email);
        formData.append("name", name);
        const message = document.getElementById("message");
        message.innerHTML = ""; // Clear previous messages
        // Validate inputs
        if (!validateInputs()) {
            return; // Stop if validation fails
        }
        fetch(url, {
            method: "POST",
            body: formData,
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 200) {
                console.log("Subscription successful!");
                message.innerHTML = "Subscription successful! Check your email for confirmation.";
            } else {
                console.log("Subscription failed. Please try again.");
                message.innerHTML = data.message || "Subscription failed. Please try again.";
            }
        })
        .catch(error => {
            console.error("Error:", error);
        });
    }

    function validateInputs() {
        const email = document.getElementById("email").value;
        const name = document.getElementById("name").value;
        const message = document.getElementById("message");
        message.innerHTML = ""; // Clear previous messages

        if (!email || !name) {
            message.innerHTML = "Please fill in all fields.";
            return false;
        }

        if (!validateEmail(email)) {
            message.innerHTML = "Please enter a valid email address.";
            return false;
        }

        return true;
    }
    // Validate email format
    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(String(email).toLowerCase());
    }
}

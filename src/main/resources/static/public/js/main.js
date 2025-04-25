//navbar
document.addEventListener('DOMContentLoaded', async function(){
    const res = await fetch('/carts/getCart', {
        method: 'GET',
    });

    if(res != undefined && res.status == 200){
        const data = await res.json();
        const cartItemsCount = document.getElementById("cartItemsCount");
        if (cartItemsCount) {
            cartItemsCount.textContent = data.totalItems;
        }
    }

    window.addEventListener('click', function(event) {
        if (!event.target.matches('.dropdown')) {
            const dropdowns = document.getElementById("dropdownMenu");
            if (dropdowns && dropdowns.classList.contains('active')) {
                dropdowns.classList.remove('active');
            }
        }
    });

    const dropdownCartButton = document.getElementById("dropdownCartButton");
    if (dropdownCartButton) {
        dropdownCartButton.addEventListener("click", function() {
            const dropdownCart = document.getElementById("dropdownCart");
            if(dropdownCart){
                dropdownCart.classList.toggle("active");
            }
        });
    }

    const dropdownMenuButton = document.getElementById("dropdownMenuButton");
    if (dropdownMenuButton) {
        
        dropdownMenuButton.addEventListener("click", function() {
            const dropdownMenu = document.getElementById("dropdownMenu");
            if(dropdownMenu){
                dropdownMenu.classList.toggle("active");
            }
        });
    }

    const categoryMenu = document.getElementById("categoryMenu");
    if (categoryMenu) {
        categoryMenu.closest('li').addEventListener('click', function(e){
            const categoryMenu = document.getElementById("categoryMenu");
            if(categoryMenu){
                categoryMenu.classList.toggle('active');
            }
        })
    }

});


const SignupForm = (function(){

    function init(){
        document.addEventListener('DOMContentLoaded', async function(){
            const signupForm = document.getElementById("signupForm");
            if(signupForm){
                signupForm.addEventListener('submit', function (e) {
                    handleSignupFormSubmit(e);
                });
            }
        });
    }

    function handleSignupFormSubmit(e) {
        e.preventDefault();
    
        const email = document.getElementById('email');
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');
    
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
            username: email.value, 
            password: email.value, 
            confirmPassword: confirmPassword.value 
        };

        const url = '/auth/register';
        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");

        fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', [header]: token },
            body: JSON.stringify(data),
        }).then(response => {
            if (response.status === 200) {
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

    return {
        handleSignupFormSubmit,
        init,
    }
})();

const LoginForm = (function(){
    function handleLoginSubmit(e) {
        e.preventDefault();
        const formData = new FormData(e.target);
        const url = '/auth/login';
    
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

    return {
        handleLoginSubmit,
    }
})();



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
    const formData = new FormData();
    formData.append("productId", productId);
    formData.append("quantity", quantity);

    fetch(url, {
        method: 'POST',
        body: formData,
    })
    .then((response) => response.json())
    .then((data) => {
        console.log('Cart update response:', data);
        if(data.status === "OK") {
            const cartItemsCount = document.getElementById('cartItemsCount');
            if (cartItemsCount && data.totalItems) {
                cartItemsCount.textContent = data.totalItems;
            }
        }
    })
    .catch((error) => console.error('Error:', error));
}

const SubscriptionForm = (function() {

    function init(){
        document.addEventListener("DOMContentLoaded", function () {
            document.getElementById("subscribeButton").addEventListener("click", function (event) {
                event.preventDefault(); // Prevent the default form submission
                subscribe(); // Call the subscribe function
            });
        });
    }

    function subscribe() {
        const email = document.getElementById("email").value;
        const name = document.getElementById("name").value;
        const url = "/subscription/subscribe" + "?email=" + encodeURIComponent(email) + "&name=" + encodeURIComponent(name);

        const message = document.getElementById("message");
        message.innerHTML = ""; // Clear previous messages
        // Validate inputs
        if (!validateInputs()) {
            return; // Stop if validation fails
        }
        fetch(url, {
            method: "GET",
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

    return{
        init,
    }
})();

const cartItems = (function(){
    let cart = null;

    const cartRender = (productImage, productName, productPrice, productId, quantity, total) => {
        let image = undefined
        if(productImage == null){
            image = '<svg width="80px" height="80px" viewBox="0 0 48 48" id="a" xmlns="http://www.w3.org/2000/svg"><defs><style>.b{fill:none;stroke:#000000;stroke-linecap:round;stroke-linejoin:round;}</style></defs><path class="b" d="M29.4995,12.3739c.7719-.0965,1.5437,.4824,1.5437,1.2543h0l2.5085,23.8312c.0965,.7719-.4824,1.5437-1.2543,1.5437l-23.7347,2.5085c-.7719,.0965-1.5437-.4824-1.5437-1.2543h0l-2.5085-23.7347c-.0965-.7719,.4824-1.5437,1.2543-1.5437l23.7347-2.605Z"/><path class="b" d="M12.9045,18.9347c-1.7367,.193-3.0874,1.7367-2.8945,3.5699,.193,1.7367,1.7367,3.0874,3.5699,2.8945,1.7367-.193,3.0874-1.7367,2.8945-3.5699s-1.8332-3.0874-3.5699-2.8945h0Zm8.7799,5.596l-4.6312,5.6925c-.193,.193-.4824,.2894-.6754,.0965h0l-1.0613-.8683c-.193-.193-.5789-.0965-.6754,.0965l-5.0171,6.1749c-.193,.193-.193,.5789,.0965,.6754-.0965,.0965,.0965,.0965,.193,.0965l19.9719-2.1226c.2894,0,.4824-.2894,.4824-.5789,0-.0965-.0965-.193-.0965-.2894l-7.8151-9.0694c-.2894-.0965-.5789-.0965-.7719,.0965h0Z"/><path class="b" d="M16.2814,13.8211l.6754-6.0784c.0965-.7719,.7719-1.3508,1.5437-1.2543l23.7347,2.5085c.7719,.0965,1.3508,.7719,1.2543,1.5437h0l-2.5085,23.7347c0,.6754-.7719,1.2543-1.5437,1.2543l-6.1749-.6754"/><path class="b" d="M32.7799,29.9337l5.3065,.5789c.2894,0,.4824-.193,.5789-.4824,0-.0965,0-.193-.0965-.2894l-5.789-10.5166c-.0965-.193-.4824-.2894-.6754-.193h0l-.3859,.3859"/></svg>';
        } else{
            image = `<img width="80px"  src="/uploads/images/${productImage}" alt="${productName}"/>`;
        }
    
        // Create a DocumentFragment
        const fragment = document.createDocumentFragment();
    
        // Create a template element to hold the HTML structure
        const template = document.createElement("template");
        template.innerHTML = `<tr class="bottom-boarder">
            <td>${image}</td>
            <td>
                <div class="product-name">${productName}</div>                                
                <strong class="item-price" data-item-price="${productId}">${productPrice} SDG</strong>
            </td>
            <td>
                <div class="item-quantity">
                    <button type="button" data-item-inc="${productId}" onclick="cartItemIncerment(${productId})">+</button>
                    <span data-item-quantity="${productId}">${quantity}</span>
                    <button type="button" data-item-dec="${productId}" onclick="cartItemDecerment(${productId})">-</button>
                </div>
            </td>
            <td>
                <div class="item-price" data-item-total="${productId}">${total} SDG</div>
            </td>
            <td>
                <button type="button" class="button-delete" data-item-del="${productId}" onclick="buttonDeleteItemClicked(${productId})">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24px" height="24px">
                        <path d="M12,2C6.47,2,2,6.47,2,12c0,5.53,4.47,10,10,10s10-4.47,10-10C22,6.47,17.53,2,12,2z M16.707,15.293 
                            c0.391,0.391,0.391,1.023,0,1.414C16.512,16.902,16.256,17,16,17s-0.512-0.098-0.707-0.293L12,13.414l-3.293,3.293 
                            C8.512,16.902,8.256,17,8,17s-0.512-0.098-0.707-0.293c-0.391-0.391-0.391-1.023,0-1.414L10.586,12L7.293,8.707 
                            c-0.391-0.391-0.391-1.023,0-1.414s1.023-0.391,1.414,0L12,10.586l3.293-3.293c0.391-0.391,1.023-0.391,1.414,0 
                            s0.391,1.023,0,1.414L13.414,12L16.707,15.293z"/>
                    </svg>
                </button>
            </td>
        </tr>`;
    
        // Append the template's content to the fragment
        fragment.appendChild(template.content);
    
        // The fragment is now ready to use
        console.log(fragment);
        return fragment;
    }

    const updateCart = (id)=>{    
        const item = Array.from(cart.cartItems).find(it => it.id === id);
        
        console.log(cart.cartItems);
        console.log("item is : "+item);
        if(item != undefined){
            $('[data-item-quantity='+id+']').text(item.quantity);
            $('[data-item-total='+id+']').text(item.total + ' SDG');
            $('[data-item-price='+id+']').text(item.price + ' SDG');
        }       
        
    }

    const updateTotal = (shipping, tax) => {
        if(shipping == undefined) shipping = 0;
        if(cart.total == undefined) cart.total = 0;
        if(tax == undefined) tax = 0;
    
        const cartTotal = cart.total + shipping + tax;
    
        document.querySelector('#subtotal').textContent = cart.total  + ' جنيه';
        document.querySelector('#shipping').textContent =  shipping + ' جنيه';
        document.querySelector('#tax').textContent = tax + ' جنيه';
        document.querySelector('#total').textContent = cartTotal + ' جنيه';
    }

    const updateCartTotalitemCount = (c)=>{
        document.getElementById("cartItemsCount").textContent = c;
    }

    const getCart = () => {
        $.ajax({
            url: '/carts/getCart',
            method: 'get',
            success: (data, status, xhr)=>{
                console.log(data);
                if(data != undefined || data != null){
                    cart = data.cart;
                    if(cart != null && cart.cartItems.length > 0){
                        cart.cartItems.forEach(cart => {
                            $('tbody').append(cartRender(cart.product.image, cart.product.name, cart.price, cart.id, cart.quantity, cart.total))
                        })            
                    }
                    updateTotal(data.shipping, data.tax);
                    updateCartTotalitemCount(data.totalItems);
                }
                    
            },
            error: (xhr, status, error)=>{
                console.log("error loading cart", status, error)
            },
        })
    }

    const buttonDeleteItemClicked = (id)=>{
        $.ajax({
            url: '/carts/delete',
            method: 'post',
            data: {id: id},
            success: (data, status, xhr)=>{
                console.log(data);
                if(data != undefined || data != null){
                    cart = data.cart;
                    $('[data-item-del='+id+']').closest('tr').remove();
                    updateTotal(data.shipping, data.tax);
                    updateCartTotalitemCount(data.totalItems);
                }
            },
            failed: (xhr, status, error)=>{
                console.log(error);
            }
        });
    }

    const cartItemIncerment = (id) => {
        $.ajax({
            url: '/carts/increment',
            method: 'post',
            data: {id: id},
            success: (data, status, xhr)=>{
                console.log(data);
                if(data != undefined || data != null){
                    cart = data.cart;
                    updateCart(id);
                    updateTotal(data.shipping, data.tax);
                    updateCartTotalitemCount(data.totalItems);
                }
            },
            failed: (xhr, status, error)=>{
                console.log(error);
    
            }
        });
    }

    function cartItemDecerment(id){
        $.ajax({
            url: '/carts/decrement',
            method: 'post',
            data: {id: id},
            success: (data, status, xhr)=>{
                console.log(data);
                if(data != undefined || data != null){
                    cart = data.cart;
                    updateCart(id);
                    updateTotal(data.shipping, data.tax);
                    updateCartTotalitemCount(data.totalItems);
                }
            },
            failed: (xhr, status, error)=>{
                console.log(error);
            }
        });
    }

    return {
        cartItemDecerment,
        cartItemIncerment,
        buttonDeleteItemClicked,
        getCart,
        updateTotal,
        updateCartTotalitemCount,
        updateCart,
        cartRender,
    }
})();

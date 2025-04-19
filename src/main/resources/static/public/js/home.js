document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-add-cart]').forEach((element) => {
        element.addEventListener('click', (event) => {
            event.preventDefault();
            const productId = element.getAttribute('data-add-cart');
            const quantity = 1;
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
                console.log(data);
                document.getElementById('cartItemsCount').textContent = data.totalItems;
            })
            .catch((error) => console.error('Error:', error));
        });
    });
});

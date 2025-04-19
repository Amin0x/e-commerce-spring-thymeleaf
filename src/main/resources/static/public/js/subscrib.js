document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("subscribeButton").addEventListener("click", function (event) {
        event.preventDefault(); // Prevent the default form submission
        subscribe(); // Call the subscribe function
    });
});
function subscribe() {
    var email = document.getElementById("email").value;
    var name = document.getElementById("name").value;
    var url = "/subscribe";
    let formData = new FormData();
    formData.append("email", email);
    formData.append("name", name);
    fetch(url, {
        method: "POST",
        body: formData,
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === "success") {
            console.log("Subscription successful!");
            const message = document.getElementById("message");
            message.innerHTML = "Subscription successful! Check your email for confirmation.";
        } else {
            console.log("Subscription failed. Please try again.");
            const message = document.getElementById("message");
            message.innerHTML = data.message || "Subscription failed. Please try again.";
        }
    })
    .catch(error => {
        console.error("Error:", error);
    });
}
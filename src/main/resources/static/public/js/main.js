const signupForm = () => {
  document.getElementById('signupForm').addEventListener('submit', function(event) {
    var password = document.getElementById('password').value;
    var confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
      alert('Passwords do not match.');
      event.preventDefault(); // Prevent form submission
    }

  });
};

const signinForm = () => {
  document.getElementById('signupForm').addEventListener('submit', function(event) {

  });
};

const addToCart = () => {
  document.getElementById('signupForm').addEventListener('submit', function(event) {

  });
};

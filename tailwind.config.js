/** @type {import('tailwindcss').Config} */
module.exports = {
  // Scan every Thymeleaf template and the project's own JavaScript for Tailwind
  // class candidates. Vendored Bootstrap assets are intentionally excluded so
  // their class names are not pulled into the generated stylesheet.
  content: [
    './src/main/resources/templates/**/*.html',
    './src/main/resources/static/public/js/main.js',
    './src/main/resources/static/admin/js/main.js',
    './src/main/resources/static/admin/js/product_edit_form.js',
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}

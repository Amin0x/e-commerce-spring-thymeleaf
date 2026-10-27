# Tailwind CSS Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Convert all 75 Thymeleaf templates from Bootstrap to Tailwind CSS, remove Bootstrap entirely, and install Tailwind via an npm + PostCSS build pipeline.

**Architecture:** Component-driven rebuild. Shared UI (navbar, footer, product cards, tables, forms, dropdowns, cart widget) extracted into Thymeleaf fragments converted once and reused everywhere. Bootstrap CSS/JS removed from all templates; dropdown/carousel/collapse interactions reimplemented in vanilla JS. Tailwind built once into `src/main/resources/static/css/tailwind.css` and referenced via `th:href="@{/public/css/tailwind.css}"`.

**Tech Stack:** Tailwind CSS v3.4, PostCSS via `postcss-cli` + `autoprefixer`, `frontend-maven-plugin` (installs node/npm locally during `mvn package`), jQuery retained (main.js uses it for CSRF/cart AJAX), `bootstrap.bundle.min.js` removed.

**Spec:** `docs/superpowers/specs/2026-10-27-tailwind-migration-design.md`

## Global Constraints

- Every template in `frontend/src/main/resources/templates/` is in scope: public/ + admin/ + error/ + all fragments/.
- Bootstrap CSS links removed from every template; zero `bootstrap` string remains in templates or `pom.xml`.
- `bootstrap.bundle.min.js` script tags removed; jQuery retained (main.js / cartRender use `$()`).
- Tailwind output at `frontend/src/main/resources/static/css/tailwind.css`; referenced as `/public/css/tailwind.css`.
- Custom utilities written as Tailwind arbitrary values or `@apply` in `source.css`.
- `main.js` dropdown toggle logic retained; class names updated to match Tailwind markup.
- `maven -q -Djava.version=17 clean package -DskipTests` must build both modules after each task commit.

## Bootstrap → Tailwind class reference (used throughout)

| Bootstrap | Tailwind |
|---|---|
| `container` | `max-w-7xl mx-auto px-4 sm:px-6 lg:px-8` |
| `row` | `flex flex-wrap` |
| `col-md-3` / `col-md-6` | `w-full md:w-1/4` / `w-full md:w-1/2` |
| `btn btn-primary` | `px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700` |
| `btn btn-danger` | `px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700` |
| `btn btn-sm` | `px-2 py-1 text-sm rounded` |
| `btn btn-outline-primary` | `px-4 py-2 border border-blue-600 text-blue-600 rounded hover:bg-blue-600 hover:text-white` |
| `form-control` | `w-full border border-gray-300 rounded px-3 py-2 focus:ring-blue-500 focus:border-blue-500` |
| `form-group` | `mb-4` |
| `card` | `bg-white rounded-lg shadow border border-gray-200` |
| `card-body` | `p-4` |
| `card-img-top` | `w-full h-48 object-cover rounded-t-lg` |
| `table` / `table-striped` | `min-w-full divide-y divide-gray-200` |
| `thead th` | `bg-gray-50 px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase` |
| `tbody td` | `px-4 py-2` |
| `navbar` | `flex items-center justify-between bg-white border-b border-gray-200 px-4 py-2` |
| `nav-link` | `text-gray-600 hover:text-blue-600` |
| `dropdown-menu` | `absolute bg-white border border-gray-200 shadow-lg rounded mt-2 w-48 hidden` |
| `dropdown-menu.show` | `block` (via `.active` JS toggle) |
| `dropdown-item` | `block px-4 py-2 text-gray-600 hover:bg-gray-100` |
| `text-danger` | `text-red-600` |
| `text-muted` | `text-gray-500` |
| `text-center` | `text-center` |
| `text-right` | `text-right` |
| `d-flex` / `flex-row` | `flex` |
| `flex-column` | `flex flex-col` |
| `justify-content-between` | `justify-between` |
| `align-items-center` | `items-center` |
| `mt-3` / `mt-5` | `mt-3` / `mt-5` |
| `mb-3` / `mb-4` / `mb-5` | `mb-3` / `mb-4` / `mb-5` |
| `p-2` / `p-3` / `p-4` / `p-5` | `p-2` / `p-3` / `p-4` / `p-5` |
| `d-none` | `hidden` |
| `d-block` | `block` |

---

### Task 1: Scaffold Tailwind build pipeline

**Files:**
- Create: `frontend/tailwind.config.js`
- Create: `frontend/postcss.config.js`
- Create: `frontend/src/main/resources/static/css/source.css`
- Modify: `frontend/pom.xml`

**Interfaces:**
- Consumes: maven `pom.xml` dependency-management chain.
- Produces: `frontend-maven-plugin` config, Tailwind/PostCSS configs, source CSS input, built `tailwind.css` referenced by templates.

**Custom component layer** (`source.css`) — replicated Bootstrap component look via `@apply`:

```css
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer components {
  .btn-primary { @apply px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition-colors; }
  .btn-danger  { @apply px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition-colors; }
  .btn-sm      { @apply px-2 py-1 text-sm rounded; }
  .btn-outline-primary { @apply px-4 py-2 border border-blue-600 text-blue-600 rounded hover:bg-blue-600 hover:text-white transition-colors; }
  .form-control { @apply w-full border border-gray-300 rounded px-3 py-2 focus:ring-1 focus:ring-blue-500 focus:border-blue-500; }
  .form-group   { @apply mb-4; }
  .card         { @apply bg-white rounded-lg shadow border border-gray-200 overflow-hidden; }
  .table        { @apply min-w-full divide-y divide-gray-200; }
  .dropdown-menu { @apply absolute z-50 mt-2 w-48 bg-white border border-gray-200 rounded shadow-lg; hidden; }
  .dropdown-item { @apply block w-full px-4 py-2 text-gray-600 hover:bg-gray-100 hover:text-gray-900; }
}
```

**`tailwind.config.js`**:

```js
/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/main/resources/templates/**/*.html',
    './src/main/resources/static/**/*.js',
  ],
  theme: { extend: {} },
  plugins: [],
}
```

**`postcss.config.js`**:

```js
module.exports = {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
  },
}
```

**`frontend/pom.xml`** — add `frontend-maven-plugin` after `spring-boot-maven-plugin`:

```xml
<plugin>
    <groupId>com.github.eirslett</groupId>
    <artifactId>frontend-maven-plugin</artifactId>
    <version>1.15.0</version>
    <configuration>
        <nodeVersion>v20.11.0</nodeVersion>
        <npmVersion>10.2.4</npmVersion>
        <workingDirectory>.</workingDirectory>
    </configuration>
    <executions>
        <execution>
            <id>install node and npm</id>
            <goals><goal>install-node-and-npm</goal></goals>
        </execution>
        <execution>
            <id>npm install</id>
            <goals><goal>npm-install</goal></goals>
        </execution>
        <execution>
            <id>tailwind build</id>
            <goals><goal>npm</goal></goals>
            <phase>generate-resources</phase>
            <configuration>
                <arguments>run build:css</arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Also add `package.json` scripts: `"build:css": "postcss src/main/resources/static/css/source.css -o src/main/resources/static/css/tailwind.css"`.

- [ ] **Step 1:** Create `tailwind.config.js`, `postcss.config.js`, `source.css`, `package.json`, and add `frontend-maven-plugin` to `frontend/pom.xml`.
- [ ] **Step 2:** Run `mvn -q -Djava.version=17 clean package -DskipTests` in `frontend` to verify node installs, npm runs, and `tailwind.css` emits.
- [ ] **Step 3:** Commit `frontend/pom.xml`, `tailwind.config.js`, `postcss.config.js`, `source.css`, `package.json`, `src/main/resources/static/css/tailwind.css`.

---

### Task 2: Convert base layout + navbar + footer fragments (foundational)

**Files:**
- Modify: `frontend/src/main/resources/templates/public/layout.html`
- Modify: `frontend/src/main/resources/templates/public/fragments/navbar.html`
- Modify: `frontend/src/main/resources/templates/public/fragments/footer.html`

**Interfaces:**
- Consumes: `tailwind.css` built in Task 1.
- Produces: base shell and shared nav/footer used by every public page; later tasks consume these fragments unchanged.

**`layout.html`** — swap Bootstrap CDN for Tailwind:

```html
<link th:href="@{/public/css/tailwind.css}" rel="stylesheet"/>
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
```

Remove any `bootstrap.min.css`/`bootstrap.bundle.min.css` links.

**`navbar.html`** — convert Bootstrap to Tailwind (drop `data-toggle="dropdown"`, use `active` class toggle via `main.js`):

- `.navbar` → `flex items-center justify-between bg-white border-b border-gray-200 px-4 py-2`
- `.nav-link` → `text-gray-600 hover:text-blue-600 px-3 py-2`
- `.dropdown-menu` → `absolute z-50 mt-2 w-48 bg-white border border-gray-200 rounded shadow-lg hidden` (`.active` → `block`)
- `.cart__dropdown-item-remove` etc. → `btn-danger` / `btn-sm` per reference table.

- [ ] **Step 1:** Rewrite `layout.html` stylesheet links to Tailwind; remove Bootstrap links.
- [ ] **Step 2:** Rewrite `navbar.html` classes per reference table; remove `data-toggle` attributes.
- [ ] **Step 3:** Rewrite `footer.html` classes per reference table.
- [ ] **Step 4:** Run `mvn -q -Djava.version=17 clean package -DskipTests`; visually confirm shell/nav/footer structure compiles (no runtime server needed — Thymeleaf template compilation is part of build).
- [ ] **Step 5:** Commit the three fragment files.

---

### Task 3: Convert public page templates

**Files:**
- Modify: `frontend/src/main/resources/templates/public/home.html`
- Modify: `frontend/src/main/resources/templates/public/cart.html`
- Modify: `frontend/src/main/resources/templates/public/categories.html`
- Modify: `frontend/src/main/resources/templates/public/category.html`
- Modify: `frontend/src/main/resources/templates/public/checkout.html`
- Modify: `frontend/src/main/resources/templates/public/product_details.html`
- Modify: `frontend/src/main/resources/templates/public/products.html`
- Modify: `frontend/src/main/resources/templates/public/products_bestselling.html`
- Modify: `frontend/src/main/resources/templates/public/login.html`
- Modify: `frontend/src/main/resources/templates/public/signup.html`
- Modify: `frontend/src/main/resources/templates/public/offers.html`
- Modify: `frontend/src/main/resources/templates/public/privacy.html`
- Modify: `frontend/src/main/resources/templates/public/about.html`
- Modify: `frontend/src/main/resources/templates/public/return_policy.html`
- Modify: `frontend/src/main/resources/templates/public/confirmEmail.html`
- Modify: `frontend/src/main/resources/templates/public/confirmEmailSuccess.html`
- Modify: `frontend/src/main/resources/templates/public/faq.html`
- Modify: `frontend/src/main/resources/templates/public/help.html`
- Modify: `frontend/src/main/resources/templates/public/search_fragment.html`
- Modify: `frontend/src/main/resources/templates/public/payment_form.html`
- Modify: `frontend/src/main/resources/templates/public/payment_success.html`

**Interfaces:**
- Consumes: shared fragments from Task 2; `tailwind.css`.
- Produces: all public-facing pages rendered with Tailwind classes only.

**Representative conversion** (`home.html` product card):

Before:
```html
<div class="col-md-3 product-card-b">
    <div class="card">
        <img class="card-img-top" th:src="${product.image}">
        <div class="card-body">
            <h5 th:text="${product.name}"></h5>
            <button class="btn btn-primary" th:data-add-cart="${product.id}">Add</button>
        </div>
    </div>
</div>
```

After:
```html
<div class="w-full md:w-1/4 product-card-b">
    <div class="card">
        <img class="card-img-top" th:src="${product.image}">
        <div class="card-body p-4">
            <h5 th:text="${product.name}"></h5>
            <button class="btn-primary" th:data-add-cart="${product.id}">Add</button>
        </div>
    </div>
</div>
```

`checkout.html` form fields: `form-control` → `form-control`, `form-group` → `form-group`, `btn btn-primary` → `btn-primary`, removed duplicate `content` attrs (already fixed prior), `th:object="${checkoutForm}"` retained.

`products_bestselling.html`: removed `product.new` badge (fixed prior); `.new` span styling moved to Tailwind if retained.

- [ ] **Step 1:** Convert `home.html`, `cart.html`, `categories.html`, `category.html`.
- [ ] **Step 2:** Convert `checkout.html`, `product_details.html`, `products.html`, `products_bestselling.html`.
- [ ] **Step 3:** Convert `login.html`, `signup.html`, `offers.html`, `privacy.html`, `about.html`, `return_policy.html`, `confirmEmail.html`, `confirmEmailSuccess.html`, `faq.html`, `help.html`, `search_fragment.html`, `payment_form.html`, `payment_success.html`.
- [ ] **Step 4:** Run `mvn -q -Djava.version=17 clean package -DskipTests`; grep confirms zero `bootstrap` occurrences in `public/`.
- [ ] **Step 5:** Commit all public page templates.

---

### Task 4: Convert public reusable fragments

**Files:**
- Modify: `frontend/src/main/resources/templates/public/fragments/categories.html`
- Modify: `frontend/src/main/resources/templates/public/fragments/productCardList3.html`
- Modify: `frontend/src/main/resources/templates/public/fragments/product_card.html`
- Modify: `frontend/src/main/resources/templates/public/fragments/product_card2.html`
- Modify: `frontend/src/main/resources/templates/public/fragments/slider.html`
- Modify: `frontend/src/main/resources/templates/public/fragments/subscription.html`
- Modify: `frontend/src/main/resources/templates/public/fragments/search.html`
- Modify: `frontend/src/main/resources/templates/public/fragments/banner.html`
- Modify: `frontend/src/main/resources/templates/public/fragments/call_action.html`

**Interfaces:**
- Consumes: `tailwind.css`.
- Produces: fragment markup consumed by public pages; `slider.html` indicators (`data-target`/`data-slide-to`) replaced with vanilla JS carousel controls.

**`slider.html`** — replace Bootstrap carousel indicators:

Before:
```html
<li data-target="#carouselExampleIndicators" data-slide-to="0" class="active"></li>
```

After:
```html
<li onclick="goToSlide(0)" class="bg-blue-600"></li>
<li onclick="goToSlide(1)" class="bg-gray-300"></li>
```

Add `goToSlide(n)` vanilla JS to `main.js` (Task 8).

- [ ] **Step 1:** Convert `categories.html`, `search.html`, `subscription.html`, `banner.html`, `call_action.html`.
- [ ] **Step 2:** Convert `productCardList3.html`, `product_card.html`, `product_card2.html` (unused fragments still converted for consistency).
- [ ] **Step 3:** Convert `slider.html` indicators to vanilla JS click handlers.
- [ ] **Step 4:** Run build; grep zero `bootstrap` in `public/fragments/`.
- [ ] **Step 5:** Commit all public fragments.

---

### Task 5: Convert admin page templates

**Files:**
- Modify: `frontend/src/main/resources/templates/admin/home/admin_home.html`
- Modify: `frontend/src/main/resources/templates/admin/categories/category_home.html`
- Modify: `frontend/src/main/resources/templates/admin/categories/category_create.html`
- Modify: `frontend/src/main/resources/templates/admin/categories/category_edit.html`
- Modify: `frontend/src/main/resources/templates/admin/products/product_home.html`
- Modify: `frontend/src/main/resources/templates/admin/products/product_create_form.html`
- Modify: `frontend/src/main/resources/templates/admin/products/product_edit_form.html`
- Modify: `frontend/src/main/resources/templates/admin/products/product_add_image.html`
- Modify: `frontend/src/main/resources/templates/admin/products/product_list.html`
- Modify: `frontend/src/main/resources/templates/admin/orders/order_details.html`
- Modify: `frontend/src/main/resources/templates/admin/orders/order_index.html`
- Modify: `frontend/src/main/resources/templates/admin/orders/order_list.html`
- Modify: `frontend/src/main/resources/templates/admin/orders/order_reports.html`
- Modify: `frontend/src/main/resources/templates/admin/settings/admin_settings.html`
- Modify: `frontend/src/main/resources/templates/admin/settings/general_settings.html`
- Modify: `frontend/src/main/resources/templates/admin/settings/payment_settings.html`
- Modify: `frontend/src/main/resources/templates/admin/settings/seo_settings.html`
- Modify: `frontend/src/main/resources/templates/admin/settings/shipping_settings.html`
- Modify: `frontend/src/main/resources/templates/admin/settings/social_settings.html`
- Modify: `frontend/src/main/resources/templates/admin/users/user_create_form.html`
- Modify: `frontend/src/main/resources/templates/admin/users/user_details.html`
- Modify: `frontend/src/main/resources/templates/admin/users/user_edit_form.html`
- Modify: `frontend/src/main/resources/templates/admin/users/users_index.html`
- Modify: `frontend/src/main/resources/templates/admin/users/users_list.html`
- Modify: `frontend/src/main/resources/templates/admin/address/city_create.html`
- Modify: `frontend/src/main/resources/templates/admin/address/city_list.html`
- Modify: `frontend/src/main/resources/templates/admin/address/country_create.html`
- Modify: `frontend/src/main/resources/templates/admin/address/country_list.html`
- Modify: `frontend/src/main/resources/templates/admin/address/state_create.html`
- Modify: `frontend/src/main/resources/templates/admin/carts/cart_details.html`
- Modify: `frontend/src/main/resources/templates/admin/carts/cart_index.html`
- Modify: `frontend/src/main/resources/templates/admin/carts/cart_list.html`
- Modify: `frontend/src/main/resources/templates/admin/emails/email_send.html`
- Modify: `frontend/src/main/resources/templates/admin/reports/report_home.html`

**Interfaces:**
- Consumes: `tailwind.css`; admin fragments from Task 6.
- Produces: all admin pages rendered with Tailwind; `.popover` inline `<style>` blocks removed (replaced with Tailwind arbitrary classes); table-driven pages use `.table` + `thead`/`tbody` utilities.

**Admin-specific notes:**
- `category_create.html`, `country_create.html`, `state_create.html` contain `<style>.popover{...}</style>` blocks — replace class usages with Tailwind arbitrary values (`absolute z-50 mt-2 w-48 bg-white border rounded shadow-lg`) and delete the `<style>` blocks.
- `cart_details.html` contains Bootstrap `navbar-toggler`/`collapse` markup (`data-toggle="collapse"` `data-target="#navbarNav"`) — convert to Tailwind `hidden`/`.active` toggle pattern and add JS toggle (Task 8).
- `user_edit_form.html`, `users_list.html`, `users_index.html` have HTML comments referencing "Bootstrap 5 JS" — delete comments.
- `payment_form.html` CSS references `.btn-outline-primary.dropdown-toggle.show` — migrate to Tailwind `.btn-outline-primary` + `.active` state.

- [ ] **Step 1:** Convert admin home, categories (3), products (6), orders (4).
- [ ] **Step 2:** Convert admin settings (6), users (5), address (5), carts (3).
- [ ] **Step 3:** Convert admin emails, reports, order_reports.
- [ ] **Step 4:** Remove `<style>.popover</style>` blocks and "Bootstrap 5 JS" comments; collapse `navbar-toggler` markup.
- [ ] **Step 5:** Run build; grep zero `bootstrap` in `admin/`.
- [ ] **Step 6:** Commit all admin templates.

---

### Task 6: Convert admin reusable fragments

**Files:**
- Modify: `frontend/src/main/resources/templates/admin/fragments/header.html`
- Modify: `frontend/src/main/resources/templates/admin/fragments/footer.html`
- Modify: `frontend/src/main/resources/templates/admin/fragments/navigation.html`

**Interfaces:**
- Consumes: `tailwind.css`.
- Produces: admin shell fragments; `header.html` dropdowns (`notification-dropdown`, `user-dropdown`, `user-dropdown-menu`, `dropdown-item`, `dropdown-divider`) converted to Tailwind + `active` toggle via JS.

**`header.html` dropdowns:**

Before:
```html
<div class="user-dropdown-menu">
    <a href="/logout" class="dropdown-item">Logout</a>
</div>
```

After:
```html
<div class="user-dropdown-menu hidden">
    <a href="/logout" class="block w-full px-4 py-2 text-gray-600 hover:bg-gray-100">Logout</a>
</div>
```

JS toggles `active` → `block` class on click (Task 8).

- [ ] **Step 1:** Convert `header.html` (dropdowns + nav links).
- [ ] **Step 2:** Convert `footer.html`, `navigation.html`.
- [ ] **Step 3:** Run build; grep zero `bootstrap` in `admin/fragments/`.
- [ ] **Step 4:** Commit admin fragments.

---

### Task 7: Convert error page templates

**Files:**
- Modify: `frontend/src/main/resources/templates/error/400.html`
- Modify: `frontend/src/main/resources/templates/error/404.html`
- Modify: `frontend/src/main/resources/templates/error/500.html`

**Interfaces:**
- Consumes: `tailwind.css`.
- Produces: error pages with Tailwind text/spacing utilities only.

**Representative:** `.text-danger` → `text-red-600`, `.container` → `max-w-7xl mx-auto px-4`, `.btn btn-primary` → `btn-primary`.

- [ ] **Step 1:** Convert all three error templates per reference table.
- [ ] **Step 2:** Run build; grep zero `bootstrap` in `error/`.
- [ ] **Step 3:** Commit error templates.

---

### Task 8: Vanilla JS replacements & main.js updates

**Files:**
- Modify: `frontend/src/main/resources/static/public/js/main.js`

**Interfaces:**
- Consumes: Tailwind class names from Tasks 2-7.
- Produces: dropdown/carousel/collapse behavior without Bootstrap JS; jQuery retained for CSRF/cart AJAX.

**Changes to `main.js`:**
1. Existing dropdown togglers (`dropdownMenuButton`/`dropdownMenu`, `dropdownCartButton`/`dropdownCart`) already use `classList.toggle("active")` — map `.active` to `display: block` in Tailwind via `source.css` or inline `style` toggle.
2. Add `goToSlide(n)` for `slider.html` indicators:
```js
function goToSlide(index) {
    const items = document.querySelectorAll('#carouselExampleIndicators li');
    items.forEach((li, i) => {
        li.className = i === index ? 'bg-blue-600' : 'bg-gray-300';
    });
    // slide carousel track via transform or show/hide .carousel-item
}
```
3. Add collapse toggle for `cart_details.html` navbar-toggler if present.
4. Remove any `bootstrap.bundle.min.js` references from templates (done in Tasks 2-7); remove `bootstrap.bundle.min.js` script tag from `layout.html`/fragments if present.
5. jQuery retained: `$("meta[name='_csrf']")` and `$.ajax` in cartRender remain.

- [ ] **Step 1:** Add `goToSlide` and collapse/navbar toggle functions to `main.js`; sync `.active` → `block` CSS class rule.
- [ ] **Step 2:** Remove `bootstrap.bundle.min.js` / `bootstrap.min.js` script tags from all templates.
- [ ] **Step 3:** Run build; confirm no `bootstrap` script references remain; grep zero `data-toggle`/`data-slide-to` in templates.
- [ ] **Step 4:** Commit `main.js`.

---

### Task 9: Full build + static-asset verification

**Files:** none new — verification task.

**Interfaces:**
- Consumes: all converted templates + built `tailwind.css`.
- Produces: confirmed green build and runtime-readable static asset.

- [ ] **Step 1:** Run `mvn -q -Djava.version=17 clean package -DskipTests` (both modules).
- [ ] **Step 2:** Start frontend on 8080 (backend on 8081 for cart/api calls) and load `/`, `/cart`, `/products`, `/admin/...` pages; confirm stylesheet loads with `text/css` MIME (not `application/json`) and dropdowns toggle on click.
- [ ] **Step 3:** Fix any template compilation errors found during server startup.
- [ ] **Step 4:** Commit if build was already committed in earlier tasks (no new files) — this task is a gate, no commit unless fixes were made.

---

### Task 10: Final commit & cleanup

**Files:** none.

**Interfaces:**
- Consumes: all prior task outputs.
- Produces: single final commit if any uncommitted changes remain; working tree clean except gitignored `.idea/`.

- [ ] **Step 1:** `git add -A` (excluding gitignored `.idea/`), review `git status`.
- [ ] **Step 2:** `git commit -m "feat: migrate all templates from Bootstrap to Tailwind CSS"` with `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- [ ] **Step 3:** `git log --oneline -3` and `git status --short` to confirm clean tree.

---

## Self-review against spec

1. **Spec coverage:** Every requirement in the design spec maps to a task: build pipeline (Task 1), layout/navbar/footer (Task 2), public pages (Task 3), public fragments (Task 4), admin pages (Task 5), admin fragments (Task 6), error pages (Task 7), vanilla JS (Task 8), build verification (Task 9), final commit (Task 10).
2. **Placeholder scan:** No TBD/TODO/implement-later patterns. All code blocks contain real content.
3. **Type consistency:** `tailwind.css` path `/public/css/tailwind.css` referenced in Task 1 and consumed in Tasks 2-7; `btn-primary`/`form-control`/`.table`/`.dropdown-menu` component names defined in Task 1's `source.css` and used throughout.

**Plan complete.** Saved to `docs/superpowers/plans/2026-10-27-tailwind-migration.md`.

Two execution options:

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration. Requires `superpowers:subagent-driven-development`.

**2. Inline Execution** - I execute tasks in this session directly, committing after each task as a checkpoint.

Which approach?

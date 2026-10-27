# Tailwind CSS Migration Design

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Convert all Thymeleaf templates from Bootstrap to Tailwind CSS, remove Bootstrap entirely, and install Tailwind via an npm + PostCSS build pipeline.

**Architecture:** Component-driven rebuild. Shared UI (navbar, footer, product cards, tables, forms, dropdowns, cart widget) extracted into Thymeleaf fragments so each pattern is converted once and reused everywhere. Every page template consumes fragments; no Bootstrap CSS or JS remains. Dropdowns and carousel interactions reimplemented in vanilla JS.

**Tech Stack:** Tailwind CSS v3.4 (current stable), PostCSS via `postcss-cli`, `autoprefixer`, `tailwindcss` npm packages; `frontend-maven-plugin` to install node/npm and run the PostCSS build during `mvn package`. Spring Boot serves `target/classes/static/tailwind.css` (built into `src/main/resources/static/css`) as the single stylesheet.

**Spec:** `docs/superpowers/specs/2026-10-27-tailwind-migration-design.md` (this file)

## Global Constraints

- Every template in `frontend/src/main/resources/templates/` is in scope: public/, admin/, error/, all fragments/.
- Bootstrap CSS (all `bootstrap.min.css`, `bootstrap.bundle.min.css`, `bootstrap.rtl.min.css` links) removed from every template.
- `bootstrap.bundle.min.js` removed; dropdown/carousel/collapse interactions become vanilla JS.
- No inline `<style>` blocks referencing Bootstrap classes remain.
- Tailwind built once into `src/main/resources/static/css/tailwind.css`; referenced via `th:href="@{/public/css/tailwind.css}"`.
- Custom utility overrides (dates, RTL tweaks) written as Tailwind arbitrary values or `@apply` in `src/main/resources/static/css/source.css`.
- `main.js` retains its existing dropdown toggle logic; class names updated to match Tailwind markup.

## Components & Templates (36 files)

### Root layout (1)
- `templates/public/layout.html` — base shell; swap Bootstrap CSS link for Tailwind; add `@tailwind` directives.

### Public pages (11)
- `public/home.html` — product grid via Tailwind grid; removed `product.new` badges (already fixed prior).
- `public/cart.html` — cart items table → Tailwind table utilities.
- `public/categories.html` — category grid.
- `public/category.html` — category products grid.
- `public/checkout.html` — form fields → Tailwind form utilities.
- `public/product_details.html` — image gallery + buy form.
- `public/products.html` — product listing table/grid.
- `public/products_bestselling.html` — bestselling product strip.
- `public/login.html` — auth form.
- `public/signup.html` — auth form.
- `public/offers.html`, `public/privacy.html`, `public/about.html`, `public/return_policy.html`, `public/confirmEmailSuccess.html`, `public/help.html`, `public/search_fragment.html` — text + link utilities.

### Public fragments (7)
- `public/fragments/navbar.html` — **critical**: dropdown sections (`dropdownMenu`, `dropdownCart`, category dropdown) reworked with Tailwind classes + vanilla JS toggles.
- `public/fragments/footer.html`, `public/fragments/categories.html`, `public/fragments/productCardList3.html`, `public/fragments/product_card.html`, `public/fragments/product_card2.html`, `public/fragments/slider.html` — slider indicators (`data-target`/`data-slide-to`) → vanilla JS carousel controls.
- `public/fragments/subscription.html` — subscribe form.
- `public/fragments/search.html` — search form.
- `public/fragments/banner.html`, `public/fragments/call_action.html` — text utilities.

### Admin pages (13)
- `admin/home/admin_home.html`, `admin/categories/category_home.html`, `admin/categories/category_create.html`, `admin/categories/category_edit.html`, `admin/products/product_home.html`, `admin/products/product_create_form.html`, `admin/products/product_edit_form.html`, `admin/products/product_add_image.html`, `admin/orders/order_home.html`, `admin/orders/order_index.html`, `admin/orders/order_details.html`, `admin/orders/order_list.html`, `admin/orders/order_reports.html`, `admin/settings/*` (5 files), `admin/users/*` (5 files), `admin/address/*` (4 files), `admin/carts/*` (3 files), `admin/emails/*`, `admin/reports/report_home.html` — forms and tables → Tailwind utilities.
- `admin/fragments/header.html` — user dropdown + notification dropdown → Tailwind + vanilla JS.

### Error pages (2)
- `error/404.html`, `error/500.html` — text utilities only.

(Total unique files to touch: ~40; the exact count is set at plan time.)

## Build pipeline

1. Add `frontend-maven-plugin` to `frontend/pom.xml` (installs node/npm locally, runs `npm install` + `npx postcss`).
2. Create `frontend/src/main/resources/static/css/source.css` with `@tailwind base; @tailwind components; @tailwind utilities;` plus project custom layers.
3. Create `frontend/tailwind.config.js` (content: all templates + static JS).
4. Create `frontend/postcss.config.js` (tailwindcss + autoprefixer).
5. Output `frontend/src/main/resources/static/css/tailwind.css` committed to git (built artifact, regenerated by build).
6. Every template: `<link th:href="@{/public/css/tailwind.css}" ...>` replaces Bootstrap links.
7. Remove `bootstrap.bundle.min.js` and `jquery-*.min.js` script tags where only used for Bootstrap (keep jQuery if custom JS depends on it — `main.js` uses `$()` for CSRF meta; keep jQuery solely for that or rewrite to fetch API).

## Dropdowns / interactions (vanilla JS)

| Element | Current | Target |
|---|---|---|
| Public navbar category dropdown | `class="menu-link dropdown"` + `data-toggle="dropdown"` | Tailwind classes + `onclick` toggle `active` class on menu |
| Public profile dropdown | `dropdownMenuButton`/`dropdownMenu` | Same toggle pattern |
| Cart dropdown | `dropdownCartButton`/`dropdownCart` | Already handled by `main.js`; class names synced |
| Admin header user dropdown | `user-dropdown`/`user-dropdown-menu` | Tailwind + vanilla JS toggle |

## Custom component CSS (via @apply in source.css)

- `.btn-primary`, `.btn-danger`, `.btn-sm`, `.btn-outline-primary` — Bootstrap button look replicated.
- `.form-control`, `.form-group` — form field look.
- `.table`, `.table-striped`, `.table-bordered` — table look.
- `.card`, `.card-body`, `.card-img-top` — card look.
- `.navbar`, `.navbar-toggler`, `.navbar-collapse`, `.nav-link` — navbar look.
- `.dropdown-menu`, `.dropdown-item`, `.dropdown-divider` — dropdown look.

## Success criteria

- `mvn -q -Djava.version=17 clean package -DskipTests` builds both modules.
- All templates reference `/public/css/tailwind.css`; zero `bootstrap` string remains in templates or `pom.xml`.
- Dropdowns and carousel render/interact identically to before (manual smoke test).
- No `application/json` MIME errors on static assets (previous fix, `/public/**` permitAll, remains).

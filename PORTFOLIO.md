# Portfolio – Albert Egi

Your employer-ready portfolio is in **`portfolio/index.html`**. It uses **Tailwind CSS** (CDN), a dark theme with gradient background, and your **GitHub profile picture** by default.

---

## What’s included

- **Tailwind CSS** via CDN (no build step)
- **Dark theme** with slate/blue gradient background
- **Profile photo** – currently your GitHub avatar; easy to swap for a professional headshot
- **Sections**: Hero, About, Tech Stack, Featured Projects, Education, What I’m Looking For, Contact
- **Responsive** layout and hover states on cards and links
- **Inter** font for clean, professional typography

---

## What you need to do

### 1. Create the portfolio repo on GitHub

1. Go to [github.com/new](https://github.com/new).
2. Repository name: **`albertegi.github.io`** (exactly).
3. Set visibility to **Public**, then **Create repository**.

### 2. Put the portfolio live

**Option A – Only the HTML file (simplest)**  
- Copy the contents of **`portfolio/index.html`** from this project.  
- In the `albertegi.github.io` repo, click **Add file → Create new file**.  
- Name the file **`index.html`**.  
- Paste the content, then **Commit changes**.

**Option B – Clone and push**  
- Clone your new repo: `git clone https://github.com/albertegi/albertegi.github.io.git`  
- Copy the **`portfolio/index.html`** file into the root of that repo and rename it to **`index.html`** (if needed).  
- Commit and push:
  ```bash
  cd albertegi.github.io
  git add index.html
  git commit -m "Add portfolio"
  git push origin main
  ```

### 3. Turn on GitHub Pages

1. In **`albertegi.github.io`** go to **Settings → Pages**.  
2. Under **Build and deployment**, set:  
   - **Source**: Deploy from a branch  
   - **Branch**: `main` (or `master`)  
   - **Folder**: `/ (root)`  
3. Click **Save**.  
4. Wait 1–2 minutes. Your site will be at: **https://albertegi.github.io**

### 4. Add the link to your GitHub profile

1. Open your profile README (e.g. **`albertegi/albertegi`** repo, `README.md`).  
2. Replace the portfolio placeholder with:
   ```markdown
   - **Portfolio**: [albertegi.github.io](https://albertegi.github.io)
   ```
   or the HTML link you use there.

---

## Optional: Use your own photo

Right now the portfolio uses your **GitHub avatar**. To use a professional headshot:

1. Add a photo (e.g. `photo.jpg`) to the **root** of your `albertegi.github.io` repo (or in a folder like `assets/`).  
2. In **`index.html`**, find this line:
   ```html
   src="https://avatars.githubusercontent.com/u/185676082?v=4"
   ```
3. Replace it with your image path, for example:
   ```html
   src="photo.jpg"
   ```
   or `src="assets/photo.jpg"` if you put it in a folder.  
4. Commit and push; the new image will show after refresh.

---

## File location in this repo

| File | Purpose |
|------|--------|
| **`portfolio/index.html`** | Full portfolio page (use this for GitHub Pages) |
| **`PORTFOLIO.md`** | This guide and instructions |

Once **`index.html`** is in the root of **`albertegi.github.io`** and Pages is enabled, your portfolio is live and you can share **https://albertegi.github.io** with employers.

---

## Troubleshooting: site still shows old content

GitHub Pages only uses **`index.html`** when it’s in the **root** of the repo and the right branch is deployed. Do this:

### 1. Put `index.html` in the repo root (not in a folder)

- Open **https://github.com/albertegi/albertegi.github.io**
- When you’re on the repo’s main page, the file list should show **`index.html`** at the **top level** (same level as `README.md`), **not** inside a folder like `portfolio/`.
- If you only have **`portfolio/index.html`**, then the live site is **https://albertegi.github.io/portfolio/** and the root still shows the old page.  
  **Fix:** In the repo, open `portfolio/index.html` → **Edit** → select all → copy. Then go back to the repo root → **Add file → Create new file** → name it **`index.html`** (no folder) → paste → Commit. Now the root **is** your portfolio.

### 2. Make sure GitHub Pages uses the right branch

- Repo **Settings → Pages**
- **Source:** Deploy from a branch  
- **Branch:** `main` (or whatever branch has your `index.html`)  
- **Folder:** `/ (root)`  
- Save and wait 1–2 minutes.

### 3. Stop the old page from showing (optional)

- If you had a long **README.md** and the site still looks like that, either:
  - Replace **README.md** with a short line (e.g. “Portfolio: https://albertegi.github.io”) and ensure **`index.html`** exists in the root (step 1), or  
  - Temporarily **rename** `README.md` to `README.md.bak` and push; then the site should serve **`index.html`** only. (You can rename it back later; the repo page will then show the README again.)

### 4. Hard refresh or incognito

- **Hard refresh:** `Ctrl+Shift+R` (Windows/Linux) or `Cmd+Shift+R` (Mac).  
- Or open **https://albertegi.github.io** in an **incognito/private** window so cache doesn’t show the old version.

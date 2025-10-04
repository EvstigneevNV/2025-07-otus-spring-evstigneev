const api = {
    books: '/api/books',
    authors: '/api/authors',
    genres: '/api/genres',
    book: id => `/api/books/${id}`,
    comments: bookId => `/api/books/${bookId}/comments`,
    comment: (bookId, id) => `/api/books/${bookId}/comments/${id}`
};

async function jget(url) {
    const r = await fetch(url);
    if (!r.ok) throw new Error(await r.text());
    return r.json();
}

async function jpost(url, body) {
    const r = await fetch(url, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(body)
    });
    if (!r.ok) throw new Error(await r.text());
    return r.status === 204 ? null : r.json();
}

async function jput(url, body) {
    const r = await fetch(url, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(body)
    });
    if (!r.ok) throw new Error(await r.text());
    return r.json();
}

async function jdel(url) {
    const r = await fetch(url, {method: 'DELETE'});
    if (!r.ok) throw new Error(await r.text());
    return null;
}

function qs(sel, root = document) {
    return root.querySelector(sel);
}

function qsa(sel, root = document) {
    return [...root.querySelectorAll(sel)];
}

function byId(id) {
    return document.getElementById(id);
}

function msg(text) {
    const el = qs('.msg');
    if (el) {
        el.textContent = text;
        el.style.display = 'inline-block';
        setTimeout(() => el.style.display = 'none', 2000);
    }
}

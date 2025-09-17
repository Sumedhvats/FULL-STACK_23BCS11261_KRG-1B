const products = [
    { id: 1, name: "Laptop", category: "electronics", price: 120000 },
    { id: 2, name: "T-Shirt", category: "apparel", price: 1000 },
    { id: 3, name: "Hard Drive", category: "electronics", price: 6500 },
    { id: 4, name: "Novel", category: "books", price: 600 },
    { id: 5, name: "Smartphone", category: "electronics", price: 65000 },
    { id: 6, name: "Jeans", category: "apparel", price: 2000 },
    { id: 7, name: "Science Textbook", category: "books", price: 4000 },
    { id: 8, name: "Headphones", category: "electronics", price: 3000 },
    { id: 9, name: "Jacket", category: "apparel", price: 5000 },
    { id: 10, name: "Cookbook", category: "books", price: 800 }
];

const productList = document.getElementById('product-list');
const categoryFilter = document.getElementById('category-filter');

function displayProducts(filteredProducts) {
    productList.innerHTML = filteredProducts.map(product => `
        <div class="product-card">
            <h3>${product.name}</h3>
            <p>Category: ${product.category}</p>
            <p class="price">₹${product.price}</p>
        </div>
    `).join('');
}

function filterProducts() {
    const selectedCategory = categoryFilter.value;
    let filteredProducts;
    if (selectedCategory === 'all') {
        filteredProducts = products;
    } else {
        filteredProducts = products.filter(product => product.category === selectedCategory);
    }
    displayProducts(filteredProducts);
}

categoryFilter.addEventListener('change', filterProducts);

document.addEventListener('DOMContentLoaded', () => {
    filterProducts();
});
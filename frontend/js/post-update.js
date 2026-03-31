document.addEventListener("DOMContentLoaded", async () => {
  const urlParams = new URLSearchParams(window.location.search);
  const postId = urlParams.get("id");

  const form = document.getElementById("update-post-form");
  const alertContainer = document.getElementById("alert-container");
  const categorySelect = document.getElementById("category");
  const authorNameInput = document.getElementById("authorName");

  let postData = null;

  // Fetch post details to pre-fill form
  async function loadPost() {
    try {
      const post = await apiGet(apiUrl(`/api/posts/${postId}`));
      if (!post) return;
      postData = post;

      document.getElementById("postId").value = post.id;
      document.getElementById("title").value = post.title;
      document.getElementById("content").value = post.content;
      authorNameInput.value = post.authorName || "Unknown";
    } catch (err) {
      showAlert(err.message, "error");
    }
  }

  async function loadCategories() {
    try {
      const categories = await apiGet(apiUrl("/api/categories"));
      if (!categories) return;

      categories.forEach((cat) => {
        const option = document.createElement("option");
        option.value = cat.id;
        option.textContent = cat.catName || cat.name;
        categorySelect.appendChild(option);
      });

      if (postData) {
        categorySelect.value = String(postData.categoryId);
      }
    } catch (err) {
      showAlert(err.message, "error");
    }
  }

  // Show alert
  function showAlert(message, type = "success") {
    alertContainer.innerHTML = `<div class="alert-${type}">${message}</div>`;
    setTimeout(() => (alertContainer.innerHTML = ""), 3000);
  }

  // Handle form submission
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const title = document.getElementById("title").value.trim();
    const content = document.getElementById("content").value.trim();

    const payload = {
      title,
      content,
      categoryId: Number(categorySelect.value),
    };

    try {
      const updatedPost = await apiPut(apiUrl(`/api/posts/${postId}`), payload);
      if (!updatedPost) return;

      showAlert("Post updated successfully!", "success");
      // Redirect to posts.html after 1 second
      setTimeout(() => (window.location.href = "posts.html"), 1000);
    } catch (err) {
      showAlert(err.message, "error");
    }
  });

  // Initial load
  if (postId) {
    await loadPost();
    await loadCategories();
  }
});

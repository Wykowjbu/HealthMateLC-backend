// Function hủy chung cho tất cả form
function cancelForm(formId, targetPanelId) {
  const form = document.querySelector(formId);
  if (form) {
    // Nếu là form thêm sản phẩm thì chỉ ẩn custom input, không reset, không chuyển panel
    if (formId === "#panel-add-product .panel-form") {
      const productTypeCustom = document.getElementById("product-type-custom");
      const productUnitCustom = document.getElementById("product-unit-custom");
      if (productTypeCustom) {
        productTypeCustom.style.display = "none";
        productTypeCustom.required = false;
      }
      if (productUnitCustom) {
        productUnitCustom.style.display = "none";
        productUnitCustom.required = false;
      }
      // Không reset form, không chuyển panel
      return;
    }
    // Các form khác: reset và chuyển panel như cũ
    form.reset();
    if (targetPanelId) {
      document.querySelectorAll(".panel").forEach(panel => {
        panel.style.display = "none";
      });
      const targetPanel = document.getElementById(targetPanelId);
      if (targetPanel) {
        targetPanel.style.display = "block";
      }
    }
  }
}

// Initialize cancel buttons
function initializeCancelButtons() {
  // Nút hủy tạo tài khoản
  const cancelAddAccount = document.getElementById("cancel-add-account");
  if (cancelAddAccount) {
    cancelAddAccount.addEventListener("click", function() {
      cancelForm("#panel-add-account .panel-form", "panel-list-accounts");
    });
  }

  // Nút hủy thêm sản phẩm
  const cancelAddProduct = document.getElementById("cancel-add-product");
  if (cancelAddProduct) {
    cancelAddProduct.addEventListener("click", function() {
      cancelForm("#panel-add-product .panel-form"); // Không truyền targetPanelId
    });
  }

  // Nút hủy chỉnh sửa nhà thuốc
  const cancelEditStore = document.getElementById("cancel-edit-store");
  if (cancelEditStore) {
    cancelEditStore.addEventListener("click", function() {
      cancelForm("#panel-edit-store .panel-form", "panel-list-stores");
    });
  }

  // Nút hủy tạo nhà thuốc
  const cancelCreateStore = document.getElementById("cancel-create-store");
  if (cancelCreateStore) {
    cancelCreateStore.addEventListener("click", function() {
      cancelForm("#panel-create-store .panel-form", "panel-list-stores");
    });
  }
} 
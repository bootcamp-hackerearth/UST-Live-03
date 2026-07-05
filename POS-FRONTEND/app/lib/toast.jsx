// app/lib/toast.js

export const showToast = (message, type = 'info', duration = 4000) => {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.style.cssText = `
      position: fixed;
      top: 30px;
      right: 24px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 20px;
    `;
    document.body.appendChild(container);
  }

  const colors = {
    success: { bg: '#10b981', text: 'white', icon: '✓' },
    error: { bg: '#ef4444', text: 'white', icon: '✕' },
    info: { bg: '#006E74', text: 'white', icon: 'ℹ' },
    warning: { bg: '#0097AC', text: 'white', icon: '⚠' },
  };

  const color = colors[type] || colors.info;

  const toast = document.createElement('div');
  toast.style.cssText = `
    background-color: ${color.bg};
    color: ${color.text};
    padding: 12px 16px;
    border-radius: 6px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    animation: slideInToast 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    font-size: 13px;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 8px;
    min-width: 280px;
    max-width: 400px;
  `;

  toast.innerHTML = `
    <span style="font-size: 16px; flex-shrink: 0; line-height: 1;">${color.icon}</span>
    <span style="flex: 1; line-height: 1.4;">${message}</span>
  `;

  if (!document.getElementById('toast-styles')) {
    const style = document.createElement('style');
    style.id = 'toast-styles';
    style.textContent = `
      @keyframes slideInToast {
        from {
          transform: translateX(440px);
          opacity: 0;
        }
        to {
          transform: translateX(0);
          opacity: 1;
        }
      }
      @keyframes slideOutToast {
        from {
          transform: translateX(0);
          opacity: 1;
        }
        to {
          transform: translateX(440px);
          opacity: 0;
        }
      }
    `;
    document.head.appendChild(style);
  }

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.animation = 'slideOutToast 0.3s ease-out forwards';
    setTimeout(() => {
      toast.remove();
      if (container.children.length === 0) container.remove();
    }, 300);
  }, duration);

  return toast;
};

export const confirmToast = (message, onConfirm, onCancel) => {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.style.cssText = `
      position: fixed;
      top: 24px;
      right: 24px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 8px;
    `;
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.style.cssText = `
    background-color: #0097AC;
    color: white;
    padding: 16px;
    border-radius: 6px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    animation: slideInToast 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    font-size: 13px;
    font-weight: 500;
    min-width: 300px;
    max-width: 400px;
  `;

  toast.innerHTML = `
    <div style="margin-bottom: 12px; display: flex; align-items: flex-start; gap: 8px;">
      <span style="font-size: 16px; flex-shrink: 0; line-height: 1.2;">⚠</span>
      <span style="flex: 1; line-height: 1.4;">${message}</span>
    </div>
    <div style="display: flex; gap: 8px; justify-content: flex-end;">
      <button id="toast-cancel" style="
        background-color: transparent;
        color: white;
        padding: 6px 12px;
        border: 1px solid rgba(255, 255, 255, 0.3);
        border-radius: 4px;
        cursor: pointer;
        font-size: 12px;
        font-weight: 600;
        transition: all 0.2s;
      ">
        Cancel
      </button>
      <button id="toast-confirm" style="
        background-color: white;
        color: #0097AC;
        padding: 6px 12px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 12px;
        font-weight: 600;
        transition: all 0.2s;
      ">
        Confirm
      </button>
    </div>
  `;

  container.appendChild(toast);

  const confirmBtn = toast.querySelector('#toast-confirm');
  const cancelBtn = toast.querySelector('#toast-cancel');

  const cleanup = () => {
    toast.style.animation = 'slideOutToast 0.3s ease-out forwards';
    setTimeout(() => {
      toast.remove();
      if (container.children.length === 0) container.remove();
    }, 300);
  };

  confirmBtn.addEventListener('click', () => {
    cleanup();
    onConfirm?.();
  });

  cancelBtn.addEventListener('click', () => {
    cleanup();
    onCancel?.();
  });

  confirmBtn.addEventListener('mouseover', () => {
    confirmBtn.style.backgroundColor = '#f0f0f0';
  });
  confirmBtn.addEventListener('mouseout', () => {
    confirmBtn.style.backgroundColor = 'white';
  });

  cancelBtn.addEventListener('mouseover', () => {
    cancelBtn.style.borderColor = 'rgba(255, 255, 255, 0.6)';
    cancelBtn.style.backgroundColor = 'rgba(255, 255, 255, 0.1)';
  });
  cancelBtn.addEventListener('mouseout', () => {
    cancelBtn.style.borderColor = 'rgba(255, 255, 255, 0.3)';
    cancelBtn.style.backgroundColor = 'transparent';
  });
};
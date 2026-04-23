// 定义防止重复点击的指令
export const preventReClick = {
    created(el, binding, vnode, prevVnode) {
      el.addEventListener("click", (e) => customClick(e, binding, el));
    },
    // 销毁
    unmounted(el, binding, vnode) {
      el.removeEventListener("click", customClick);
    },
  };
  
  const customClick = (e, binding, el) => {
    e.stopPropagation();
    if (!el.disabled) {
      el.disabled = true;
      setTimeout(() => {
        el.disabled = false;
      }, binding.arg || 3000);
  
      // 正常触发点击事件
      binding.value();
    } else {
      // 拦截点击事件
    }
  };
  
  export const setupAuthDirective = (app) => {
    app.directive("click", preventReClick);
  };
  
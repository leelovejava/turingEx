const baseSize = 16
/** 与 index.scss 中移动端基准一致；大屏不再按视口放大 rem，避免 PC 上元素比例失控 */
const DESIGN_WIDTH = 828
const MIN_WIDTH = 320

function setRem() {
    const vw = document.documentElement.clientWidth
    const w = Math.min(Math.max(vw, MIN_WIDTH), DESIGN_WIDTH)
    const scale = w / DESIGN_WIDTH
    document.documentElement.style.fontSize = baseSize * scale + 'px'
}

setRem()
window.addEventListener('resize', setRem)

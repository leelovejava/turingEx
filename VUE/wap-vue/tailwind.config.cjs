/** @type {import('tailwindcss').Config} */

module.exports = {
  darkMode: 'class', // or 'media' or 'class'
  purge: [
    "./index.html",
    "./src/**/*.vue",
    "./src/**/*.js",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#1F2025', // 主色（文字等）
        gray: '#878A96', // 文字颜色
        'btn-gray': '#F1F3F9', // 背景灰
        'bd-gray': '#C3C4CD', // 下单border
        up: '#06CDA5',  // 升
        down: '#F43368' // 降
      },
      height: {
        '18': '4.5rem',
        '192': '48rem'
      },
      width: {
        '18': '4.5rem',
        '160': '38rem',
      }
    },
    fontSize: {
      '16': '1rem',
      '18': '1.125rem',
      '20': '1.25rem',
      '22': '1.375rem',
      '24': '1.5rem',
      '26': '1.625rem',
      '28': '1.75rem',
      '30': '1.875rem',
      '32': '2rem',
      '34': '2.125rem',
      '36': '2.25rem',
      '38': '2.375rem',
      '40': '2.5rem',
      '42': '2.625rem',
      '44': '2.75rem',
      '46': '2.875rem',
      '48': '3rem',
      '50': '3.125rem',
      '60': '3.75rem',
      '66': '4.125rem'
    }
  },
  variants: {
    extend: {},
  },
  plugins: [

  ],
}

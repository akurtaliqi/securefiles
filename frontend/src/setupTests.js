import '@testing-library/jest-dom'

global.ResizeObserver = class ResizeObserver {
  observe () {}
  unobserve () {}
  disconnect () {}
}

Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: (query) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: () => {},
    removeListener: () => {},
    addEventListener: () => {},
    removeEventListener: () => {},
    dispatchEvent: () => false
  })
})

window.getComputedStyle = () => ({
  getPropertyValue: () => '',
  overflow: '',
  overflowX: '',
  overflowY: '',
  width: '0',
  height: '0'
})

HTMLAnchorElement.prototype.click = () => {}


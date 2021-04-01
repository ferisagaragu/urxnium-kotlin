export interface SweetAlert2Message {
  icon: 'success' | 'error' | 'warning' | 'info' | 'question',
  title: string,
  text: string,
  html?: HTMLElement | string,
  footer?: string,
  confirmButtonText?: string,
  cancelButtonText?: string,
  showCancelButton?: boolean,
  showCloseButton?: boolean,
  confirmButtonColor?: string,
  cancelButtonColor?: string
}

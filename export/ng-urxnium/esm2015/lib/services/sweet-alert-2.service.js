import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import Swal from 'sweetalert2';
import * as i0 from "@angular/core";
export class SweetAlert2Service {
    fire(message) {
        let observerNext = null;
        const { confirmButtonColor, cancelButtonColor } = message;
        Swal.fire(Object.assign(Object.assign({}, message), { allowOutsideClick: false, confirmButtonColor: confirmButtonColor ? confirmButtonColor : '#00d1b2', cancelButtonColor: cancelButtonColor ? cancelButtonColor : '#F44336', reverseButtons: true, closeButtonHtml: `<button class="mat-focus-indicator mr-2 mat-stroked-button mat-button-base mat-accent">Cancelar</button>` })).then(resp => {
            if (observerNext) {
                observerNext.next(resp.isConfirmed);
            }
        });
        return new Observable(observer => observerNext = observer);
    }
}
SweetAlert2Service.ɵfac = function SweetAlert2Service_Factory(t) { return new (t || SweetAlert2Service)(); };
SweetAlert2Service.ɵprov = i0.ɵɵdefineInjectable({ token: SweetAlert2Service, factory: SweetAlert2Service.ɵfac, providedIn: 'root' });
/*@__PURE__*/ (function () { i0.ɵsetClassMetadata(SweetAlert2Service, [{
        type: Injectable,
        args: [{
                providedIn: 'root'
            }]
    }], null, null); })();
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic3dlZXQtYWxlcnQtMi5zZXJ2aWNlLmpzIiwic291cmNlUm9vdCI6IkM6L2RldmVsb3AvcGVjaGJsZW5kYS91cnhuaXVtL3VyeG5pdW0tYW5ndWxhci9wcm9qZWN0cy9uZy11cnhuaXVtL3NyYy8iLCJzb3VyY2VzIjpbImxpYi9zZXJ2aWNlcy9zd2VldC1hbGVydC0yLnNlcnZpY2UudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLFVBQVUsRUFBRSxNQUFNLGVBQWUsQ0FBQztBQUUzQyxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sTUFBTSxDQUFDO0FBQ2xDLE9BQU8sSUFBSSxNQUFNLGFBQWEsQ0FBQzs7QUFLL0IsTUFBTSxPQUFPLGtCQUFrQjtJQUU3QixJQUFJLENBQUMsT0FBMkI7UUFDOUIsSUFBSSxZQUFZLEdBQUcsSUFBSSxDQUFDO1FBQ3hCLE1BQU0sRUFBRSxrQkFBa0IsRUFBRSxpQkFBaUIsRUFBRSxHQUFHLE9BQU8sQ0FBQztRQUUxRCxJQUFJLENBQUMsSUFBSSxpQ0FDSixPQUFPLEtBQ1YsaUJBQWlCLEVBQUUsS0FBSyxFQUN4QixrQkFBa0IsRUFBRSxrQkFBa0IsQ0FBQyxDQUFDLENBQUMsa0JBQWtCLENBQUMsQ0FBQyxDQUFDLFNBQVMsRUFDdkUsaUJBQWlCLEVBQUUsaUJBQWlCLENBQUMsQ0FBQyxDQUFDLGlCQUFpQixDQUFDLENBQUMsQ0FBQyxTQUFTLEVBQ3BFLGNBQWMsRUFBRSxJQUFJLEVBQ3BCLGVBQWUsRUFBRSwwR0FBMEcsSUFDM0gsQ0FBQyxJQUFJLENBQUMsSUFBSSxDQUFDLEVBQUU7WUFDYixJQUFJLFlBQVksRUFBRTtnQkFDaEIsWUFBWSxDQUFDLElBQUksQ0FBQyxJQUFJLENBQUMsV0FBc0IsQ0FBQyxDQUFDO2FBQ2hEO1FBQ0gsQ0FBQyxDQUFDLENBQUM7UUFFSCxPQUFPLElBQUksVUFBVSxDQUFDLFFBQVEsQ0FBQyxFQUFFLENBQUMsWUFBWSxHQUFHLFFBQVEsQ0FBQyxDQUFDO0lBQzdELENBQUM7O29GQXBCVSxrQkFBa0I7MERBQWxCLGtCQUFrQixXQUFsQixrQkFBa0IsbUJBRmpCLE1BQU07a0RBRVAsa0JBQWtCO2NBSDlCLFVBQVU7ZUFBQztnQkFDVixVQUFVLEVBQUUsTUFBTTthQUNuQiIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7IEluamVjdGFibGUgfSBmcm9tICdAYW5ndWxhci9jb3JlJztcbmltcG9ydCB7IFN3ZWV0QWxlcnQyTWVzc2FnZSB9IGZyb20gJy4uL2ludGVyZmFjZXMvc3dlZXQtYWxlcnQtMi1tZXNzYWdlJztcbmltcG9ydCB7IE9ic2VydmFibGUgfSBmcm9tICdyeGpzJztcbmltcG9ydCBTd2FsIGZyb20gJ3N3ZWV0YWxlcnQyJztcblxuQEluamVjdGFibGUoe1xuICBwcm92aWRlZEluOiAncm9vdCdcbn0pXG5leHBvcnQgY2xhc3MgU3dlZXRBbGVydDJTZXJ2aWNlIHtcblxuICBmaXJlKG1lc3NhZ2U6IFN3ZWV0QWxlcnQyTWVzc2FnZSk6IE9ic2VydmFibGU8Ym9vbGVhbj4ge1xuICAgIGxldCBvYnNlcnZlck5leHQgPSBudWxsO1xuICAgIGNvbnN0IHsgY29uZmlybUJ1dHRvbkNvbG9yLCBjYW5jZWxCdXR0b25Db2xvciB9ID0gbWVzc2FnZTtcblxuICAgIFN3YWwuZmlyZSh7XG4gICAgICAuLi5tZXNzYWdlLFxuICAgICAgYWxsb3dPdXRzaWRlQ2xpY2s6IGZhbHNlLFxuICAgICAgY29uZmlybUJ1dHRvbkNvbG9yOiBjb25maXJtQnV0dG9uQ29sb3IgPyBjb25maXJtQnV0dG9uQ29sb3IgOiAnIzAwZDFiMicsXG4gICAgICBjYW5jZWxCdXR0b25Db2xvcjogY2FuY2VsQnV0dG9uQ29sb3IgPyBjYW5jZWxCdXR0b25Db2xvciA6ICcjRjQ0MzM2JyxcbiAgICAgIHJldmVyc2VCdXR0b25zOiB0cnVlLFxuICAgICAgY2xvc2VCdXR0b25IdG1sOiBgPGJ1dHRvbiBjbGFzcz1cIm1hdC1mb2N1cy1pbmRpY2F0b3IgbXItMiBtYXQtc3Ryb2tlZC1idXR0b24gbWF0LWJ1dHRvbi1iYXNlIG1hdC1hY2NlbnRcIj5DYW5jZWxhcjwvYnV0dG9uPmBcbiAgICB9KS50aGVuKHJlc3AgPT4ge1xuICAgICAgaWYgKG9ic2VydmVyTmV4dCkge1xuICAgICAgICBvYnNlcnZlck5leHQubmV4dChyZXNwLmlzQ29uZmlybWVkIGFzIGJvb2xlYW4pO1xuICAgICAgfVxuICAgIH0pO1xuXG4gICAgcmV0dXJuIG5ldyBPYnNlcnZhYmxlKG9ic2VydmVyID0+IG9ic2VydmVyTmV4dCA9IG9ic2VydmVyKTtcbiAgfVxuXG59XG4iXX0=
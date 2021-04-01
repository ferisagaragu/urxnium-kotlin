import { Injectable } from '@angular/core';
import { SweetAlert2Message } from '../interfaces/sweet-alert-2-message';
import { Observable } from 'rxjs';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class SweetAlert2Service {

  fire(message: SweetAlert2Message): Observable<boolean> {
    let observerNext = null;
    const { confirmButtonColor, cancelButtonColor } = message;

    Swal.fire({
      ...message,
      allowOutsideClick: false,
      confirmButtonColor: confirmButtonColor ? confirmButtonColor : '#00d1b2',
      cancelButtonColor: cancelButtonColor ? cancelButtonColor : '#F44336',
      reverseButtons: true,
      closeButtonHtml: `<button class="mat-focus-indicator mr-2 mat-stroked-button mat-button-base mat-accent">Cancelar</button>`
    }).then(resp => {
      if (observerNext) {
        observerNext.next(resp.isConfirmed as boolean);
      }
    });

    return new Observable(observer => observerNext = observer);
  }

}

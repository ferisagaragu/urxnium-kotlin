import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class SignInWindowService {

  open(url: string, event?: MouseEvent): Observable<string> {
    return new Observable<string>(observable => {
      let i = 0;
      const dialog = window.open(
        url,
        'targetWindow',
        `toolbar=no,
        location=center,
        status=no,
        menubar=no,
        scrollbars=no,
        resizable=no,
        width=400,
        height=600,
        ${event ? `screenX=${event.screenX}` : ''},
        ${event ? `screenY=${event.screenY}` : ''}`
      );

      const interval = setInterval(() => {
          try {
            const href = dialog.location.href;

            if (href !== undefined && href.toString().includes('http')) {
              dialog.close();
              clearInterval(interval);
              observable.next(href.split('?code=')[1]);
            }
          } catch (e) { }

          if (i === 10) {
            clearInterval(interval);
            observable.error();
          }

          i++;
        }, 1000
      );
    });
  }

}

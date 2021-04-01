import { Component } from '@angular/core';
import { SweetAlert2Service } from '../../projects/ng-urxnium/src/lib/services/sweet-alert-2.service';
import { SignInWindowService } from 'ng-urxnium';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'urxnium-angular';

  constructor(
    swal: SweetAlert2Service,
    signInWindow: SignInWindowService
  ) {
    signInWindow.open('https://sweetalert2.github.io/#examples').subscribe(resp => {
      console.log(resp);
    });

    swal.fire({
      icon: 'success',
      title: 'Perron',
      text: 'Hola'
    }).subscribe(resp => {
      console.log(resp);

      swal.fire({
        icon: 'error',
        title: 'upps',
        text: 'Salio mal'
      });

    });
  }

}

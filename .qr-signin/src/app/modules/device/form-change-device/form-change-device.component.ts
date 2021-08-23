import { Component } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { UserService } from '../../../core/http/user.service';

@Component({
  selector: 'app-form-change-device',
  templateUrl: './form-change-device.component.html',
  styleUrls: ['./form-change-device.component.scss']
})
export class FormChangeDeviceComponent {

  email = new FormControl(
    '',
    [
      Validators.email,
      Validators.required
    ]
  );
  sent: boolean;
  load: boolean;

  constructor(private userService: UserService) {
    this.sent = false;
    this.load = false;
  }

  onRecover(): void {
    if (this.email.invalid) {
      return;
    }

    this.load = true;
    this.email.disable();

    this.userService.recoverQRAccount(this.email.value).subscribe(_ => {
      this.sent = true;
      this.load = false;
      this.email.enable();
    }, _ => {
      this.load = false;
      this.email.enable();
      this.email.setErrors({ invalid: 'error' });
    });
  }

}

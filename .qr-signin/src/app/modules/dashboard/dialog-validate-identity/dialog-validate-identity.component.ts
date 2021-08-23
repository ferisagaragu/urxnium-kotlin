import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserService } from '../../../core/http/user.service';

@Component({
  selector: 'app-dialog-validate-identity',
  templateUrl: './dialog-validate-identity.component.html',
  styleUrls: ['./dialog-validate-identity.component.scss']
})
export class DialogValidateIdentityComponent implements OnInit {

  load: boolean;
  verifyCode = new FormControl(
    '',
    [
      Validators.pattern(/^\d{2} - \d{2} - \d{2}$/)
    ]
  );

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: any,
    private dialogRef: MatDialogRef<DialogValidateIdentityComponent>,
    private userService: UserService
  ) { }

  ngOnInit(): void { }

  onVerifyAccount(): void {
    if (this.verifyCode.invalid) {
      return;
    }

    this.load = true;
    this.verifyCode.disable();

    this.userService.activateQRAccount({
      secret: this.data,
      verifyCode: this.verifyCode.value
    }).subscribe(resp => {
      this.load  = false;
      this.verifyCode.enable();
      this.dialogRef.close(resp);
    }, ({ error }) => {
      this.load  = false;
      this.verifyCode.enable();

      if (error.message.toString().includes('JWT')) {
        this.dialogRef.close(null);
      } else {
        this.verifyCode.setErrors({ badCode: error.message });
      }
    });

  }

}

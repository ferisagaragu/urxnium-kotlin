import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../core/http/user.service';
import { DeviceUUID } from 'device-uuid';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DialogValidateIdentityComponent } from '../dialog-validate-identity/dialog-validate-identity.component';

@Component({
  selector: 'app-form-user',
  templateUrl: './form-user.component.html',
  styleUrls: ['./form-user.component.scss']
})
export class FormUserComponent implements OnInit {

  form: FormGroup;
  token: string;
  load: boolean;

  @Output() code: EventEmitter<string>;
  @Output() expired: EventEmitter<void>;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private route: ActivatedRoute,
    private dialog: MatDialog
  ) {
    this.code = new EventEmitter<string>();
    this.expired = new EventEmitter<void>();

    this.createForm();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.token = params.token;
    });
  }

  save() {
    if (this.form.invalid) {
      return;
    }

    const uuid = new DeviceUUID().get();
    this.load = true;
    this.form.disable();

    this.userService.signUpQR({
      uuid,
      secret: this.token,
      ...this.form.value
    }).subscribe(_ => {
      this.load = false;
      this.form.enable();
      this.onVerifyIdentity();
    }, ({ error }) => {
      this.load = false;
      this.form.enable();

      if (error.developMessage === 'duplicate') {
        this.form.get('email').setErrors({ duplicate: 'error' });
      } else {
        this.expired.emit();
      }
    });
  }

  private onVerifyIdentity() {
    this.dialog.open(
      DialogValidateIdentityComponent,
      {
        maxWidth: '95%',
        maxHeight: '95%',
        width: '90%',
        disableClose: true,
        data: this.token
      }
    ).beforeClosed().subscribe(resp => {
      if (resp) {
        this.code.emit(resp);
      } else {
        this.expired.emit();
      }
    });
  }

  private createForm(): void {
    this.form = this.formBuilder.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      motherSurname: [''],
      email: ['', Validators.required]
    });
  }

}

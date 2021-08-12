import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../core/http/user.service';
import { DeviceUUID } from 'device-uuid';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-form-user',
  templateUrl: './form-user.component.html',
  styleUrls: ['./form-user.component.scss']
})
export class FormUserComponent implements OnInit {

  form: FormGroup;
  token: string;

  @Output() code: EventEmitter<string>;
  @Output() expired: EventEmitter<void>;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private route: ActivatedRoute
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

    this.userService.signUpQR({
      uuid,
      secret: this.token,
      ...this.form.value
    }).subscribe(resp => {
      this.code.emit(resp.code);
      console.log(resp);
    }, error => {
      this.expired.emit();
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

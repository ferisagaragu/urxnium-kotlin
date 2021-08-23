import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../../core/http/user.service';
import { DeviceUUID } from 'device-uuid';

@Component({
  selector: 'app-recover-account',
  templateUrl: './recover-account.component.html',
  styleUrls: ['./recover-account.component.scss']
})
export class RecoverAccountComponent implements OnInit {

  code: string;
  error: boolean;
  load: boolean;

  constructor(
    private activatedRoute: ActivatedRoute,
    private userService: UserService
  ) {
    this.code = '';
    this.error = false;
    this.load = false;
  }

  ngOnInit(): void {
    const uuid = new DeviceUUID().get();
    this.load = true;

    this.activatedRoute.params.subscribe(params => {
      this.userService.changeQRDevice({
        activatePassword: params.activatePassword,
        deviceUuid: uuid
      }).subscribe(resp => {
        this.code = resp;
        this.load = false;
      }, _ => {
        this.error = true;
        this.load = false;
      });
    });
  }

}

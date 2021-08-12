import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../core/http/user.service';
import { DeviceUUID } from 'device-uuid';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-view',
  templateUrl: './view.component.html',
  styleUrls: ['./view.component.scss']
})
export class ViewComponent {

  user: any;
  load: boolean;
  expired: boolean;
  code: string;

  constructor(
    private userService: UserService,
    private route: ActivatedRoute
  ) {
    const uuid = new DeviceUUID().get();
    this.load = true;

    this.userService.findBasicUserInfoByUuid(uuid).subscribe(resp => {
      this.user = resp;
      this.load = false;

      if (this.user) {
        this.route.params.subscribe(params => {
          this.userService.signUpQR({
            uuid,
            secret: params.token
          }).subscribe(resp => {
            this.code = resp.code;
          }, error => {
            this.expired = true;
          });
        });
      }
    });
  }

  onCode(event: string) {
    this.code = event;
    this.user = {};
  }

  onExpired() {
    this.expired = true;
  }

}

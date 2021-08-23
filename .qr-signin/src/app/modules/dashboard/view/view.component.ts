import { Component } from '@angular/core';
import { UserService } from '../../../core/http/user.service';
import { DeviceUUID } from 'device-uuid';
import { ActivatedRoute } from '@angular/router';
import { DialogValidateIdentityComponent } from '../dialog-validate-identity/dialog-validate-identity.component';
import { MatDialog } from '@angular/material/dialog';

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
    private route: ActivatedRoute,
    private dialog: MatDialog
  ) {
    const uuid = new DeviceUUID().get();
    this.load = true;

    this.userService.findBasicUserInfoByUuid(uuid).subscribe(resp => {
      this.user = resp;
      this.load = false;

      if (this.user) {
        this.load = true;

        if (!this.user.active) {
          this.openVerifyIdentity();
        } else {
          this.getCode(uuid);
        }
      }
    });
  }

  onCode(event: string) {
    this.code = event;
    this.user = {};
  }

  onExpired(): void {
    this.expired = true;
  }

  private openVerifyIdentity(): void {
    this.route.params.subscribe(params => {
      this.dialog.open(
        DialogValidateIdentityComponent,
        {
          maxWidth: '95%',
          maxHeight: '95%',
          width: '90%',
          disableClose: true,
          data: params.token
        }
      ).beforeClosed().subscribe(resp => {
        this.load = false;

        if (resp) {
          this.onCode(resp);
        } else {
          this.onExpired();
        }
      });
    });
  }

  private getCode(uuid: string): void {
    this.route.params.subscribe(params => {
      this.userService.signUpQR({
        uuid,
        secret: params.token
      }).subscribe(resp => {
        this.code = resp.code;
        this.load = false;
      }, error => {
        this.expired = true;
        this.load = false;
      });
    });
  }

}

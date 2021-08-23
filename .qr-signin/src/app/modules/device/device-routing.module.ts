import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FormChangeDeviceComponent } from './form-change-device/form-change-device.component';
import { RecoverAccountComponent } from './recover-account/recover-account.component';

const routes: Routes = [
  {
    path: 'change-email',
    component: FormChangeDeviceComponent
  },{
    path: 'recover/:activatePassword',
    component: RecoverAccountComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DeviceRoutingModule { }

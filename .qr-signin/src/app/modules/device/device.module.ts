import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule } from '@angular/forms';

import { DeviceRoutingModule } from './device-routing.module';
import { FormChangeDeviceComponent } from './form-change-device/form-change-device.component';
import { RecoverAccountComponent } from './recover-account/recover-account.component';

@NgModule({
  declarations: [
    FormChangeDeviceComponent,
    RecoverAccountComponent
  ],
  imports: [
    CommonModule,
    DeviceRoutingModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    ReactiveFormsModule
  ]
})
export class DeviceModule { }

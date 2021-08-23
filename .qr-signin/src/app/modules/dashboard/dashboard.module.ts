import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { ReactiveFormsModule } from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialogModule } from '@angular/material/dialog';

import { UrxFormatModule } from 'ng-urxnium';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { FormUserComponent } from './form-user/form-user.component';
import { ViewComponent } from './view/view.component';
import { DialogValidateIdentityComponent } from './dialog-validate-identity/dialog-validate-identity.component';

@NgModule({
  declarations: [FormUserComponent, ViewComponent, DialogValidateIdentityComponent],
  imports: [
    CommonModule,
    DashboardRoutingModule,
    ReactiveFormsModule,
    MatExpansionModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatDialogModule,
    UrxFormatModule
  ]
})
export class DashboardModule { }

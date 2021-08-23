import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormChangeDeviceComponent } from './form-change-device.component';

describe('FormChangeDeviceComponent', () => {
  let component: FormChangeDeviceComponent;
  let fixture: ComponentFixture<FormChangeDeviceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FormChangeDeviceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FormChangeDeviceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

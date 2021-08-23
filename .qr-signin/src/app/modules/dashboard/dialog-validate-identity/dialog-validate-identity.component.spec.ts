import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogValidateIdentityComponent } from './dialog-validate-identity.component';

describe('DialogValidateIdentityComponent', () => {
  let component: DialogValidateIdentityComponent;
  let fixture: ComponentFixture<DialogValidateIdentityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogValidateIdentityComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogValidateIdentityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

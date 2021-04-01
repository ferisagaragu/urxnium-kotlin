import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgUrxniumComponent } from './ng-urxnium.component';

describe('NgUrxniumComponent', () => {
  let component: NgUrxniumComponent;
  let fixture: ComponentFixture<NgUrxniumComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NgUrxniumComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NgUrxniumComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

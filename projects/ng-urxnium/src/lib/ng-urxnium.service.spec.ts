import { TestBed } from '@angular/core/testing';

import { NgUrxniumService } from './ng-urxnium.service';

describe('NgUrxniumService', () => {
  let service: NgUrxniumService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NgUrxniumService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

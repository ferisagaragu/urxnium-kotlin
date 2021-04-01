import { TestBed } from '@angular/core/testing';

import { SweetAlert2Service } from './sweet-alert-2.service';

describe('SweetAlert2Service', () => {
  let service: SweetAlert2Service;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SweetAlert2Service);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

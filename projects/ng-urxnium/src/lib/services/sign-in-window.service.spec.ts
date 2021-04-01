import { TestBed } from '@angular/core/testing';

import { SignInWindowService } from './sign-in-window.service';

describe('SignInWindowService', () => {
  let service: SignInWindowService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SignInWindowService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

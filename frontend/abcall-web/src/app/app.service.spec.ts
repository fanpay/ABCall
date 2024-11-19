/* tslint:disable:no-unused-variable */

import { TestBed, inject } from '@angular/core/testing';
import { AppService } from './app.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('Service: App', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AppService]
    });
  });

  it('should ...', inject([AppService], (service: AppService) => {
    expect(service).toBeTruthy();
  }));
});

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  findBasicUserInfoByUuid(userUuid: string): Observable<any> {
    return this.http.get(`http://localhost:5000/rest/auth/find-basic-user-info-by-uuid/${userUuid}`)
      .pipe(map((resp: any) => resp.data));
  }

  signUpQR(userData: any): Observable<any> {
    return this.http.post(
      `http://localhost:5000/rest/auth/sign-up-qr`,
      userData
    ).pipe(map((resp: any) => resp.data));
  }

}

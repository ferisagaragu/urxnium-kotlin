import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, Subscriber } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate {

  private user: any;
  private token: string;
  private refreshToken: string;

  constructor(private http: HttpClient) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    this.user = sessionStorage.getItem('user');
    this.token = sessionStorage.getItem('token');
    this.refreshToken = sessionStorage.getItem('refreshToken');

    return new Observable<boolean>(observable => {
        console.log('aqui va toda la programacion externa');
    });
  }

  private containsRefreshTokenUrl(
    route: ActivatedRouteSnapshot,
    observable: Subscriber<boolean>
  ): boolean {
    if (route.data.validateTokenUrl) {
      return true;
    }

    observable.next(false);
    return false;
  }

  private checkSessionData(observable: Subscriber<boolean>): void {
    if (this.user && this.refreshToken && this.token) {
      this.http.get('valid token').subscribe(
        _ => {
          //proceso de hasta cuando
          observable.next(true);
        }, _ => {
          //refresh token
          this.http.get('').subscribe(
            _ => {
              this.refreshTokenAtClock();
              observable.next(true);
            }, _ => {
              //close session
              sessionStorage.clear();
              observable.next(false);
            }
          )
        }
      );
    } else {
      observable.next(false);
    }
  }

  private refreshTokenAtClock(): void {
    //aqui va el timer para refrescar el token
  }

}

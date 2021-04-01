import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import * as i0 from "@angular/core";
export declare class AuthenticationGuard implements CanActivate {
    private http;
    private user;
    private token;
    private refreshToken;
    constructor(http: HttpClient);
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>;
    private containsRefreshTokenUrl;
    private checkSessionData;
    private refreshTokenAtClock;
    static ɵfac: i0.ɵɵFactoryDef<AuthenticationGuard, never>;
    static ɵprov: i0.ɵɵInjectableDef<AuthenticationGuard>;
}
//# sourceMappingURL=authentication.guard.d.ts.map
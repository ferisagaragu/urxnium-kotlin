import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Session } from '../interfaces/session';
import * as i0 from "@angular/core";
export declare class SessionService {
    private http;
    private baseUrl;
    onSignIn: BehaviorSubject<boolean>;
    private count;
    private firstSignIn;
    private user;
    private timeOut;
    constructor(http: HttpClient, baseUrl: string);
    signIn(session: Session, user: any): void;
    signOut(): void;
    checkSession(): Observable<boolean>;
    getUser(): any;
    private validateToken;
    private refreshToken;
    private refreshSession;
    private setSignIn;
    private setSignOut;
    private get headers();
    static ɵfac: i0.ɵɵFactoryDef<SessionService, never>;
    static ɵprov: i0.ɵɵInjectableDef<SessionService>;
}
//# sourceMappingURL=session.service.d.ts.map
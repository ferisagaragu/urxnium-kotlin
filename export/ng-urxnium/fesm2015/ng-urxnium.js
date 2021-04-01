import { HttpHeaders, HttpClient } from '@angular/common/http';
import { ɵɵdefineInjectable, ɵsetClassMetadata, Injectable, ɵɵinject, Inject, ɵɵdefineComponent, ɵɵelementStart, ɵɵtext, ɵɵelementEnd, Component, ɵɵdefineNgModule, ɵɵdefineInjector, ɵɵsetNgModuleScope, NgModule } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import Swal from 'sweetalert2';
import { map } from 'rxjs/operators';

class HttpService {
    get headers() {
        return new HttpHeaders().set('Authorization', `Bearer ${this.getToken()}`);
    }
    getToken() {
        return localStorage.getItem('token');
    }
}

class SignInWindowService {
    open(url, event) {
        return new Observable(observable => {
            let i = 0;
            const dialog = window.open(url, 'targetWindow', `toolbar=no,
        location=center,
        status=no,
        menubar=no,
        scrollbars=no,
        resizable=no,
        width=400,
        height=600,
        ${event ? `screenX=${event.screenX}` : ''},
        ${event ? `screenY=${event.screenY}` : ''}`);
            const interval = setInterval(() => {
                try {
                    const href = dialog.location.href;
                    if (href !== undefined && href.toString().includes('http')) {
                        dialog.close();
                        clearInterval(interval);
                        observable.next(href.split('?code=')[1]);
                    }
                }
                catch (e) { }
                if (i === 10) {
                    clearInterval(interval);
                    observable.error();
                }
                i++;
            }, 1000);
        });
    }
}
SignInWindowService.ɵfac = function SignInWindowService_Factory(t) { return new (t || SignInWindowService)(); };
SignInWindowService.ɵprov = ɵɵdefineInjectable({ token: SignInWindowService, factory: SignInWindowService.ɵfac, providedIn: 'root' });
/*@__PURE__*/ (function () { ɵsetClassMetadata(SignInWindowService, [{
        type: Injectable,
        args: [{
                providedIn: 'root'
            }]
    }], null, null); })();

class SweetAlert2Service {
    fire(message) {
        let observerNext = null;
        const { confirmButtonColor, cancelButtonColor } = message;
        Swal.fire(Object.assign(Object.assign({}, message), { allowOutsideClick: false, confirmButtonColor: confirmButtonColor ? confirmButtonColor : '#00d1b2', cancelButtonColor: cancelButtonColor ? cancelButtonColor : '#F44336', reverseButtons: true, closeButtonHtml: `<button class="mat-focus-indicator mr-2 mat-stroked-button mat-button-base mat-accent">Cancelar</button>` })).then(resp => {
            if (observerNext) {
                observerNext.next(resp.isConfirmed);
            }
        });
        return new Observable(observer => observerNext = observer);
    }
}
SweetAlert2Service.ɵfac = function SweetAlert2Service_Factory(t) { return new (t || SweetAlert2Service)(); };
SweetAlert2Service.ɵprov = ɵɵdefineInjectable({ token: SweetAlert2Service, factory: SweetAlert2Service.ɵfac, providedIn: 'root' });
/*@__PURE__*/ (function () { ɵsetClassMetadata(SweetAlert2Service, [{
        type: Injectable,
        args: [{
                providedIn: 'root'
            }]
    }], null, null); })();

class SessionService {
    constructor(http, baseUrl) {
        this.http = http;
        this.baseUrl = baseUrl;
        this.onSignIn = new BehaviorSubject(false);
        this.count = true;
        this.firstSignIn = false;
        this.user = JSON.parse(localStorage.getItem('user'));
    }
    signIn(session, user) {
        localStorage.setItem('token', session.token);
        localStorage.setItem('expiration', session.expiration.toString());
        localStorage.setItem('expirationDate', session.expirationDate.toString());
        localStorage.setItem('refreshToken', session.refreshToken);
        localStorage.setItem('user', JSON.stringify(user));
        this.user = user;
        this.onSignIn.next(true);
        this.firstSignIn = true;
    }
    signOut() {
        localStorage.clear();
        this.onSignIn.next(false);
        this.firstSignIn = false;
        clearTimeout(this.timeOut);
    }
    checkSession() {
        return new Observable(observable => {
            if (this.firstSignIn) {
                this.setSignIn(null, observable);
            }
            else if (localStorage.getItem('token') &&
                localStorage.getItem('expiration') &&
                localStorage.getItem('expirationDate') &&
                localStorage.getItem('refreshToken') &&
                localStorage.getItem('user') &&
                !this.firstSignIn) {
                this.validateToken().subscribe(session => {
                    this.setSignIn(session, observable);
                }, _ => {
                    this.refreshToken().subscribe(session => {
                        this.setSignIn(session, observable);
                    }, _ => {
                        this.setSignOut(observable);
                    });
                });
            }
            else {
                this.setSignOut(observable);
            }
        });
    }
    getUser() {
        return this.user;
    }
    validateToken() {
        return this.http.get(`${this.baseUrl}/auth/validate-token`, { headers: this.headers }).pipe(map((resp) => {
            const session = resp.data.session;
            delete resp.data.session;
            this.user = resp.data;
            localStorage.setItem('user', JSON.stringify(resp.data));
            return session;
        }));
    }
    refreshToken() {
        return this.http.post(`${this.baseUrl}/auth/refresh-token`, { refreshToken: localStorage.getItem('refreshToken') }).pipe(map((resp) => resp.data));
    }
    refreshSession() {
        if (this.count) {
            this.count = false;
            this.timeOut = setTimeout(() => {
                this.refreshToken().subscribe(session => {
                    localStorage.setItem('token', session.token);
                    localStorage.setItem('expiration', session.expiration.toString());
                    localStorage.setItem('expirationDate', session.expirationDate.toString());
                    this.count = true;
                    this.refreshSession();
                }, _ => {
                    this.signOut();
                });
            }, localStorage.getItem('expiration'));
        }
    }
    setSignIn(session, observable) {
        if (session) {
            localStorage.setItem('token', session.token);
            localStorage.setItem('expiration', session.expiration.toString());
            localStorage.setItem('expirationDate', session.expirationDate.toString());
        }
        this.refreshSession();
        this.onSignIn.next(true);
        observable.next(true);
    }
    setSignOut(observable) {
        this.signOut();
        observable.next(false);
    }
    get headers() {
        return new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('token')}`);
    }
}
SessionService.ɵfac = function SessionService_Factory(t) { return new (t || SessionService)(ɵɵinject(HttpClient), ɵɵinject('baseUrl')); };
SessionService.ɵprov = ɵɵdefineInjectable({ token: SessionService, factory: SessionService.ɵfac, providedIn: 'root' });
/*@__PURE__*/ (function () { ɵsetClassMetadata(SessionService, [{
        type: Injectable,
        args: [{
                providedIn: 'root'
            }]
    }], function () { return [{ type: HttpClient }, { type: undefined, decorators: [{
                type: Inject,
                args: ['baseUrl']
            }] }]; }, null); })();

class AuthenticationGuard {
    constructor(http) {
        this.http = http;
    }
    canActivate(route, state) {
        this.user = sessionStorage.getItem('user');
        this.token = sessionStorage.getItem('token');
        this.refreshToken = sessionStorage.getItem('refreshToken');
        return new Observable(observable => {
            console.log('aqui va toda la programacion externa');
        });
    }
    containsRefreshTokenUrl(route, observable) {
        if (route.data.validateTokenUrl) {
            return true;
        }
        observable.next(false);
        return false;
    }
    checkSessionData(observable) {
        if (this.user && this.refreshToken && this.token) {
            this.http.get('valid token').subscribe(_ => {
                //proceso de hasta cuando
                observable.next(true);
            }, _ => {
                //refresh token
                this.http.get('').subscribe(_ => {
                    this.refreshTokenAtClock();
                    observable.next(true);
                }, _ => {
                    //close session
                    sessionStorage.clear();
                    observable.next(false);
                });
            });
        }
        else {
            observable.next(false);
        }
    }
    refreshTokenAtClock() {
        //aqui va el timer para refrescar el token
    }
}
AuthenticationGuard.ɵfac = function AuthenticationGuard_Factory(t) { return new (t || AuthenticationGuard)(ɵɵinject(HttpClient)); };
AuthenticationGuard.ɵprov = ɵɵdefineInjectable({ token: AuthenticationGuard, factory: AuthenticationGuard.ɵfac, providedIn: 'root' });
/*@__PURE__*/ (function () { ɵsetClassMetadata(AuthenticationGuard, [{
        type: Injectable,
        args: [{
                providedIn: 'root'
            }]
    }], function () { return [{ type: HttpClient }]; }, null); })();

class NgUrxniumService {
    constructor() { }
}
NgUrxniumService.ɵfac = function NgUrxniumService_Factory(t) { return new (t || NgUrxniumService)(); };
NgUrxniumService.ɵprov = ɵɵdefineInjectable({ token: NgUrxniumService, factory: NgUrxniumService.ɵfac, providedIn: 'root' });
/*@__PURE__*/ (function () { ɵsetClassMetadata(NgUrxniumService, [{
        type: Injectable,
        args: [{
                providedIn: 'root'
            }]
    }], function () { return []; }, null); })();

class NgUrxniumComponent {
    constructor() { }
    ngOnInit() {
    }
}
NgUrxniumComponent.ɵfac = function NgUrxniumComponent_Factory(t) { return new (t || NgUrxniumComponent)(); };
NgUrxniumComponent.ɵcmp = ɵɵdefineComponent({ type: NgUrxniumComponent, selectors: [["lib-ng-urxnium"]], decls: 2, vars: 0, template: function NgUrxniumComponent_Template(rf, ctx) { if (rf & 1) {
        ɵɵelementStart(0, "p");
        ɵɵtext(1, " ng-urxnium works! ");
        ɵɵelementEnd();
    } }, encapsulation: 2 });
/*@__PURE__*/ (function () { ɵsetClassMetadata(NgUrxniumComponent, [{
        type: Component,
        args: [{
                selector: 'lib-ng-urxnium',
                template: `
    <p>
      ng-urxnium works!
    </p>
  `,
                styles: []
            }]
    }], function () { return []; }, null); })();

class NgUrxniumModule {
}
NgUrxniumModule.ɵmod = ɵɵdefineNgModule({ type: NgUrxniumModule });
NgUrxniumModule.ɵinj = ɵɵdefineInjector({ factory: function NgUrxniumModule_Factory(t) { return new (t || NgUrxniumModule)(); }, imports: [[]] });
(function () { (typeof ngJitMode === "undefined" || ngJitMode) && ɵɵsetNgModuleScope(NgUrxniumModule, { declarations: [NgUrxniumComponent], exports: [NgUrxniumComponent] }); })();
/*@__PURE__*/ (function () { ɵsetClassMetadata(NgUrxniumModule, [{
        type: NgModule,
        args: [{
                declarations: [NgUrxniumComponent],
                imports: [],
                exports: [NgUrxniumComponent]
            }]
    }], null, null); })();

/*
 * Public API Surface of ng-urxnium
 */

/**
 * Generated bundle index. Do not edit.
 */

export { AuthenticationGuard, HttpService, NgUrxniumComponent, NgUrxniumModule, NgUrxniumService, SessionService, SignInWindowService, SweetAlert2Service };
//# sourceMappingURL=ng-urxnium.js.map

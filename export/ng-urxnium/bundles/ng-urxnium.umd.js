(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports, require('@angular/common/http'), require('@angular/core'), require('rxjs'), require('sweetalert2'), require('rxjs/operators')) :
    typeof define === 'function' && define.amd ? define('ng-urxnium', ['exports', '@angular/common/http', '@angular/core', 'rxjs', 'sweetalert2', 'rxjs/operators'], factory) :
    (global = typeof globalThis !== 'undefined' ? globalThis : global || self, factory(global['ng-urxnium'] = {}, global.ng.common.http, global.ng.core, global.rxjs, global.Swal, global.rxjs.operators));
}(this, (function (exports, i1, i0, rxjs, Swal, operators) { 'use strict';

    function _interopDefaultLegacy (e) { return e && typeof e === 'object' && 'default' in e ? e : { 'default': e }; }

    var Swal__default = /*#__PURE__*/_interopDefaultLegacy(Swal);

    var HttpService = /** @class */ (function () {
        function HttpService() {
        }
        Object.defineProperty(HttpService.prototype, "headers", {
            get: function () {
                return new i1.HttpHeaders().set('Authorization', "Bearer " + this.getToken());
            },
            enumerable: false,
            configurable: true
        });
        HttpService.prototype.getToken = function () {
            return localStorage.getItem('token');
        };
        return HttpService;
    }());

    var SignInWindowService = /** @class */ (function () {
        function SignInWindowService() {
        }
        SignInWindowService.prototype.open = function (url, event) {
            return new rxjs.Observable(function (observable) {
                var i = 0;
                var dialog = window.open(url, 'targetWindow', "toolbar=no,\n        location=center,\n        status=no,\n        menubar=no,\n        scrollbars=no,\n        resizable=no,\n        width=400,\n        height=600,\n        " + (event ? "screenX=" + event.screenX : '') + ",\n        " + (event ? "screenY=" + event.screenY : ''));
                var interval = setInterval(function () {
                    try {
                        var href = dialog.location.href;
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
        };
        return SignInWindowService;
    }());
    SignInWindowService.ɵfac = function SignInWindowService_Factory(t) { return new (t || SignInWindowService)(); };
    SignInWindowService.ɵprov = i0.ɵɵdefineInjectable({ token: SignInWindowService, factory: SignInWindowService.ɵfac, providedIn: 'root' });
    /*@__PURE__*/ (function () {
        i0.ɵsetClassMetadata(SignInWindowService, [{
                type: i0.Injectable,
                args: [{
                        providedIn: 'root'
                    }]
            }], null, null);
    })();

    var SweetAlert2Service = /** @class */ (function () {
        function SweetAlert2Service() {
        }
        SweetAlert2Service.prototype.fire = function (message) {
            var observerNext = null;
            var confirmButtonColor = message.confirmButtonColor, cancelButtonColor = message.cancelButtonColor;
            Swal__default['default'].fire(Object.assign(Object.assign({}, message), { allowOutsideClick: false, confirmButtonColor: confirmButtonColor ? confirmButtonColor : '#00d1b2', cancelButtonColor: cancelButtonColor ? cancelButtonColor : '#F44336', reverseButtons: true, closeButtonHtml: "<button class=\"mat-focus-indicator mr-2 mat-stroked-button mat-button-base mat-accent\">Cancelar</button>" })).then(function (resp) {
                if (observerNext) {
                    observerNext.next(resp.isConfirmed);
                }
            });
            return new rxjs.Observable(function (observer) { return observerNext = observer; });
        };
        return SweetAlert2Service;
    }());
    SweetAlert2Service.ɵfac = function SweetAlert2Service_Factory(t) { return new (t || SweetAlert2Service)(); };
    SweetAlert2Service.ɵprov = i0.ɵɵdefineInjectable({ token: SweetAlert2Service, factory: SweetAlert2Service.ɵfac, providedIn: 'root' });
    /*@__PURE__*/ (function () {
        i0.ɵsetClassMetadata(SweetAlert2Service, [{
                type: i0.Injectable,
                args: [{
                        providedIn: 'root'
                    }]
            }], null, null);
    })();

    var SessionService = /** @class */ (function () {
        function SessionService(http, baseUrl) {
            this.http = http;
            this.baseUrl = baseUrl;
            this.onSignIn = new rxjs.BehaviorSubject(false);
            this.count = true;
            this.firstSignIn = false;
            this.user = JSON.parse(localStorage.getItem('user'));
        }
        SessionService.prototype.signIn = function (session, user) {
            localStorage.setItem('token', session.token);
            localStorage.setItem('expiration', session.expiration.toString());
            localStorage.setItem('expirationDate', session.expirationDate.toString());
            localStorage.setItem('refreshToken', session.refreshToken);
            localStorage.setItem('user', JSON.stringify(user));
            this.user = user;
            this.onSignIn.next(true);
            this.firstSignIn = true;
        };
        SessionService.prototype.signOut = function () {
            localStorage.clear();
            this.onSignIn.next(false);
            this.firstSignIn = false;
            clearTimeout(this.timeOut);
        };
        SessionService.prototype.checkSession = function () {
            var _this = this;
            return new rxjs.Observable(function (observable) {
                if (_this.firstSignIn) {
                    _this.setSignIn(null, observable);
                }
                else if (localStorage.getItem('token') &&
                    localStorage.getItem('expiration') &&
                    localStorage.getItem('expirationDate') &&
                    localStorage.getItem('refreshToken') &&
                    localStorage.getItem('user') &&
                    !_this.firstSignIn) {
                    _this.validateToken().subscribe(function (session) {
                        _this.setSignIn(session, observable);
                    }, function (_) {
                        _this.refreshToken().subscribe(function (session) {
                            _this.setSignIn(session, observable);
                        }, function (_) {
                            _this.setSignOut(observable);
                        });
                    });
                }
                else {
                    _this.setSignOut(observable);
                }
            });
        };
        SessionService.prototype.getUser = function () {
            return this.user;
        };
        SessionService.prototype.validateToken = function () {
            var _this = this;
            return this.http.get(this.baseUrl + "/auth/validate-token", { headers: this.headers }).pipe(operators.map(function (resp) {
                var session = resp.data.session;
                delete resp.data.session;
                _this.user = resp.data;
                localStorage.setItem('user', JSON.stringify(resp.data));
                return session;
            }));
        };
        SessionService.prototype.refreshToken = function () {
            return this.http.post(this.baseUrl + "/auth/refresh-token", { refreshToken: localStorage.getItem('refreshToken') }).pipe(operators.map(function (resp) { return resp.data; }));
        };
        SessionService.prototype.refreshSession = function () {
            var _this = this;
            if (this.count) {
                this.count = false;
                this.timeOut = setTimeout(function () {
                    _this.refreshToken().subscribe(function (session) {
                        localStorage.setItem('token', session.token);
                        localStorage.setItem('expiration', session.expiration.toString());
                        localStorage.setItem('expirationDate', session.expirationDate.toString());
                        _this.count = true;
                        _this.refreshSession();
                    }, function (_) {
                        _this.signOut();
                    });
                }, localStorage.getItem('expiration'));
            }
        };
        SessionService.prototype.setSignIn = function (session, observable) {
            if (session) {
                localStorage.setItem('token', session.token);
                localStorage.setItem('expiration', session.expiration.toString());
                localStorage.setItem('expirationDate', session.expirationDate.toString());
            }
            this.refreshSession();
            this.onSignIn.next(true);
            observable.next(true);
        };
        SessionService.prototype.setSignOut = function (observable) {
            this.signOut();
            observable.next(false);
        };
        Object.defineProperty(SessionService.prototype, "headers", {
            get: function () {
                return new i1.HttpHeaders().set('Authorization', "Bearer " + localStorage.getItem('token'));
            },
            enumerable: false,
            configurable: true
        });
        return SessionService;
    }());
    SessionService.ɵfac = function SessionService_Factory(t) { return new (t || SessionService)(i0.ɵɵinject(i1.HttpClient), i0.ɵɵinject('baseUrl')); };
    SessionService.ɵprov = i0.ɵɵdefineInjectable({ token: SessionService, factory: SessionService.ɵfac, providedIn: 'root' });
    /*@__PURE__*/ (function () {
        i0.ɵsetClassMetadata(SessionService, [{
                type: i0.Injectable,
                args: [{
                        providedIn: 'root'
                    }]
            }], function () {
            return [{ type: i1.HttpClient }, { type: undefined, decorators: [{
                            type: i0.Inject,
                            args: ['baseUrl']
                        }] }];
        }, null);
    })();

    var AuthenticationGuard = /** @class */ (function () {
        function AuthenticationGuard(http) {
            this.http = http;
        }
        AuthenticationGuard.prototype.canActivate = function (route, state) {
            this.user = sessionStorage.getItem('user');
            this.token = sessionStorage.getItem('token');
            this.refreshToken = sessionStorage.getItem('refreshToken');
            return new rxjs.Observable(function (observable) {
                console.log('aqui va toda la programacion externa');
            });
        };
        AuthenticationGuard.prototype.containsRefreshTokenUrl = function (route, observable) {
            if (route.data.validateTokenUrl) {
                return true;
            }
            observable.next(false);
            return false;
        };
        AuthenticationGuard.prototype.checkSessionData = function (observable) {
            var _this = this;
            if (this.user && this.refreshToken && this.token) {
                this.http.get('valid token').subscribe(function (_) {
                    //proceso de hasta cuando
                    observable.next(true);
                }, function (_) {
                    //refresh token
                    _this.http.get('').subscribe(function (_) {
                        _this.refreshTokenAtClock();
                        observable.next(true);
                    }, function (_) {
                        //close session
                        sessionStorage.clear();
                        observable.next(false);
                    });
                });
            }
            else {
                observable.next(false);
            }
        };
        AuthenticationGuard.prototype.refreshTokenAtClock = function () {
            //aqui va el timer para refrescar el token
        };
        return AuthenticationGuard;
    }());
    AuthenticationGuard.ɵfac = function AuthenticationGuard_Factory(t) { return new (t || AuthenticationGuard)(i0.ɵɵinject(i1.HttpClient)); };
    AuthenticationGuard.ɵprov = i0.ɵɵdefineInjectable({ token: AuthenticationGuard, factory: AuthenticationGuard.ɵfac, providedIn: 'root' });
    /*@__PURE__*/ (function () {
        i0.ɵsetClassMetadata(AuthenticationGuard, [{
                type: i0.Injectable,
                args: [{
                        providedIn: 'root'
                    }]
            }], function () { return [{ type: i1.HttpClient }]; }, null);
    })();

    var NgUrxniumService = /** @class */ (function () {
        function NgUrxniumService() {
        }
        return NgUrxniumService;
    }());
    NgUrxniumService.ɵfac = function NgUrxniumService_Factory(t) { return new (t || NgUrxniumService)(); };
    NgUrxniumService.ɵprov = i0.ɵɵdefineInjectable({ token: NgUrxniumService, factory: NgUrxniumService.ɵfac, providedIn: 'root' });
    /*@__PURE__*/ (function () {
        i0.ɵsetClassMetadata(NgUrxniumService, [{
                type: i0.Injectable,
                args: [{
                        providedIn: 'root'
                    }]
            }], function () { return []; }, null);
    })();

    var NgUrxniumComponent = /** @class */ (function () {
        function NgUrxniumComponent() {
        }
        NgUrxniumComponent.prototype.ngOnInit = function () {
        };
        return NgUrxniumComponent;
    }());
    NgUrxniumComponent.ɵfac = function NgUrxniumComponent_Factory(t) { return new (t || NgUrxniumComponent)(); };
    NgUrxniumComponent.ɵcmp = i0.ɵɵdefineComponent({ type: NgUrxniumComponent, selectors: [["lib-ng-urxnium"]], decls: 2, vars: 0, template: function NgUrxniumComponent_Template(rf, ctx) {
            if (rf & 1) {
                i0.ɵɵelementStart(0, "p");
                i0.ɵɵtext(1, " ng-urxnium works! ");
                i0.ɵɵelementEnd();
            }
        }, encapsulation: 2 });
    /*@__PURE__*/ (function () {
        i0.ɵsetClassMetadata(NgUrxniumComponent, [{
                type: i0.Component,
                args: [{
                        selector: 'lib-ng-urxnium',
                        template: "\n    <p>\n      ng-urxnium works!\n    </p>\n  ",
                        styles: []
                    }]
            }], function () { return []; }, null);
    })();

    var NgUrxniumModule = /** @class */ (function () {
        function NgUrxniumModule() {
        }
        return NgUrxniumModule;
    }());
    NgUrxniumModule.ɵmod = i0.ɵɵdefineNgModule({ type: NgUrxniumModule });
    NgUrxniumModule.ɵinj = i0.ɵɵdefineInjector({ factory: function NgUrxniumModule_Factory(t) { return new (t || NgUrxniumModule)(); }, imports: [[]] });
    (function () { (typeof ngJitMode === "undefined" || ngJitMode) && i0.ɵɵsetNgModuleScope(NgUrxniumModule, { declarations: [NgUrxniumComponent], exports: [NgUrxniumComponent] }); })();
    /*@__PURE__*/ (function () {
        i0.ɵsetClassMetadata(NgUrxniumModule, [{
                type: i0.NgModule,
                args: [{
                        declarations: [NgUrxniumComponent],
                        imports: [],
                        exports: [NgUrxniumComponent]
                    }]
            }], null, null);
    })();

    /*
     * Public API Surface of ng-urxnium
     */

    /**
     * Generated bundle index. Do not edit.
     */

    exports.AuthenticationGuard = AuthenticationGuard;
    exports.HttpService = HttpService;
    exports.NgUrxniumComponent = NgUrxniumComponent;
    exports.NgUrxniumModule = NgUrxniumModule;
    exports.NgUrxniumService = NgUrxniumService;
    exports.SessionService = SessionService;
    exports.SignInWindowService = SignInWindowService;
    exports.SweetAlert2Service = SweetAlert2Service;

    Object.defineProperty(exports, '__esModule', { value: true });

})));
//# sourceMappingURL=ng-urxnium.umd.js.map

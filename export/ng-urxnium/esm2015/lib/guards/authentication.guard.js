import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import * as i0 from "@angular/core";
import * as i1 from "@angular/common/http";
export class AuthenticationGuard {
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
AuthenticationGuard.ɵfac = function AuthenticationGuard_Factory(t) { return new (t || AuthenticationGuard)(i0.ɵɵinject(i1.HttpClient)); };
AuthenticationGuard.ɵprov = i0.ɵɵdefineInjectable({ token: AuthenticationGuard, factory: AuthenticationGuard.ɵfac, providedIn: 'root' });
/*@__PURE__*/ (function () { i0.ɵsetClassMetadata(AuthenticationGuard, [{
        type: Injectable,
        args: [{
                providedIn: 'root'
            }]
    }], function () { return [{ type: i1.HttpClient }]; }, null); })();
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiYXV0aGVudGljYXRpb24uZ3VhcmQuanMiLCJzb3VyY2VSb290IjoiQzovZGV2ZWxvcC9wZWNoYmxlbmRhL3VyeG5pdW0vdXJ4bml1bS1hbmd1bGFyL3Byb2plY3RzL25nLXVyeG5pdW0vc3JjLyIsInNvdXJjZXMiOlsibGliL2d1YXJkcy9hdXRoZW50aWNhdGlvbi5ndWFyZC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDO0FBRTNDLE9BQU8sRUFBRSxVQUFVLEVBQWMsTUFBTSxNQUFNLENBQUM7OztBQU05QyxNQUFNLE9BQU8sbUJBQW1CO0lBTTlCLFlBQW9CLElBQWdCO1FBQWhCLFNBQUksR0FBSixJQUFJLENBQVk7SUFBSSxDQUFDO0lBRXpDLFdBQVcsQ0FDVCxLQUE2QixFQUM3QixLQUEwQjtRQUUxQixJQUFJLENBQUMsSUFBSSxHQUFHLGNBQWMsQ0FBQyxPQUFPLENBQUMsTUFBTSxDQUFDLENBQUM7UUFDM0MsSUFBSSxDQUFDLEtBQUssR0FBRyxjQUFjLENBQUMsT0FBTyxDQUFDLE9BQU8sQ0FBQyxDQUFDO1FBQzdDLElBQUksQ0FBQyxZQUFZLEdBQUcsY0FBYyxDQUFDLE9BQU8sQ0FBQyxjQUFjLENBQUMsQ0FBQztRQUUzRCxPQUFPLElBQUksVUFBVSxDQUFVLFVBQVUsQ0FBQyxFQUFFO1lBQ3hDLE9BQU8sQ0FBQyxHQUFHLENBQUMsc0NBQXNDLENBQUMsQ0FBQztRQUN4RCxDQUFDLENBQUMsQ0FBQztJQUNMLENBQUM7SUFFTyx1QkFBdUIsQ0FDN0IsS0FBNkIsRUFDN0IsVUFBK0I7UUFFL0IsSUFBSSxLQUFLLENBQUMsSUFBSSxDQUFDLGdCQUFnQixFQUFFO1lBQy9CLE9BQU8sSUFBSSxDQUFDO1NBQ2I7UUFFRCxVQUFVLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxDQUFDO1FBQ3ZCLE9BQU8sS0FBSyxDQUFDO0lBQ2YsQ0FBQztJQUVPLGdCQUFnQixDQUFDLFVBQStCO1FBQ3RELElBQUksSUFBSSxDQUFDLElBQUksSUFBSSxJQUFJLENBQUMsWUFBWSxJQUFJLElBQUksQ0FBQyxLQUFLLEVBQUU7WUFDaEQsSUFBSSxDQUFDLElBQUksQ0FBQyxHQUFHLENBQUMsYUFBYSxDQUFDLENBQUMsU0FBUyxDQUNwQyxDQUFDLENBQUMsRUFBRTtnQkFDRix5QkFBeUI7Z0JBQ3pCLFVBQVUsQ0FBQyxJQUFJLENBQUMsSUFBSSxDQUFDLENBQUM7WUFDeEIsQ0FBQyxFQUFFLENBQUMsQ0FBQyxFQUFFO2dCQUNMLGVBQWU7Z0JBQ2YsSUFBSSxDQUFDLElBQUksQ0FBQyxHQUFHLENBQUMsRUFBRSxDQUFDLENBQUMsU0FBUyxDQUN6QixDQUFDLENBQUMsRUFBRTtvQkFDRixJQUFJLENBQUMsbUJBQW1CLEVBQUUsQ0FBQztvQkFDM0IsVUFBVSxDQUFDLElBQUksQ0FBQyxJQUFJLENBQUMsQ0FBQztnQkFDeEIsQ0FBQyxFQUFFLENBQUMsQ0FBQyxFQUFFO29CQUNMLGVBQWU7b0JBQ2YsY0FBYyxDQUFDLEtBQUssRUFBRSxDQUFDO29CQUN2QixVQUFVLENBQUMsSUFBSSxDQUFDLEtBQUssQ0FBQyxDQUFDO2dCQUN6QixDQUFDLENBQ0YsQ0FBQTtZQUNILENBQUMsQ0FDRixDQUFDO1NBQ0g7YUFBTTtZQUNMLFVBQVUsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLENBQUM7U0FDeEI7SUFDSCxDQUFDO0lBRU8sbUJBQW1CO1FBQ3pCLDBDQUEwQztJQUM1QyxDQUFDOztzRkE1RFUsbUJBQW1COzJEQUFuQixtQkFBbUIsV0FBbkIsbUJBQW1CLG1CQUZsQixNQUFNO2tEQUVQLG1CQUFtQjtjQUgvQixVQUFVO2VBQUM7Z0JBQ1YsVUFBVSxFQUFFLE1BQU07YUFDbkIiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBJbmplY3RhYmxlIH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XG5pbXBvcnQgeyBDYW5BY3RpdmF0ZSwgQWN0aXZhdGVkUm91dGVTbmFwc2hvdCwgUm91dGVyU3RhdGVTbmFwc2hvdCwgVXJsVHJlZSB9IGZyb20gJ0Bhbmd1bGFyL3JvdXRlcic7XG5pbXBvcnQgeyBPYnNlcnZhYmxlLCBTdWJzY3JpYmVyIH0gZnJvbSAncnhqcyc7XG5pbXBvcnQgeyBIdHRwQ2xpZW50IH0gZnJvbSAnQGFuZ3VsYXIvY29tbW9uL2h0dHAnO1xuXG5ASW5qZWN0YWJsZSh7XG4gIHByb3ZpZGVkSW46ICdyb290J1xufSlcbmV4cG9ydCBjbGFzcyBBdXRoZW50aWNhdGlvbkd1YXJkIGltcGxlbWVudHMgQ2FuQWN0aXZhdGUge1xuXG4gIHByaXZhdGUgdXNlcjogYW55O1xuICBwcml2YXRlIHRva2VuOiBzdHJpbmc7XG4gIHByaXZhdGUgcmVmcmVzaFRva2VuOiBzdHJpbmc7XG5cbiAgY29uc3RydWN0b3IocHJpdmF0ZSBodHRwOiBIdHRwQ2xpZW50KSB7IH1cblxuICBjYW5BY3RpdmF0ZShcbiAgICByb3V0ZTogQWN0aXZhdGVkUm91dGVTbmFwc2hvdCxcbiAgICBzdGF0ZTogUm91dGVyU3RhdGVTbmFwc2hvdFxuICApOiBPYnNlcnZhYmxlPGJvb2xlYW4+IHtcbiAgICB0aGlzLnVzZXIgPSBzZXNzaW9uU3RvcmFnZS5nZXRJdGVtKCd1c2VyJyk7XG4gICAgdGhpcy50b2tlbiA9IHNlc3Npb25TdG9yYWdlLmdldEl0ZW0oJ3Rva2VuJyk7XG4gICAgdGhpcy5yZWZyZXNoVG9rZW4gPSBzZXNzaW9uU3RvcmFnZS5nZXRJdGVtKCdyZWZyZXNoVG9rZW4nKTtcblxuICAgIHJldHVybiBuZXcgT2JzZXJ2YWJsZTxib29sZWFuPihvYnNlcnZhYmxlID0+IHtcbiAgICAgICAgY29uc29sZS5sb2coJ2FxdWkgdmEgdG9kYSBsYSBwcm9ncmFtYWNpb24gZXh0ZXJuYScpO1xuICAgIH0pO1xuICB9XG5cbiAgcHJpdmF0ZSBjb250YWluc1JlZnJlc2hUb2tlblVybChcbiAgICByb3V0ZTogQWN0aXZhdGVkUm91dGVTbmFwc2hvdCxcbiAgICBvYnNlcnZhYmxlOiBTdWJzY3JpYmVyPGJvb2xlYW4+XG4gICk6IGJvb2xlYW4ge1xuICAgIGlmIChyb3V0ZS5kYXRhLnZhbGlkYXRlVG9rZW5VcmwpIHtcbiAgICAgIHJldHVybiB0cnVlO1xuICAgIH1cblxuICAgIG9ic2VydmFibGUubmV4dChmYWxzZSk7XG4gICAgcmV0dXJuIGZhbHNlO1xuICB9XG5cbiAgcHJpdmF0ZSBjaGVja1Nlc3Npb25EYXRhKG9ic2VydmFibGU6IFN1YnNjcmliZXI8Ym9vbGVhbj4pOiB2b2lkIHtcbiAgICBpZiAodGhpcy51c2VyICYmIHRoaXMucmVmcmVzaFRva2VuICYmIHRoaXMudG9rZW4pIHtcbiAgICAgIHRoaXMuaHR0cC5nZXQoJ3ZhbGlkIHRva2VuJykuc3Vic2NyaWJlKFxuICAgICAgICBfID0+IHtcbiAgICAgICAgICAvL3Byb2Nlc28gZGUgaGFzdGEgY3VhbmRvXG4gICAgICAgICAgb2JzZXJ2YWJsZS5uZXh0KHRydWUpO1xuICAgICAgICB9LCBfID0+IHtcbiAgICAgICAgICAvL3JlZnJlc2ggdG9rZW5cbiAgICAgICAgICB0aGlzLmh0dHAuZ2V0KCcnKS5zdWJzY3JpYmUoXG4gICAgICAgICAgICBfID0+IHtcbiAgICAgICAgICAgICAgdGhpcy5yZWZyZXNoVG9rZW5BdENsb2NrKCk7XG4gICAgICAgICAgICAgIG9ic2VydmFibGUubmV4dCh0cnVlKTtcbiAgICAgICAgICAgIH0sIF8gPT4ge1xuICAgICAgICAgICAgICAvL2Nsb3NlIHNlc3Npb25cbiAgICAgICAgICAgICAgc2Vzc2lvblN0b3JhZ2UuY2xlYXIoKTtcbiAgICAgICAgICAgICAgb2JzZXJ2YWJsZS5uZXh0KGZhbHNlKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICApXG4gICAgICAgIH1cbiAgICAgICk7XG4gICAgfSBlbHNlIHtcbiAgICAgIG9ic2VydmFibGUubmV4dChmYWxzZSk7XG4gICAgfVxuICB9XG5cbiAgcHJpdmF0ZSByZWZyZXNoVG9rZW5BdENsb2NrKCk6IHZvaWQge1xuICAgIC8vYXF1aSB2YSBlbCB0aW1lciBwYXJhIHJlZnJlc2NhciBlbCB0b2tlblxuICB9XG5cbn1cbiJdfQ==
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import * as i0 from "@angular/core";
export class SignInWindowService {
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
SignInWindowService.ɵprov = i0.ɵɵdefineInjectable({ token: SignInWindowService, factory: SignInWindowService.ɵfac, providedIn: 'root' });
/*@__PURE__*/ (function () { i0.ɵsetClassMetadata(SignInWindowService, [{
        type: Injectable,
        args: [{
                providedIn: 'root'
            }]
    }], null, null); })();
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoic2lnbi1pbi13aW5kb3cuc2VydmljZS5qcyIsInNvdXJjZVJvb3QiOiJDOi9kZXZlbG9wL3BlY2hibGVuZGEvdXJ4bml1bS91cnhuaXVtLWFuZ3VsYXIvcHJvamVjdHMvbmctdXJ4bml1bS9zcmMvIiwic291cmNlcyI6WyJsaWIvc2VydmljZXMvc2lnbi1pbi13aW5kb3cuc2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxPQUFPLEVBQUUsVUFBVSxFQUFFLE1BQU0sZUFBZSxDQUFDO0FBQzNDLE9BQU8sRUFBRSxVQUFVLEVBQUUsTUFBTSxNQUFNLENBQUM7O0FBTWxDLE1BQU0sT0FBTyxtQkFBbUI7SUFFOUIsSUFBSSxDQUFDLEdBQVcsRUFBRSxLQUFrQjtRQUNsQyxPQUFPLElBQUksVUFBVSxDQUFTLFVBQVUsQ0FBQyxFQUFFO1lBQ3pDLElBQUksQ0FBQyxHQUFHLENBQUMsQ0FBQztZQUNWLE1BQU0sTUFBTSxHQUFHLE1BQU0sQ0FBQyxJQUFJLENBQ3hCLEdBQUcsRUFDSCxjQUFjLEVBQ2Q7Ozs7Ozs7O1VBUUUsS0FBSyxDQUFDLENBQUMsQ0FBQyxXQUFXLEtBQUssQ0FBQyxPQUFPLEVBQUUsQ0FBQyxDQUFDLENBQUMsRUFBRTtVQUN2QyxLQUFLLENBQUMsQ0FBQyxDQUFDLFdBQVcsS0FBSyxDQUFDLE9BQU8sRUFBRSxDQUFDLENBQUMsQ0FBQyxFQUFFLEVBQUUsQ0FDNUMsQ0FBQztZQUVGLE1BQU0sUUFBUSxHQUFHLFdBQVcsQ0FBQyxHQUFHLEVBQUU7Z0JBQzlCLElBQUk7b0JBQ0YsTUFBTSxJQUFJLEdBQUcsTUFBTSxDQUFDLFFBQVEsQ0FBQyxJQUFJLENBQUM7b0JBRWxDLElBQUksSUFBSSxLQUFLLFNBQVMsSUFBSSxJQUFJLENBQUMsUUFBUSxFQUFFLENBQUMsUUFBUSxDQUFDLE1BQU0sQ0FBQyxFQUFFO3dCQUMxRCxNQUFNLENBQUMsS0FBSyxFQUFFLENBQUM7d0JBQ2YsYUFBYSxDQUFDLFFBQVEsQ0FBQyxDQUFDO3dCQUN4QixVQUFVLENBQUMsSUFBSSxDQUFDLElBQUksQ0FBQyxLQUFLLENBQUMsUUFBUSxDQUFDLENBQUMsQ0FBQyxDQUFDLENBQUMsQ0FBQztxQkFDMUM7aUJBQ0Y7Z0JBQUMsT0FBTyxDQUFDLEVBQUUsR0FBRztnQkFFZixJQUFJLENBQUMsS0FBSyxFQUFFLEVBQUU7b0JBQ1osYUFBYSxDQUFDLFFBQVEsQ0FBQyxDQUFDO29CQUN4QixVQUFVLENBQUMsS0FBSyxFQUFFLENBQUM7aUJBQ3BCO2dCQUVELENBQUMsRUFBRSxDQUFDO1lBQ04sQ0FBQyxFQUFFLElBQUksQ0FDUixDQUFDO1FBQ0osQ0FBQyxDQUFDLENBQUM7SUFDTCxDQUFDOztzRkF4Q1UsbUJBQW1COzJEQUFuQixtQkFBbUIsV0FBbkIsbUJBQW1CLG1CQUZsQixNQUFNO2tEQUVQLG1CQUFtQjtjQUgvQixVQUFVO2VBQUM7Z0JBQ1YsVUFBVSxFQUFFLE1BQU07YUFDbkIiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBJbmplY3RhYmxlIH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XG5pbXBvcnQgeyBPYnNlcnZhYmxlIH0gZnJvbSAncnhqcyc7XG5pbXBvcnQgeyBSb3V0ZXIgfSBmcm9tICdAYW5ndWxhci9yb3V0ZXInO1xuXG5ASW5qZWN0YWJsZSh7XG4gIHByb3ZpZGVkSW46ICdyb290J1xufSlcbmV4cG9ydCBjbGFzcyBTaWduSW5XaW5kb3dTZXJ2aWNlIHtcblxuICBvcGVuKHVybDogc3RyaW5nLCBldmVudD86IE1vdXNlRXZlbnQpOiBPYnNlcnZhYmxlPHN0cmluZz4ge1xuICAgIHJldHVybiBuZXcgT2JzZXJ2YWJsZTxzdHJpbmc+KG9ic2VydmFibGUgPT4ge1xuICAgICAgbGV0IGkgPSAwO1xuICAgICAgY29uc3QgZGlhbG9nID0gd2luZG93Lm9wZW4oXG4gICAgICAgIHVybCxcbiAgICAgICAgJ3RhcmdldFdpbmRvdycsXG4gICAgICAgIGB0b29sYmFyPW5vLFxuICAgICAgICBsb2NhdGlvbj1jZW50ZXIsXG4gICAgICAgIHN0YXR1cz1ubyxcbiAgICAgICAgbWVudWJhcj1ubyxcbiAgICAgICAgc2Nyb2xsYmFycz1ubyxcbiAgICAgICAgcmVzaXphYmxlPW5vLFxuICAgICAgICB3aWR0aD00MDAsXG4gICAgICAgIGhlaWdodD02MDAsXG4gICAgICAgICR7ZXZlbnQgPyBgc2NyZWVuWD0ke2V2ZW50LnNjcmVlblh9YCA6ICcnfSxcbiAgICAgICAgJHtldmVudCA/IGBzY3JlZW5ZPSR7ZXZlbnQuc2NyZWVuWX1gIDogJyd9YFxuICAgICAgKTtcblxuICAgICAgY29uc3QgaW50ZXJ2YWwgPSBzZXRJbnRlcnZhbCgoKSA9PiB7XG4gICAgICAgICAgdHJ5IHtcbiAgICAgICAgICAgIGNvbnN0IGhyZWYgPSBkaWFsb2cubG9jYXRpb24uaHJlZjtcblxuICAgICAgICAgICAgaWYgKGhyZWYgIT09IHVuZGVmaW5lZCAmJiBocmVmLnRvU3RyaW5nKCkuaW5jbHVkZXMoJ2h0dHAnKSkge1xuICAgICAgICAgICAgICBkaWFsb2cuY2xvc2UoKTtcbiAgICAgICAgICAgICAgY2xlYXJJbnRlcnZhbChpbnRlcnZhbCk7XG4gICAgICAgICAgICAgIG9ic2VydmFibGUubmV4dChocmVmLnNwbGl0KCc/Y29kZT0nKVsxXSk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgfSBjYXRjaCAoZSkgeyB9XG5cbiAgICAgICAgICBpZiAoaSA9PT0gMTApIHtcbiAgICAgICAgICAgIGNsZWFySW50ZXJ2YWwoaW50ZXJ2YWwpO1xuICAgICAgICAgICAgb2JzZXJ2YWJsZS5lcnJvcigpO1xuICAgICAgICAgIH1cblxuICAgICAgICAgIGkrKztcbiAgICAgICAgfSwgMTAwMFxuICAgICAgKTtcbiAgICB9KTtcbiAgfVxuXG59XG4iXX0=
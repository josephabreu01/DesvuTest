import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Notification {
    message: string;
    type: 'success' | 'error';
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
    private notificationSubject = new BehaviorSubject<Notification | null>(null);

    get notification$(): Observable<Notification | null> {
        return this.notificationSubject.asObservable();
    }

    showSuccess(message: string): void {
        this.notificationSubject.next({ message, type: 'success' });
        this.clearAfterDelay();
    }

    showError(message: string): void {
        this.notificationSubject.next({ message, type: 'error' });
        this.clearAfterDelay();
    }

    private clearAfterDelay(): void {
        setTimeout(() => this.notificationSubject.next(null), 5000);
    }
}

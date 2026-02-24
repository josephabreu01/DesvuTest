import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface ModalConfig {
    title: string;
    primaryButtonLabel?: string;
    secondaryButtonLabel?: string;
    showFooter?: boolean;
    maxWidth?: string;
}

@Component({
    selector: 'app-modal',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './modal.component.html',
    styleUrl: './modal.component.css'
})
export class ModalComponent {
    @Input() show = false;
    @Input() loading = false;
    @Input() config: ModalConfig = {
        title: '',
        primaryButtonLabel: 'Guardar',
        secondaryButtonLabel: 'Cancelar',
        showFooter: true,
        maxWidth: '520px'
    };

    @Output() close = new EventEmitter<void>();
    @Output() submit = new EventEmitter<void>();

    onClose(): void {
        this.close.emit();
    }

    onSubmit(): void {
        this.submit.emit();
    }

    onOverlayClick(event: MouseEvent): void {
        if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
            this.onClose();
        }
    }
}

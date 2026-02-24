import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Reporte } from '../../core/models/reporte.model';
import { ReporteService } from '../../core/services/reporte.service';
import { FileService } from '../../core/services/file.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
    selector: 'app-reportes',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './reportes.component.html',
    styleUrl: './reportes.component.css'
})
export class ReportesComponent {
    private fb = inject(FormBuilder);
    private reporteService = inject(ReporteService);
    private fileService = inject(FileService);
    private notificationService = inject(NotificationService);

    reportForm = this.fb.nonNullable.group({
        clienteId: [''],
        nombreCliente: [''],
        fechaInicio: ['', [Validators.required]],
        fechaFin: ['', [Validators.required]]
    });

    reportes: Reporte[] = [];
    loading = false;
    searched = false;

    notification$ = this.notificationService.notification$;

    search(): void {
        const { clienteId, nombreCliente, fechaInicio, fechaFin } = this.reportForm.getRawValue();

        if (!clienteId && !nombreCliente) {
            this.notificationService.showError('Debe proporcionar un ID de cliente o un Nombre');
            return;
        }

        if (this.reportForm.invalid) {
            this.reportForm.markAllAsTouched();
            return;
        }

        this.loading = true;

        this.reporteService.getReporte(clienteId, nombreCliente, fechaInicio, fechaFin).subscribe({
            next: (data) => {
                this.reportes = data;
                this.loading = false;
                this.searched = true;
            },
            error: (err) => {
                const msg = err.error?.message || 'Error al generar reporte';
                this.notificationService.showError(msg);
                this.reportes = [];
                this.loading = false;
                this.searched = true;
            }
        });
    }

    exportPdf(): void {
        const { clienteId, nombreCliente, fechaInicio, fechaFin } = this.reportForm.getRawValue();

        if (!clienteId && !nombreCliente) {
            this.notificationService.showError('Debe proporcionar un ID de cliente o un Nombre');
            return;
        }

        if (this.reportForm.invalid) {
            this.reportForm.markAllAsTouched();
            return;
        }

        this.loading = true;
        const filename = clienteId ? `Estado_Cuenta_${clienteId}.pdf` : `Estado_Cuenta_${nombreCliente}.pdf`;

        this.reporteService.getReportePdf(clienteId, nombreCliente, fechaInicio, fechaFin).subscribe({
            next: (base64) => {
                this.fileService.downloadBase64Pdf(base64, filename);
                this.loading = false;
            },
            error: (err) => {
                this.notificationService.showError('Error al exportar PDF');
                this.loading = false;
            }
        });
    }

    get totalCreditos(): number {
        return this.reportes.reduce((total, r) =>
            total + (r.movimientos || [])
                .filter(m => Number(m.valor) > 0)
                .reduce((s, m) => s + Number(m.valor), 0), 0);
    }

    get totalDebitos(): number {
        const sum = this.reportes.reduce((total, r) =>
            total + (r.movimientos || [])
                .filter(m => Number(m.valor) < 0)
                .reduce((s, m) => s + Number(m.valor), 0), 0);
        return Math.abs(sum);
    }
}

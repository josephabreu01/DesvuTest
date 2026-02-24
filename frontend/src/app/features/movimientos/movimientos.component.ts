import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, Validators } from '@angular/forms';
import { Movimiento, MovimientoRequest, TipoMovimiento } from '../../core/models/movimiento.model';
import { Cuenta } from '../../core/models/cuenta.model';
import { MovimientoService } from '../../core/services/movimiento.service';
import { CuentaService } from '../../core/services/cuenta.service';
import { NotificationService } from '../../core/services/notification.service';
import { TableComponent, TableColumn } from '../../shared/components/table/table.component';
import { ModalComponent, ModalConfig } from '../../shared/components/modal/modal.component';

@Component({
    selector: 'app-movimientos',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, FormsModule, TableComponent, ModalComponent],
    templateUrl: './movimientos.component.html',
    styleUrl: './movimientos.component.css'
})
export class MovimientosComponent implements OnInit {
    private fb = inject(FormBuilder);
    private movimientoService = inject(MovimientoService);
    private cuentaService = inject(CuentaService);
    private notificationService = inject(NotificationService);

    movimientoForm = this.fb.nonNullable.group({
        tipoMovimiento: ['', [Validators.required]],
        valor: [0, [Validators.required, Validators.min(0.01)]],
        cuentaId: [0, [Validators.required, Validators.min(1)]]
    });

    movimientos: Movimiento[] = [];
    filteredMovimientos: Movimiento[] = [];
    cuentas: Cuenta[] = [];
    searchTerm = '';
    showModal = false;
    loading = false;
    modalConfig: ModalConfig = {
        title: 'Nuevo Movimiento',
        primaryButtonLabel: 'Registrar',
        secondaryButtonLabel: 'Cancelar',
        showFooter: true
    };

    tableColumns: TableColumn[] = [
        { key: 'numeroCuenta', label: 'Numero Cuenta' },
        { key: 'tipoCuenta', label: 'Tipo' },
        { key: 'saldoInicial', label: 'Saldo Inicial', template: 'initialBalance' },
        { key: 'estadoCuenta', label: 'Estado', template: 'status' },
        { key: 'valor', label: 'Movimiento', template: 'movement' },
        { key: 'acciones', label: 'Acciones', template: 'actions' }
    ];

    tiposMovimiento: TipoMovimiento[] = [];

    notification$ = this.notificationService.notification$;

    ngOnInit(): void {
        this.loadData();
    }

    loadData(): void {
        this.loading = true;
        this.movimientoService.getAll().subscribe({
            next: (data) => {
                this.movimientos = data;
                this.filteredMovimientos = data;
                this.loading = false;
            },
            error: (err) => {
                this.notificationService.showError(err.error?.message || 'Error al cargar movimientos');
                this.loading = false;
            }
        });
        this.cuentaService.getAll().subscribe({
            next: (data) => this.cuentas = data.filter(c => c.estado),
            error: () => this.notificationService.showError('Error al cargar cuentas')
        });
        this.movimientoService.getTipos().subscribe({
            next: (data) => this.tiposMovimiento = data,
            error: () => this.notificationService.showError('Error al cargar tipos de movimiento')
        });
    }

    search(): void {
        const term = this.searchTerm.toLowerCase().trim();
        this.filteredMovimientos = term
            ? this.movimientos.filter(m =>
                m.tipoCuenta?.toLowerCase().includes(term) ||
                m.tipoMovimiento.toLowerCase().includes(term) ||
                m.numeroCuenta?.toLowerCase().includes(term))
            : [...this.movimientos];
    }

    openCreate(): void {
        this.movimientoForm.reset({ valor: 0 });
        this.showModal = true;
    }

    closeModal(): void {
        this.showModal = false;
    }

    save(): void {
        if (this.movimientoForm.invalid) {
            this.movimientoForm.markAllAsTouched();
            return;
        }

        this.loading = true;
        const formData = this.movimientoForm.getRawValue() as MovimientoRequest;
        this.movimientoService.create(formData).subscribe({
            next: () => {
                this.notificationService.showSuccess('Movimiento registrado correctamente');
                this.showModal = false;
                this.loadData();
            },
            error: (err) => {
                this.notificationService.showError(err.error?.message || 'Error al registrar movimiento');
                this.loading = false;
            }
        });
    }

    delete(id: number): void {
        if (!confirm('¿Estás seguro que deseas eliminar este movimiento?')) return;
        this.movimientoService.delete(id).subscribe({
            next: () => {
                this.notificationService.showSuccess('Movimiento eliminado correctamente');
                this.loadData();
            },
            error: (err) => {
                this.notificationService.showError(err.error?.message || 'Error al eliminar movimiento');
            }
        });
    }
}

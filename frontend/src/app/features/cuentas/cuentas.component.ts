import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, Validators } from '@angular/forms';
import { Cuenta } from '../../core/models/cuenta.model';
import { Cliente } from '../../core/models/cliente.model';
import { CuentaService } from '../../core/services/cuenta.service';
import { ClienteService } from '../../core/services/cliente.service';
import { NotificationService } from '../../core/services/notification.service';
import { TableComponent, TableColumn } from '../../shared/components/table/table.component';
import { ModalComponent, ModalConfig } from '../../shared/components/modal/modal.component';

@Component({
    selector: 'app-cuentas',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, FormsModule, TableComponent, ModalComponent],
    templateUrl: './cuentas.component.html',
    styleUrl: './cuentas.component.css'
})
export class CuentasComponent implements OnInit {
    private fb = inject(FormBuilder);
    private cuentaService = inject(CuentaService);
    private clienteService = inject(ClienteService);
    private notificationService = inject(NotificationService);

    cuentaForm = this.fb.nonNullable.group({
        tipoCuenta: ['', [Validators.required]],
        saldoInicial: [0, [Validators.required, Validators.min(0)]],
        clienteId: [0, [Validators.required, Validators.min(1)]],
        estado: [true]
    });

    cuentas: Cuenta[] = [];
    filteredCuentas: Cuenta[] = [];
    clientes: Cliente[] = [];
    searchTerm = '';
    showModal = false;
    isEditing = false;
    loading = false;
    modalConfig: ModalConfig = {
        title: 'Nueva Cuenta',
        primaryButtonLabel: 'Crear',
        secondaryButtonLabel: 'Cancelar',
        showFooter: true
    };
    selectedId: number | null = null;

    tableColumns: TableColumn[] = [
        { key: 'numeroCuenta', label: 'Número Cuenta' },
        { key: 'tipoCuenta', label: 'Tipo' },
        { key: 'saldoInicial', label: 'Saldo Inicial', template: 'currency' },
        { key: 'clienteNombre', label: 'Cliente' },
        { key: 'estado', label: 'Estado', template: 'status' },
        { key: 'acciones', label: 'Acciones', template: 'actions' }
    ];

    tiposCuenta = ['Ahorro', 'Corriente'];

    notification$ = this.notificationService.notification$;

    ngOnInit(): void {
        this.loadData();
    }

    loadData(): void {
        this.loading = true;
        this.cuentaService.getAll().subscribe({
            next: (data) => {
                this.cuentas = data;
                this.filteredCuentas = data;
                this.loading = false;
            },
            error: (err) => {
                this.notificationService.showError(err.error?.message || 'Error al cargar cuentas');
                this.loading = false;
            }
        });
        this.clienteService.getAll().subscribe({
            next: (data) => this.clientes = data,
            error: () => this.notificationService.showError('Error al cargar clientes')
        });
    }

    search(): void {
        const term = this.searchTerm.toLowerCase().trim();
        this.filteredCuentas = term
            ? this.cuentas.filter(c =>
                c.numeroCuenta.toLowerCase().includes(term) ||
                c.tipoCuenta.toLowerCase().includes(term) ||
                c.clienteNombre?.toLowerCase().includes(term))
            : [...this.cuentas];
    }

    openCreate(): void {
        this.cuentaForm.reset({ saldoInicial: 0, estado: true });
        this.isEditing = false;
        this.selectedId = null;
        this.showModal = true;
    }

    openEdit(cuenta: Cuenta): void {
        this.cuentaForm.patchValue({
            tipoCuenta: cuenta.tipoCuenta,
            saldoInicial: cuenta.saldoInicial,
            estado: cuenta.estado,
            clienteId: cuenta.clienteId
        });
        this.isEditing = true;
        this.selectedId = cuenta.id!;
        this.showModal = true;
    }

    closeModal(): void {
        this.showModal = false;
    }

    save(): void {
        if (this.cuentaForm.invalid) {
            this.cuentaForm.markAllAsTouched();
            return;
        }

        this.loading = true;
        const formData = this.cuentaForm.getRawValue();
        const request = this.isEditing
            ? this.cuentaService.update(this.selectedId!, formData)
            : this.cuentaService.create(formData);

        request.subscribe({
            next: () => {
                this.notificationService.showSuccess(this.isEditing ? 'Cuenta actualizada correctamente' : 'Cuenta creada correctamente');
                this.showModal = false;
                this.loadData();
            },
            error: (err) => {
                this.notificationService.showError(err.error?.message || 'Error al guardar cuenta');
                this.loading = false;
            }
        });
    }

    delete(id: number): void {
        if (!confirm('¿Estás seguro que deseas eliminar esta cuenta?')) return;
        this.cuentaService.delete(id).subscribe({
            next: () => {
                this.notificationService.showSuccess('Cuenta eliminada correctamente');
                this.loadData();
            },
            error: (err) => {
                this.notificationService.showError(err.error?.message || 'Error al eliminar cuenta');
            }
        });
    }
}
